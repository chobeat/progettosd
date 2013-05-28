package distributed;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ListenDispatcher extends Thread {

	
	ServerSocket socket;
	PeerManager pm;
	public  Map<Integer,Socket> socketMap;
	public ListenDispatcher(PeerManager p){
		socketMap=new HashMap<Integer,Socket>();
		try {
			socket=new ServerSocket(p.main.me.getPort());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pm=p;
	}
	@Override
	public void run(){
		Socket s;
		while(true){
			try {
				s=socket.accept();
				
					socketMap.put(s.getLocalPort(),s);
			   new ListenThread(s,pm).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
	}
}
