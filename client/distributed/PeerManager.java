package distributed;

import game.Game;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import client.Main;
import client.ToServer;

import common.Player;
import communication.AckMessage;
import communication.AddMeToYourListMessage;
import communication.BroadcastEnvelope;
import communication.DeathMessage;
import communication.DummyBroadCastMessage;
import communication.Envelope;
import communication.Message;
import communication.RemoveMeFromYourListMessage;

public class PeerManager {
	ConcurrentHashMap<Integer, Peer> connectionList;
	ListenDispatcher listener;

	public Main main;
	public MessageDispatcher md;
	public TokenManager tm;
	public BlockingQueue<Message> inboundMessageQueue;
	public BlockingQueue<AckMessage> AckQueue;
	public Game game;

	public PeerManager(Main m, Player me, List<Player> pl) {
		inboundMessageQueue = new LinkedBlockingQueue<Message>();
		AckQueue = new LinkedBlockingQueue<AckMessage>();

		main = m;

		connectionList = new ConcurrentHashMap<Integer, Peer>();
		final List<Player> localList = pl;
		tm = new TokenManager(this);
		
		listener = new ListenDispatcher(this);
		listener.start();

		final PeerManager myself=this;
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
		md = new MessageDispatcher(2, this);
		game = new Game(this);

	}

	public void startMatch() {
		try {
			tm.joinRing();
			AddMeToYourListMessage m = new AddMeToYourListMessage();
			m.sender = main.me;

			sendWithAck(new BroadcastEnvelope(m));
		} catch (IOException | JAXBException e) {
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

	public int sendAllWithAckAtToken(Message m) {
		tm.messageToBeSent.add(new BroadcastEnvelope(m));
		return connectionList.size() - 1;
	}

	public void send(Message m, Player player) throws IOException, JAXBException {
		int port=player.getPort();
		Peer p=connectionList.get(port);
		
		if(p==null){
			p=addToPeerList(player);
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
		try{
		md.enqueue(m, out);
		}catch(NullPointerException e){
			System.out.println("Sono "+main.me.getPort()+" fallisco mandando "+m.getClass().getName());
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

	public void sendWithAck(Envelope e) {
		if (e instanceof BroadcastEnvelope) {
			sendAllWithAck(e.getMessage());
		} else {
			sendSingleWithAck(e.getMessage(), e.getDestination());

		}

	}

	public void sendSingleWithAck(Message m, DataOutputStream out) {
		try {
			send(m, out);
			while (AckQueue.size() < 1) {
				AckQueue.wait();
			}

			AckQueue.clear();
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
		System.out.println("Ricevuti " + receivers + "ACK. Procedo.");
	}

	public void onAddMeToYourListMessageReceived(AddMeToYourListMessage m) {

		try {
			addToPeerList(m.sender);
			AckMessage ack=new AckMessage();
			ack.sender=main.me;
			send(ack,m.sender);
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

				n = new Peer(this,s, p);

				connectionList.put(p.getPort(), n);

				return n;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Sono " + main.me.getPort()
						+ " Fallisco la connessione a+" + p.getPort()
						+ ", riprovo");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
		throw new IOException();
	}

	public void gameLost() {

		DeathMessage dm = new DeathMessage();
		dm.sender = main.me;
		sendAllWithAck(dm);

		exitRing();

		try {
			main.server.endMatch();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void exitRing() {
		RemoveMeFromYourListMessage m = new RemoveMeFromYourListMessage();
		m.sender = main.me;

		sendAllExceptMe(m);
		tm.exitRing();

	}

	public void removeFromPeerList(Player p) {
		Peer peer = connectionList.get(p.getPort());
		try {
			peer.getSocket().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connectionList.remove(p.getPort());
	}

	public void onRemoveMeFromYourListMessageReceived(
			RemoveMeFromYourListMessage m) {
		removeFromPeerList(m.sender);
	}

	public void onDummyBroadCastMessageReceived(
			DummyBroadCastMessage dummyBroadCastMessage) {

		System.out.println("Ricevo Dummys");
		AckMessage m = new AckMessage();
		m.sender = main.me;
		send(new Envelope(m, connectionList.get(dummyBroadCastMessage.sender
				.getPort())));

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
			} else {
				inboundMessageQueue.put(m);
			}

		} catch (ClassNotFoundException | DOMException | JAXBException
				| ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
