package distributed;

import java.io.DataOutputStream;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import communication.Envelope;
import communication.Message;

public class MessageDispatcher {
	public BlockingQueue<Envelope> queue;
	MessageDispatcher(int tNum, PeerManager pm) {
		queue= new LinkedBlockingQueue<Envelope>();
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
	
}
