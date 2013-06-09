package distributed;

import java.io.IOException;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.bind.JAXBException;

import common.Player;
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
	//Previous node in the ring
	public Peer prev;

	//Next node in the ring
	public Peer next;
	public PeerManager pm;
	
	//Traccia se sto già gestendo l'entrata di un peer
	public boolean someoneEntering = false;
	//Traccia se sono effettivamente nel ring o sto entrando
	public boolean inRing = false;
	//Traccia se sto bloccando il token
	public boolean tokenBlocked = false;
	
	//Lista di nodi in attesa di entrare nel ring dietro di me
	public Stack<JoinRingMessage> waitingList;
	//Ultimo target scelto per agganciarmi al ring
	public int lastTry = 0;
	
	//Oggetto a cui accodarsi per aspettare il token
	public Object tokenWaiter;
	//Messaggi da inviare all'arrivo del token
	public BlockingQueue<Envelope> messageToBeSent;
	
	
	public TokenManager(PeerManager p) {
		pm = p;
		waitingList = new Stack<JoinRingMessage>();
		messageToBeSent = new LinkedBlockingQueue<Envelope>();
		tokenWaiter = new Object();
	}



	public void onTokenReceived(TokenMessage t) throws IOException {
		//Dico che è arrivato il token
		synchronized (tokenWaiter) {
			tokenWaiter.notifyAll();

		}
		/*System.out.println("Sono " + pm.main.me.getPort()
				+ " e ricevo token con counter " + t.counter);*/
		// System.out.println("Il mio next è "+next.player.getPort()+" il mio prev è "+prev.player.getPort());

		//creo il nuovo token
		TokenMessage newToken = new TokenMessage();
		newToken.counter = t.counter + 1;
		try {
			
			//Aspetto finché l'invio del token non viene sbloccato
			waitOnToken();
			//Mando i messaggi in attesa di sincronizzazione col token
			sendTokenWaitingMessages();
			//Faccio proseguire il token
			pm.send(newToken, next.player);

		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public synchronized void waitOnToken() {
		if (tokenBlocked) {
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
		ExitRingSetNextMessage n = new ExitRingSetNextMessage();
		n.newNext = next.player;
		n.sender = pm.main.me;
		ExitRingSetPrevMessage p = new ExitRingSetPrevMessage();
		p.newPrev = prev.player;
		p.sender = pm.main.me;
		pm.sendWithAck(new Envelope(n, pm.connectionList.get(prev.player
				.getPort())));
		System.out.println("Ricevo primo ack");
		pm.sendWithAck(new Envelope(p, pm.connectionList.get(next.player
				.getPort())));
		System.out.println("Sono uscito dal ring");

	}
	
	public void joinRing() throws IOException, JAXBException {
		//Caso in cui sono solo
		if (pm.connectionList.size() < 2) {
			inRing = true;
			next = pm.connectionList.get(pm.main.me.getPort());
			prev = next;

			TokenMessage newToken = new TokenMessage();
			newToken.counter = 0;
			pm.send(newToken, pm.main.me);

		} 
		//caso in cui non sono  solo
		else {
			//ad ogni tentativo provo un peer diverso a cui connettermi
			Peer target = (Peer) pm.connectionList.values().toArray()[lastTry++
					% pm.connectionList.values().size()];
			//mando un messaggio di richiesta d'entrata nel ring
			JoinRingMessage jrm = new JoinRingMessage();
			jrm.sender = pm.main.me;
			pm.send(jrm, target.player);
			next = target;
		}

	}
	
	public synchronized void onJoinRingMessageReceived(JoinRingMessage jrm) {
		JoinRingAckMessage reply = new JoinRingAckMessage();
		// System.out.println("Sono"+
		// pm.main.me.getPort()+" e ricevo JoinRingMessage");
		
		//Se non sono ancora nel ring, rifiuto l'entrata e il peer proverà ad entrare con qualcun altro
		if (!inRing) {
			reply.found = false;
			// System.out.println("Non sono ancora nel ring e sono "
			// + pm.main.me.getPort());
		} else {
		//Se qualcuno sta già entrando, metto il nuovo arrivato in coda
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

			prev = p;

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
		
		//Se non ho trovato un target valido, riprovo
		if (!joinRingReplyMessage.found) {
			try {
				joinRing();
			} catch (IOException | JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		//altrimenti mi presento al mio nuovo prev.
		else {
			prev = pm.connectionList
					.get(joinRingReplyMessage.newPrev.getPort());
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

		//aggiorno il mio next
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
	public void onJoinUnlockMessageReceived(JoinUnlockMessage joinUnlockMessage) {
		someoneEntering = false;
		// System.out.println("Ricevo unlock e sono "
		// + pm.main.me.getPort());

		if (waitingList.size() > 0) {
			JoinRingMessage jrm = waitingList.pop();
			onJoinRingMessageReceived(jrm);

		}

	}
	public void onExitRingSetPrevMessageReceived(

	ExitRingSetPrevMessage exitRingSetPrevMessage) {
		System.out.println("Sono " + pm.main.me.getPort()
				+ "e ricevo SetPrev per "
				+ exitRingSetPrevMessage.newPrev.getPort());
		prev=pm.connectionList.get(exitRingSetPrevMessage.newPrev.getPort());
		pm.sendAck(exitRingSetPrevMessage.sender);
	}

	public void onExitRingSetNextMessageReceived(
			ExitRingSetNextMessage exitRingSetNextMessage) {
		System.out.println("Sono " + pm.main.me.getPort()
				+ "e ricevo SetNext per "
				+ exitRingSetNextMessage.newNext.getPort());
		next = pm.connectionList.get(exitRingSetNextMessage.newNext.getPort());
		pm.sendAck(exitRingSetNextMessage.sender);
	}



}
