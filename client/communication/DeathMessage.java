package communication;

import game.Position;

import javax.xml.bind.annotation.XmlRootElement;

import distributed.PeerManager;

@XmlRootElement
public class DeathMessage extends Message {
	
	public Position lastPosition;
	@Override
	public void execute(PeerManager pm){
		pm.onDeathMessageReceived(this);
		
	}
}
