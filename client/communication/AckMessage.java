package communication;

import javax.xml.bind.annotation.XmlRootElement;

import distributed.PeerManager;
@XmlRootElement
public class AckMessage extends Message {

	@Override
	public synchronized void execute(PeerManager pm){
		System.out.println("Sono "+pm.main.me+ "e ricevo ack");
		
	}
}
