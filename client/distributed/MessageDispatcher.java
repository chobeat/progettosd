package distributed;

import java.io.DataOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import communication.Envelope;
import communication.Message;

public class MessageDispatcher {
	
	//Coda dei messaggi in uscita
	public BlockingQueue<Envelope> queue;
	//Numero di messaggi in gestione dai thread
	public Integer queueCounter=0;
	MessageDispatcher() {

		queue= new LinkedBlockingQueue<Envelope>();
	}
	public void init(int tNum, PeerManager pm)
	{	//Inizializzo i thread di invio 
		for (int i = 0; i < tNum; i++) {
			new MessageSenderThread(queue,pm).start();

		}
		
	}
	
	//Accodo un messaggio
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
	//Aspetto che tutti i messaggi siano stati inviati
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
