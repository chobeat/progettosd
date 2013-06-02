package distributed;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import javax.xml.bind.JAXBException;

import org.hamcrest.core.IsInstanceOf;

import communication.BroadcastEnvelope;
import communication.Envelope;
import communication.Message;

public class MessageHandlerThread extends Thread {
	PeerManager pm;

	BlockingQueue<Envelope> queue;

	public MessageHandlerThread(BlockingQueue<Envelope> queue, PeerManager pm) {
		this.queue = queue;
		this.pm = pm;
	}

	@Override
	public void run() {
		Message m=null;
		DataOutputStream writer;
		while (true) {

			try {
				Envelope e = queue.take();
				m = e.getMessage();
				if (e instanceof BroadcastEnvelope) {
					for (Peer p : pm.connectionList.values()) {
						if(p.player.getPort()!=pm.main.me.getPort()){
						writeMessage(pm,new DataOutputStream(p.getOutput()), CustomMarshaller
								.getCustomMarshaller().marshal(m) + "\n");
						}
						}
				} else {

					writer = e.getDestination();
					// System.out.println("Mando "+m.getClass().getName());
					writeMessage(pm,writer, CustomMarshaller.getCustomMarshaller()
							.marshal(m) + "\n");
				}
			} catch (InterruptedException | JAXBException | IOException e) {
				
				//System.out.println("Eccezzione in "+pm.main.me.getPort()+""+Thread.currentThread().getId()+". Sono "+pm.main.me.getPort()+" Mando:"+ m.type);
				
				e.printStackTrace();
			}
			
		}

	}

	public static synchronized void writeMessage(PeerManager pm,DataOutputStream writer,
			String m) throws IOException {

			writer.writeBytes(m);

	}
}
