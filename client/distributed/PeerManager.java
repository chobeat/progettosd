package distributed;

import game.Game;
import game.Position;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import client.Main;
import common.Player;
import communication.AckMessage;
import communication.AddMeToYourListMessage;
import communication.BroadcastEnvelope;
import communication.BroadcastMoveEnvelope;
import communication.DummyBroadCastMessage;
import communication.Envelope;
import communication.ExitRingSetNextMessage;
import communication.ExitRingSetPrevMessage;
import communication.Message;
import communication.MoveAck;
import communication.MoveMessage;
import communication.RemoveFromYourListMessage;
import communication.VictoryMessage;

public class PeerManager {
	public ConcurrentHashMap<Integer, Peer> connectionList;
	ListenDispatcher listener;
	final int DISPATCH_THREADS = 2;
	public Main main;
	public MessageDispatcher md;
	public TokenManager tm;
	public BlockingQueue<Message> inboundMessageQueue;
	public BlockingQueue<AckMessage> AckQueue;
	public BlockingQueue<MoveAck>MoveAckQueue;
	public Game game;
	Integer sendQueueWaiter;

	public PeerManager(Main m, Player me, List<Player> pl) {
		sendQueueWaiter = 0;
		inboundMessageQueue = new LinkedBlockingQueue<Message>();
		AckQueue = new LinkedBlockingQueue<AckMessage>();
		MoveAckQueue = new LinkedBlockingQueue<MoveAck>();

		main = m;

		connectionList = new ConcurrentHashMap<Integer, Peer>();
		final List<Player> localList = pl;
		tm = new TokenManager(this);

		listener = new ListenDispatcher(this);
		listener.start();

		for (Player p : localList) {
			try {
				addToPeerList(p);
			} catch (IOException e) {
				System.err.println("Give up on connection");

			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		InboundWorker iw = new InboundWorker(this);
		iw.start();
		md = new MessageDispatcher();
		md.init(DISPATCH_THREADS, this);

	}

	public void startMatch() {
		
			game = new Game(this);
			
			AddMeToYourListMessage m = new AddMeToYourListMessage();
			m.sender = main.me;
			MoveMessage mm=new MoveMessage();
			mm.sender=main.me;
			mm.direction=-3;
				sendAllWithMoveAckAtToken(mm);
			
			
			try {
				tm.joinRing();
			} catch (IOException | JAXBException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		sendWithAck(new BroadcastEnvelope(m));
			


	}

	public void handleMessage(String s) throws InterruptedException {
		Message m;
	
		try {
	
			m = CustomMarshaller.getCustomMarshaller().unmarshal(s);
			if (m == null) {
				return;
			}
			if (m instanceof AckMessage) {
				synchronized (AckQueue) {
					AckQueue.put((AckMessage) m);
					AckQueue.notify();
				}
			} else if (m instanceof MoveAck) {
				synchronized (MoveAckQueue) {
					MoveAckQueue.put((MoveAck) m);
					MoveAckQueue.notify();
				}
			} else {
				inboundMessageQueue.put(m);
			}
	
		} catch (ClassNotFoundException | DOMException | JAXBException
				| ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public int sendAllExceptMe(Message m) {

		m.sender = main.me;
		BroadcastEnvelope be = new BroadcastEnvelope(m);
		md.enqueue(be);
		return connectionList.size() - 1;
	}

	public int sendAllWithMoveAckAtToken(Message m) {
		tm.messageToBeSent.add(new BroadcastMoveEnvelope(m));
		return connectionList.size() - 1;
	}

	public void send(Message m, Player player) throws IOException,
			JAXBException {
		int port = player.getPort();
		Peer p = connectionList.get(port);

		if (p == null) {
			try{
			p = addToPeerList(player);
			}catch(IOException e){
				
			return;	
			}
			
			}
		Socket s = p.getSocket();

		if (s == null) {
			System.out.println("Giocatore" + port + " non presente");

			System.exit(0);
		}
		m.sender = main.me;

		send(m, new DataOutputStream(s.getOutputStream()));
	}

	public void send(Message m, DataOutputStream out) throws IOException,
			JAXBException {
		try {
			md.enqueue(m, out);
		} catch (NullPointerException e) {
			System.out.println("Sono " + main.me.getPort()
					+ " fallisco mandando " + m.getClass().getName());
			e.printStackTrace();

		}
	}

	public void send(Envelope e) {
		if (e instanceof BroadcastEnvelope) {
			sendAllExceptMe(e.getMessage());
		} else
			try {
				send(e.getMessage(), e.getDestination());
			} catch (IOException | JAXBException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

	}
	
	public void sendAllWithMoveAck(MoveMessage m){
		int receivers = sendAllExceptMe(m);
		
		while (MoveAckQueue.size() < receivers) {
			synchronized (MoveAckQueue) {
				try {
					MoveAckQueue.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for(MoveAck ack:MoveAckQueue){
			if(ack.eaten){
				boolean isWon;
				System.out.println("Sono "+main.me.getPort()+" e faccio punto");
				
				
				isWon=game.scorePoint();
				
				
				removePlayerFromGame(ack);
				

				
				
				
				if(isWon){
					win();
				}

			}
			
		}
		
		MoveAckQueue.clear();
	}
		
	public void removePlayerFromGame(MoveAck ack){
	

		ExitRingSetNextMessage n = new ExitRingSetNextMessage();
		n.newNext = ack.next;
		n.sender=main.me;
		ExitRingSetPrevMessage p = new ExitRingSetPrevMessage();
		p.newPrev = ack.prev;
		p.sender=main.me;
		
		if(ack.prev.getPort()==main.me.getPort()){
			tm.onExitRingSetNextMessageReceived(n);
		}
		else{
		sendWithAck(new Envelope(n,connectionList.get(ack.prev.getPort())));

		}
		if(ack.next.getPort()==main.me.getPort()){

			tm.onExitRingSetPrevMessageReceived(p);
		}
		else{
		sendWithAck(new Envelope(p,connectionList.get(ack.next.getPort())));
		}
		
		removeFromPeerList(ack.sender);
		if(connectionList.size()>0){
		RemoveFromYourListMessage rm=new RemoveFromYourListMessage();
		rm.sender=main.me;
		rm.target=ack.sender;
		
		

		sendAllWithAck(rm);
		}
	}

	public void sendWithAck(Envelope e) {
		if (e instanceof BroadcastEnvelope) {
			sendAllWithAck(e.getMessage());
		}
		else if (e instanceof BroadcastMoveEnvelope){
			sendAllWithMoveAck((MoveMessage)e.getMessage());
		}
		else {
			sendSingleWithAck(e.getMessage(), e.getDestination());

		}

	}

	public void sendSingleWithAck(Message m, DataOutputStream out) {
		try {

			send(m, out);
			synchronized (AckQueue) {
			if(AckQueue.isEmpty()){
				
				AckQueue.wait();
			}
			}

			AckQueue.poll();
		} catch (IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendAllWithAck(Message m) {
		int receivers = sendAllExceptMe(m);
		while (AckQueue.size() < receivers) {
			synchronized (AckQueue) {
				try {
					AckQueue.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		AckQueue.clear();
	}

	public void sendAck(Player p) {
		AckMessage am = new AckMessage();
		am.sender = main.me;
		try {
			send(am, p);
		} catch (IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public void onAddMeToYourListMessageReceived(AddMeToYourListMessage m) {

		try {
			addToPeerList(m.sender);
			sendAck(m.sender);
		} catch (JAXBException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Peer addToPeerList(Player p) throws JAXBException, IOException {
		Peer n;
		if (connectionList.containsKey(p.getPort())) {
			return connectionList.get(p.getPort());
		}
		int counter = 20;
		while (counter-- > 0) {
			try {

				Socket s = new Socket(p.getAddr(), p.getPort());

				n = new Peer(this, s, p);

				connectionList.put(p.getPort(), n);

				return n;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Sono " + main.me.getPort()
						+ " Fallisco la connessione a+" + p.getPort()
						+ ", riprovo");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
		throw new IOException();
	}


	
	public void gameLost() {
		System.out.println("Sono "+main.me.getPort()+" e ho perso");

		
		
		try {
			main.server.removePlayer(main.me);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			listener.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Sono "+main.me.getPort()+" e muoio");
	}

	public void removeFromPeerList(Player p) {
		System.out.println("Rimuovo dalla lista "+p.getPort());
		Peer peer = connectionList.get(p.getPort());
		
		//md.waitEmptyQueue();
		try {
			peer.getSocket().close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connectionList.remove(p.getPort());
	}
/*
	public synchronized void waitForEmptyQueue(){
		try {
			
		synchronized(sendQueueWaiter){
			System.out.println("TODO fare il wait for queue");
		sendQueueWaiter.wait(500);
		}
		
	}catch (InterruptedException |IllegalMonitorStateException e) {
			System.out.println("Eccezione in" +main.me.getPort());
			e.printStackTrace();
		}
	
	}

*/

	public void onDummyBroadCastMessageReceived(
			DummyBroadCastMessage dummyBroadCastMessage) {

		System.out.println("Ricevo Dummys");
		sendAck(dummyBroadCastMessage.sender);
	}

	public void onRemoveFromYourListMessage(	communication.RemoveFromYourListMessage removeFromYourListMessage) {
		removeFromPeerList(removeFromYourListMessage.target);
		sendAck(removeFromYourListMessage.sender);
		
	}

	public void win() {
		tm.blockToken();
		VictoryMessage vm = new VictoryMessage();
		vm.sender = main.me;
		sendAllWithAck(vm);
		System.out.println("Victory ack da tutti");
		
		System.out.println("Termino");
	}

	public void onVictoryMessageReceived(VictoryMessage m) {
		System.out.println("Il vincitore è "+m.sender.getPort());
		
		gameLost();
		sendAck(m.sender);
		}
}
