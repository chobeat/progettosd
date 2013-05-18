package distributed;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ListenDispatcher extends Thread {

	
	ServerSocket socket;
	PeerManager manager;
	public ListenDispatcher(PeerManager p){
		System.out.println("Creo Listen dispatcher");
		try {
			socket=new ServerSocket(p.main.me.getPort());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		manager=p;
	}
	@Override
	public void run(){
		Socket s;
		while(true){
			try {
				s=socket.accept();
			   new ListenThread(s).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
