package distributed;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import javax.xml.bind.JAXBException;

import communication.Envelope;
import communication.Message;


public class MessageHandlerThread extends Thread {

	BlockingQueue<Envelope> queue;
	public MessageHandlerThread(BlockingQueue<Envelope> queue){
		this.queue=queue;
	}
	
	@Override
	public void run(){
		Message m;
		DataOutputStream writer;
		while(true){
				
				try {
					Envelope e=queue.take();
					m=e.getMessage();
					writer =e.getDestination();
					writeMessage(writer, CustomMarshaller.getCustomMarshaller().marshal(m)+"\n");
					
				} catch (InterruptedException  | JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
		}
		
	}

	public static synchronized void writeMessage(DataOutputStream writer, String m){

		try {
			writer.writeBytes(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}	
}
