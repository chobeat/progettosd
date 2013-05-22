package distributed;

import java.io.DataOutputStream;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import communication.Envelope;
import communication.Message;

public class MessageDispatcher {
	public BlockingQueue<Envelope> queue;
	MessageDispatcher(int tNum) {
		queue= new LinkedBlockingQueue<Envelope>();
		for (int i = 0; i < tNum; i++) {
			new MessageHandlerThread(queue).start();

		}
	}

	public synchronized void enqueue(Message m, DataOutputStream d) {
		try {

			queue.put(new Envelope(m,d));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

}
