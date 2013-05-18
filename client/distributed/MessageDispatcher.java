package distributed;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import communication.Message;

public class MessageDispatcher {
	public static int HandlingThreadNumber=2;
	public BlockingQueue<Message> queue;
	private static MessageDispatcher singleton;
	private MessageDispatcher(int tNum) {
		queue= new LinkedBlockingQueue<Message>();
		for (int i = 0; i < tNum; i++) {
			new MessageHandlerThread(queue).start();

		}
	}

	public static MessageDispatcher getMessageDispatcher(){
		if(singleton==null)
			singleton=new MessageDispatcher(HandlingThreadNumber);
		return singleton;
		
	}
	public synchronized void enqueue(Message m) {
		try {
			queue.put(m);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

}
