package distributed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ListenThread extends Thread {

	Socket socket;
	BufferedReader inFromClient;
	public ListenThread(Socket s) throws IOException{
		System.out.println("Sono dispatchato");
		socket=s;
		inFromClient=new BufferedReader(new
				InputStreamReader(socket.getInputStream()));
		
	}
	@Override
	public void run() {
		
		while(true){
			try {
				System.out.println("Ricevo: "+inFromClient.readLine());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
		
	}

	
}
