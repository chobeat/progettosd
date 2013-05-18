package distributed;

import java.io.IOException;
import java.net.ServerSocket;


public class ListenThread extends Thread {

	
	ServerSocket socket;
	PeerManager manager;
	public ListenThread(PeerManager p){
		try {
			socket=new ServerSocket(p.me.getPort());
			socket.accept();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		manager=p;
	}
	@Override
	public void run(){
		
		while(true){
			try {
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
