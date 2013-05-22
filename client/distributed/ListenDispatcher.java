package distributed;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ListenDispatcher extends Thread {

	
	ServerSocket socket;
	PeerManager pm;
	public ListenDispatcher(PeerManager p){
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
			   new ListenThread(s,pm).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
