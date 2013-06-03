package distributed;

import java.io.DataOutputStream;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import communication.Envelope;
import communication.Message;

public class MessageDispatcher {
	public BlockingQueue<Envelope> queue;
	public Integer queueCounter=0;
	MessageDispatcher() {

		queue= new LinkedBlockingQueue<Envelope>();
	}
	public void init(int tNum, PeerManager pm)
	{
		for (int i = 0; i < tNum; i++) {
			new MessageHandlerThread(queue,pm).start();

		}
		
	}
	public synchronized void enqueue(Message m, DataOutputStream d) {
		enqueue(new Envelope(m,d));
	}
	public synchronized void enqueue(Envelope e){
		try {
			queue.put(e);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
public synchronized void increaseCounter(){
	queueCounter++;	
	}

public synchronized void decreaseCounter(){
	queueCounter--;	
	synchronized(queueCounter){
	queueCounter.notify();
	}
}
	
	public void waitEmptyQueue(){
		while(queueCounter>0&&queue.size()>0){
			try {
				synchronized (queueCounter) {

					queueCounter.wait();	
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
