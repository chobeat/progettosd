package distributed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import communication.AckMessage;
import communication.Message;
import communication.MoveAck;

public class ListenThread extends Thread {
	public static int count=0;
	Socket socket;
	BufferedReader inFromClient;
	PeerManager pm;
	public ListenThread(Socket s,PeerManager pm) throws IOException{
		this.pm=pm;
		socket=s;
		inFromClient=new BufferedReader(new
				InputStreamReader(socket.getInputStream()));
		
	}
	
	public void handleMessage(String s) throws InterruptedException {
		Message m;
	
		try {
	
			m = CustomMarshaller.getCustomMarshaller().unmarshal(s);
			if (m == null) {
				return;
			}
		
			//Smisto gli ack dai messaggi normali
			if (m instanceof AckMessage) {
				synchronized (pm.AckQueue) {
					pm.AckQueue.put((AckMessage) m);
					pm.AckQueue.notify();
				}
			} else if (m instanceof MoveAck) {
				synchronized (pm.MoveAckQueue) {
					pm.MoveAckQueue.put((MoveAck) m);
					pm.MoveAckQueue.notify();
				}
			} else {
				pm.inboundMessageQueue.put(m);
			}
	
		} catch (ClassNotFoundException | DOMException | JAXBException
				| ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	@Override
	public void run() {
		
		while(true){
			try {
				String s=inFromClient.readLine();
				if(s==null)
					continue;
				
					handleMessage( s);
				
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}		}
		
	}

	
}
