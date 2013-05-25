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
				Message m=CustomMarshaller.getCustomMarshaller().unmarshal(inFromClient.readLine());
				if(m==null){
					continue;
				}
				m.execute(pm);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
		
	}

	
}
