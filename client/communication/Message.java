package communication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import common.Player;

import distributed.PeerManager;

@XmlRootElement
public class Message {

	public final String type;
	
@XmlElement(name="senderPort")
	public Player sender;


	public Message() {
		type = this.getClass().getName();
		
	}

	public String getType() {
		return type;
	}

	public void execute(PeerManager pm) {
	};
}
