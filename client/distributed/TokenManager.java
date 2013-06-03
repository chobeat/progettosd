package distributed;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.bind.JAXBException;

import common.Player;
import communication.BroadcastEnvelope;
import communication.DummyBroadCastMessage;
import communication.Envelope;
import communication.ExitRingSetNextMessage;
import communication.ExitRingSetPrevMessage;
import communication.JoinRingAckMessage;
import communication.JoinRingMessage;
import communication.JoinToPrevMessage;
import communication.JoinUnlockMessage;
import communication.Message;
import communication.TokenMessage;

public class TokenManager {

	private Peer prev;
	public Peer next;
	public PeerManager pm;
	public boolean someoneEntering = false;
	public boolean inRing = false;
	public boolean tokenBlocked = false;
	public int lastTry = 0;
	public Object tokenWaiter;
	public BlockingQueue<Envelope> messageToBeSent;

	public Stack<JoinRingMessage> waitingList;

	public TokenManager(PeerManager p) {
		pm = p;
		waitingList = new Stack<JoinRingMessage>();
		messageToBeSent = new LinkedBlockingQueue<Envelope>();
		tokenWaiter = new Object();
	}

	public void setPrev(Peer p) {
		if (p == null) {
			System.err.println("Prev è uguale a null");
			System.err.println("Sono " + pm.main.me);
			new Throwable().printStackTrace();

		}
		prev = p;

	}

