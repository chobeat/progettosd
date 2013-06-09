package distributed;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

import javax.xml.bind.JAXBException;
import communication.BroadcastEnvelope;
import communication.Envelope;
import communication.Message;
import communication.MoveMessage;

public class MessageSenderThread extends Thread {
	PeerManager pm;
	// riferimento alla coda condivisa di messaggi in uscita
	BlockingQueue<Envelope> queue;

	public MessageSenderThread(BlockingQueue<Envelope> queue, PeerManager pm) {
		this.queue = queue;
		this.pm = pm;
	}

	@Override
	public void run() {
		Message m = null;
		DataOutputStream writer;
		while (true) {
			try {

				pm.md.increaseCounter();
				Envelope e = queue.take();
				m = e.getMessage();

				/*
				 * Se il messaggio è una Move, calcolo la posizione appena prima
				 * di mandarlo, così che sia consistente. La consistenza è
				 * garantita dal fatto che non ci sono due move inviate
				 * contemporaneamente, ma solo una per ogni token ricevuto
				 */
				if (m instanceof MoveMessage) {
					MoveMessage mm = (MoveMessage) m;

					mm.newPosition = pm.game.move(mm.direction).clone();
				}
				
				//Se devo spedire una BroadCastEnvelope, la spacchetto e la invio a tutti
				if (e instanceof BroadcastEnvelope) {
					for (Peer p : pm.connectionList.values()) {
						if (p.player.getPort() != pm.main.me.getPort()) {
							writeMessage(pm,
									new DataOutputStream(p.getOutput()),
									CustomMarshaller.getCustomMarshaller()
											.marshal(m) + "\n");
						}
					}
				} else {
					//Altrimenti se è una Envelope normale, invio solo al destinatario
					writer = e.getDestination();
					// System.out.println("Mando "+m.getClass().getName());
					writeMessage(pm, writer, CustomMarshaller
							.getCustomMarshaller().marshal(m) + "\n");
				}
			} catch (SocketException e) {

			}

			catch (InterruptedException | JAXBException | IOException e) {

				// System.out.println("Eccezione in "+pm.main.me.getPort()+""+Thread.currentThread().getId()+". Sono "+pm.main.me.getPort()+" Mando:"+
				// m.type);

				e.printStackTrace();
			} catch (CloneNotSupportedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (pm.md.queue.size() <= 0)
				pm.md.decreaseCounter();
		}

	}
	//Scrittura sincronizzata su un output stream, per evitare messaggi sovrapposti
	public static synchronized void writeMessage(PeerManager pm,
			DataOutputStream writer, String m) throws IOException {

		writer.writeBytes(m);

	}
}
