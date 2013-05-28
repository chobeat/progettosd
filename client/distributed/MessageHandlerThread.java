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
		Message m;
		DataOutputStream writer;
		while (true) {

			try {
				Envelope e = queue.take();
				m = e.getMessage();

				if (e instanceof BroadcastEnvelope) {
					for (Peer p : pm.connectionList.values()) {
						if(p.player.getPort()!=pm.main.me.getPort()){
						writeMessage(p.output, CustomMarshaller
								.getCustomMarshaller().marshal(m) + "\n");
						}
						}
				} else {

					writer = e.getDestination();
					// System.out.println("Mando "+m.getClass().getName());
					writeMessage(writer, CustomMarshaller.getCustomMarshaller()
							.marshal(m) + "\n");
				}
			} catch (InterruptedException | JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static synchronized void writeMessage(DataOutputStream writer,
			String m) {

		try {
			writer.writeBytes(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
