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
	public boolean tokenReleaseble=true;
	public int lastTry = 0;
	public TokenHolder tokenWaiter;
	public BlockingQueue<Envelope> messageToBeSent;

	public Stack<JoinRingMessage> waitingList;
	public TokenHolder tokenReleaser;
	public TokenManager(PeerManager p) {
		pm = p;
		waitingList = new Stack<JoinRingMessage>();
		messageToBeSent=new LinkedBlockingQueue<Envelope>();
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

		System.out.println("Sono "+pm.main.me+
		 " e ricevo token con counter "+t.counter);
		
		sendTokenWaitingMessages();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TokenMessage newToken = new TokenMessage();
		newToken.counter = t.counter + 1;
	
		try {
			while(!tokenReleaseble){
			
					tokenReleaser.wait();
					
				} 
				pm.send(newToken, next.player);
				 
			}
		catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
	} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

	}

	public void sendTokenWaitingMessages(){
		//rimuovo e invio un solo messaggio per token per garantire fairness nel move
		Envelope e=messageToBeSent.poll();
		if(e!=null)
		pm.sendWithAck(e);

	}
	
	public synchronized void onJoinRingMessageReceived(JoinRingMessage jrm) {
		JoinRingAckMessage reply = new JoinRingAckMessage();

		if (!inRing) {
			reply.found = false;
			//System.out.println("Non sono ancora nel ring e sono "
				//	+ pm.main.me.getPort());
		} else {
			if (someoneEntering) {
				//System.out.println("Sono nel ring e sono " + pm.main.me.getPort()+"Metto in coda "+jrm.sender.getPort());
					
				waitingList.push(jrm);
				return;
			}

			//System.out.println("Sono nel ring e sono " + pm.main.me.getPort()+"Rispondo a "+jrm.sender.getPort());
			
			someoneEntering = true;
			Peer p;
			try {
				p = pm.addToPeerList(jrm.sender);

				reply.sender = pm.main.me;
				reply.found = true;
				reply.newPrev = prev.player;

				setPrev(p);
			} catch (JAXBException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}

		try {
			pm.send(reply, jrm.sender);
		} catch (IOException | JAXBException |NullPointerException e ) {
		
		System.out.println("Porta= "+jrm.sender.getPort());
			e.printStackTrace();
		}

	}

	public void addToMessageToBeSentQueue(Message m, int port){
		try {
			messageToBeSent.add(new Envelope(m,pm.connectionList.get(port).getOutput()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void exitRing() {
		ExitRingSetNextMessage n= new ExitRingSetNextMessage();
		n.newNext=next.player;

		ExitRingSetPrevMessage p= new ExitRingSetPrevMessage();
		p.newPrev=prev.player;
		
			addToMessageToBeSentQueue(n,prev.player.getPort());

			addToMessageToBeSentQueue(p,next.player.getPort());
		

	}

	public void joinRing() throws IOException, JAXBException {

		if (pm.connectionList.size() < 2) {
			inRing = true;
			next = pm.connectionList.get(pm.main.me.getPort());
			setPrev(next);

			TokenMessage newToken = new TokenMessage();
			newToken.counter = 0;
			newToken.iddo = new Random().nextInt() % 100;
			pm.send(newToken, pm.main.me);

		} else {
			Peer target = (Peer) pm.connectionList.values().toArray()[lastTry++
					% pm.connectionList.values().size()];

			JoinRingMessage jrm = new JoinRingMessage();
			jrm.sender = pm.main.me;
			pm.send(jrm, target.player);
			next = target;
		}

	}


	public void onJoinRingAckMessageReceived(
			JoinRingAckMessage joinRingReplyMessage) {
		//System.out.println("RwaitForAckicevo ack e sono "	+ pm.main.me.getPort());

		if (!joinRingReplyMessage.found) {
			try {
				joinRing();
			} catch (IOException | JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			setPrev(pm.connectionList.get(joinRingReplyMessage.newPrev
					.getPort()));
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
		//System.out.println("Ricevo jointoprev e sono "+ pm.main.me.getPort());

		Player oldNext = next.player;
		try {
			next = pm.addToPeerList(smn.sender);
		} catch (JAXBException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		setPrev(pm.connectionList.get(exitRingSetPrevMessage.newPrev.getPort()));

	}

	public void onExitRingSetNextMessageReceived(
			ExitRingSetNextMessage exitRingSetNextMessage) {
		next = pm.connectionList.get(exitRingSetNextMessage.newNext.getPort());

	}

	public void onJoinUnlockMessageReceived(JoinUnlockMessage joinUnlockMessage) {
		someoneEntering = false;
	//	System.out.println("Ricevo unlock e sono "
		//		+ pm.main.me.getPort());

		if (waitingList.size() > 0) {
			JoinRingMessage jrm = waitingList.pop();
			onJoinRingMessageReceived(jrm);

		}

	}

}
