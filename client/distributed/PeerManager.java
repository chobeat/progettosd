package distributed;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBException;

import client.Main;

import sun.nio.ch.ThreadPool;

import common.Player;
import communication.JoinRingReplyMessage;
import communication.Message;

public class PeerManager {
	Map<Integer, Peer> connectionList;
	Executor executor;
	ListenDispatcher listener;

	public Main main;

	public MessageDispatcher md;
	public TokenManager tm;

	public PeerManager(Main m, Player me, List<Player> pl) throws IOException {
		main = m;
		md = new MessageDispatcher(2);
		connectionList = new HashMap<Integer, Peer>();
		listener = new ListenDispatcher(this);
		listener.start();
		tm = new TokenManager(this);
		List<Player> localMap = pl;

		for (Player p : localMap) {
			System.out.println(p);
				joinPeerList(p);

		}

	}

	public void send(Message m, int port) throws IOException, JAXBException {
		Socket s = connectionList.get(port).socket;
		if (s == null) {
			System.out.println("Giocatore" + port + " non presente");

			System.exit(0);
		}
		m.sender = main.me;

		md.enqueue(m, new DataOutputStream(connectionList.get(port).output));
	}
	public Peer joinPeerList(Player p){
		Peer n;
		try {
			n = new Peer(new Socket(p.getAddr(), p.getPort()), p);

			connectionList.put(p.getPort(),n );
			return n;
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
		}
	}



}
