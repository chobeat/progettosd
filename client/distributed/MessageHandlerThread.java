package distributed;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import javax.xml.bind.JAXBException;

import communication.Message;


public class MessageHandlerThread extends Thread {

	BlockingQueue<Message> queue;
	public MessageHandlerThread(BlockingQueue<Message> queue){
		this.queue=queue;
	}
	
	@Override
	public void run(){
		Message m;
		BufferedWriter writer;
		while(true){
				
				try {
					m=queue.take();
					writer=m.getDestination();
					writer.write(CustomMarshaller.getCustomMarshaller().marshal(m));
					
				} catch (InterruptedException | IOException | JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
		}
		
	}
}
