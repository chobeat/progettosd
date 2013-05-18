package distributed;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import client.Main;

import sun.nio.ch.ThreadPool;

import common.Player;

public class PeerManager {
	Player me;
	Map<Integer,Socket> connectionList;
	Executor executor;
	ListenThread listener;
	Main main;
	public PeerManager(Main m,Player me, HashMap<Integer, Player> pl) throws IOException{
		main=m;
		connectionList=new HashMap<Integer,Socket>();
		listener= new ListenThread(this,new ServerSocket(me.getPort()));
		listener.start();
		for(Player p:pl.values()){
			connectionList.put(p.getPort(), new Socket(p.getAddr(),p.getPort()));
			
		}
		
		
	}
	
}
