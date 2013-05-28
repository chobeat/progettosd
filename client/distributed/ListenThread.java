package distributed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import communication.Message;

public class ListenThread extends Thread {

	Socket socket;
	BufferedReader inFromClient;
	PeerManager pm;
	public ListenThread(Socket s,PeerManager pm) throws IOException{
		this.pm=pm;
		socket=s;
		inFromClient=new BufferedReader(new
				InputStreamReader(socket.getInputStream()));
		
	}
	@Override
	public void run() {
		
		while(true){
			try {
				String s=inFromClient.readLine();
				
				try {
					pm.handleMessage( s);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}		}
		
	}

	
}
