package server;

import java.util.LinkedList;
import java.util.List;

public class Server {

	public static Server s;
	private int currID=0;
	private LinkedList<Match> matchList;
	
	public Server(){
		matchList=new LinkedList<Match>();
	}
	public synchronized int getNextID(){
		return ++currID;
		
	};
	public static synchronized Server getServer() {
		if (s == null)
			s = new Server();
		return s;
	}

}
