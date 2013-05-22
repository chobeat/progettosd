package communication;

import javax.xml.bind.annotation.XmlRootElement;

import distributed.PeerManager;

@XmlRootElement
public class DeathMessage extends Message {
	
	@Override
	public void execute(PeerManager pm){
		System.out.println("Sono morto");
		
	}
}
