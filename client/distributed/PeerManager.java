package distributed;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
import communication.Message;

public class PeerManager {
	Map<Integer,Socket> connectionList;
	Executor executor;
	ListenDispatcher listener;
	Main main;
	public PeerManager(Main m,Player me,List<Player> pl) throws IOException{
		main=m;
		connectionList=new HashMap<Integer,Socket>();
		listener= new ListenDispatcher(this);
		listener.start();
		List<Player> localMap=pl;
		
		for(Player p:localMap){
			if(p.getPort()!=me.getPort())
			connectionList.put(p.getPort(), new Socket(p.getAddr(),p.getPort()));
			
		}
		
		
	}
	public void send(Message m, int port) throws IOException, JAXBException{
		System.out.println("Connection list"+connectionList.size());
		Socket s=connectionList.get(port);
		if(s==null){
			System.out.println("Giocatore"+port+" non presente");
			
			System.exit(0);
		}
		DataOutputStream output=new DataOutputStream(s.getOutputStream());
		String msg=CustomMarshaller.getCustomMarshaller().marshal(m);
		output.writeBytes(msg+"\n");
	}
	
}