	public void onTokenReceived(TokenMessage t) throws IOException {
		
		synchronized (tokenWaiter) {
			tokenWaiter.notifyAll();

		}
		System.out.println("Sono " + pm.main.me.getPort()
				+ " e ricevo token con counter " + t.counter);
		//System.out.println("Il mio next è "+next.player.getPort()+" il mio prev è "+prev.player.getPort());
		
		sendTokenWaitingMessages();
		TokenMessage newToken = new TokenMessage();
		newToken.counter = t.counter + 1;
		try {
			waitOnToken();

			pm.send(newToken, next.player);

		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public synchronized void waitOnToken() {
		if(tokenBlocked){
		try {
			this.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	public synchronized void blockToken() {

		tokenBlocked = true;
	}

	public synchronized void releaseToken() {
		tokenBlocked = false;
		this.notifyAll();

	}

	public void sendTokenWaitingMessages() {
		// rimuovo e invio un solo messaggio per token per garantire fairness
		// nel move
		Envelope e = messageToBeSent.poll();
		if (e != null)
			pm.sendWithAck(e);

	}

	public void addToMessageToBeSentQueue(Message m, int port) {
		try {
			messageToBeSent.add(new Envelope(m, pm.connectionList.get(port)
					.getOutput()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void exitRing() {
		System.out.println("Sono " + pm.main.me.getPort()
				+ " e voglio uscire dal ring");
		
		System.out.println("Invio next a "+next.player.getPort()+"e prev a "+prev.player.getPort());
		ExitRingSetNextMessage n = new ExitRingSetNextMessage();
		n.newNext = next.player;
		n.sender=pm.main.me;
		ExitRingSetPrevMessage p = new ExitRingSetPrevMessage();
		p.newPrev = prev.player;
		p.sender=pm.main.me;
		pm.sendWithAck(new Envelope(n,pm.connectionList.get(prev.player.getPort())));
		try{
		pm.sendWithAck(new Envelope(p,pm.connectionList.get(next.player.getPort())));
		}catch (NullPointerException e){
			e.printStackTrace();
			System.out.println("Sono "+pm.main.me.getPort()+" Il mio next è"+next.player);
		}	
		

	}

	public void joinRing() throws IOException, JAXBException {

		if (pm.connectionList.size() < 2) {
			System.out.println("Ballo da solo");
			inRing = true;
			next = pm.connectionList.get(pm.main.me.getPort());
			prev=next;

			TokenMessage newToken = new TokenMessage();
			newToken.counter = 0;
			pm.send(newToken, pm.main.me);

		} else {

			System.out.println("Ballo in compagnia");
			Peer target = (Peer) pm.connectionList.values().toArray()[lastTry++
					% pm.connectionList.values().size()];

			JoinRingMessage jrm = new JoinRingMessage();
			jrm.sender = pm.main.me;
			pm.send(jrm, target.player);
			next = target;
		}

	}


	public synchronized void onJoinRingMessageReceived(JoinRingMessage jrm) {
		JoinRingAckMessage reply = new JoinRingAckMessage();
		System.out.println("Sono"+ pm.main.me.getPort()+" e ricevo JoinRingMessage");
		if (!inRing) {
			reply.found = false;
			// System.out.println("Non sono ancora nel ring e sono "
			// + pm.main.me.getPort());
		} else {
			if (someoneEntering) {
				// System.out.println("Sono nel ring e sono " +
				// pm.main.me.getPort()+"Metto in coda "+jrm.sender.getPort());

				waitingList.push(jrm);
				return;
			}

			// System.out.println("Sono nel ring e sono " +
			// pm.main.me.getPort()+"Rispondo a "+jrm.sender.getPort());

			someoneEntering = true;
			Peer p;
				p = pm.connectionList.get(jrm.sender.getPort());
				reply.sender = pm.main.me;
				reply.found = true;
				reply.newPrev = prev.player;

				prev=p;
			
		}

		try {
			pm.send(reply, jrm.sender);
		} catch (IOException | JAXBException | NullPointerException e) {

			System.out.println("Porta= " + jrm.sender.getPort());
			e.printStackTrace();
		}

	}

	
	public void onJoinRingAckMessageReceived(
			JoinRingAckMessage joinRingReplyMessage) {
		// System.out.println("RwaitForAckicevo ack e sono " +
		// pm.main.me.getPort());

		if (!joinRingReplyMessage.found) {
			try {
				joinRing();
			} catch (IOException | JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			prev=pm.connectionList.get(joinRingReplyMessage.newPrev
					.getPort());
			JoinToPrevMessage smn = new JoinToPrevMessage();
			smn.sender = pm.main.me;
			inRing = true;
			try {
				pm.send(smn, prev.player);
			} catch (IOException | JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void onJoinToPrevMessageReceived(JoinToPrevMessage smn) {
		// System.out.println("Ricevo jointoprev e sono "+
		// pm.main.me.getPort());

		Player oldNext = next.player;
		next = pm.connectionList.get(smn.sender.getPort());
		
		JoinUnlockMessage jtpa = new JoinUnlockMessage();
		jtpa.sender = pm.main.me;
		try {
			pm.send(jtpa, oldNext);
		} catch (IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onExitRingSetPrevMessageReceived(
			
			ExitRingSetPrevMessage exitRingSetPrevMessage) {
	//System.out.println("Sono "+pm.main.me.getPort()+"e ricevo SetPrev per "+exitRingSetPrevMessage.newPrev.getPort());
		setPrev(pm.connectionList.get(exitRingSetPrevMessage.newPrev.getPort()));
		pm.sendAck(exitRingSetPrevMessage.sender);
	}

	public void onExitRingSetNextMessageReceived(
			ExitRingSetNextMessage exitRingSetNextMessage) {
	//	System.out.println("Sono "+pm.main.me.getPort()+"e ricevo SetNext per "+exitRingSetNextMessage.newNext.getPort());
		next = pm.connectionList.get(exitRingSetNextMessage.newNext.getPort());
		pm.sendAck(exitRingSetNextMessage.sender);
	}

	public void onJoinUnlockMessageReceived(JoinUnlockMessage joinUnlockMessage) {
		someoneEntering = false;
		// System.out.println("Ricevo unlock e sono "
		// + pm.main.me.getPort());

		if (waitingList.size() > 0) {
			JoinRingMessage jrm = waitingList.pop();
			onJoinRingMessageReceived(jrm);

		}

	}

}
