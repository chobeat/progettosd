package communication;

import game.Position;

import javax.xml.bind.annotation.XmlRootElement;

import distributed.PeerManager;
@XmlRootElement
public class MoveMessage extends Message {

	public Position newPosition;
	
	@Override
	public void execute(PeerManager pm){
		pm.game.onMoveMessageReceived(this);
	}
}
