package distributed;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;

import client.Main;
import client.ToServer;

import common.Player;
import communication.AckMessage;
import communication.AddMeToYourListMessage;
import communication.DummyBroadCastMessage;
import communication.Envelope;
import communication.Message;
import communication.RemoveMeFromYourListMessage;

public class PeerManager {
	Map<Integer, Peer> connectionList;
	ListenDispatcher listener;

	public Main main;

	public MessageDispatcher md;
	public TokenManager tm;
	public AckWaiter aw;
	public PeerManager(Main m, Player me, List<Player> pl) {
		aw=new AckWaiter();
		main = m;
		md = new MessageDispatcher(2);
		connectionList = new HashMap<Integer, Peer>();
		listener = new ListenDispatcher(this);
		listener.start();
		tm = new TokenManager(this);
		List<Player> localMap = pl;
		for (Player p : localMap) {
			try {
				addToPeerList(p);
			} catch (IOException e) {
				System.err.println("Give up on connection");
				
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void startMatch() {

		try {
			tm.joinRing();
			AddMeToYourListMessage m = new AddMeToYourListMessage();
			m.sender = main.me;

			sendAllExceptMe(m);
		} catch (IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendAll(Message m) {

		m.sender = main.me;
		for (Peer p : connectionList.values()) {
			md.enqueue(m, new DataOutputStream(p.output));
		}
	}

	public void sendAllExceptMe(Message m) {

		m.sender = main.me;
		for (Peer p : connectionList.values()) {
			if (p.player.getPort() != main.me.getPort()) {
				md.enqueue(m, new DataOutputStream(p.output));
			}
		}
	}

	public void onAddMeToYourListMessageReceived(AddMeToYourListMessage m) {

		try {
			addToPeerList(m.sender);
		} catch (JAXBException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public void send(Message m, int port) throws IOException, JAXBException {
		Socket s = connectionList.get(port).socket;
		if (s == null) {
			System.out.println("Giocatore" + port + " non presente");

			System.exit(0);
		}
		m.sender = main.me;
		
		send(m, connectionList.get(port).output);
	}
	public void send(Message m, DataOutputStream out) throws IOException, JAXBException {

	md.enqueue(m,out);

	}

	public Peer addToPeerList(Player p) throws JAXBException, IOException {
		Peer n;
		if(connectionList.containsKey(p.getPort()))
		{
			return connectionList.get(p.getPort());
		}
			int counter=20;
		while(counter-->0){
		try {
			
			Socket s=new Socket(p.getAddr(), p.getPort());
			
			n = new Peer(s, p);

			connectionList.put(p.getPort(), n);
			
			return n;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Sono "+main.me.getPort()+" Fallisco la connessione a+"+ p.getPort()+", riprovo");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		}
		main.server.removePlayer(p);
		throw new IOException();
	}

	public void gameLost(ToServer server){
		exitRing();
		
		try {
			server.endMatch();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void exitRing(){
		RemoveMeFromYourListMessage m=new RemoveMeFromYourListMessage();
		m.sender=main.me;
		
		sendAllExceptMe(m);
		tm.exitRing();
		
	}
	
	public void removeFromPeerList(Player p){
		Peer peer=connectionList.get(p.getPort());
		try {
			peer.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connectionList.remove(p.getPort());
	}
	public void  onRemoveMeFromYourListMessageReceived(RemoveMeFromYourListMessage m){
		removeFromPeerList(m.sender);
	}

	public void onDummyBroadCastMessageReceived(
			DummyBroadCastMessage dummyBroadCastMessage) {
System.out.println("Ricevo dummy");
		AckMessage m=new AckMessage();
		m.sender=main.me;
		try {
			send(m, dummyBroadCastMessage.sender.getPort());
		} catch (IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
