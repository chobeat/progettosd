package communication;

import javax.xml.bind.annotation.XmlRootElement;

import common.Player;

import distributed.PeerManager;

@XmlRootElement
public class JoinRingAckMessage extends Message {

	public Player newPrev;
	public boolean found;
	@Override
	public void execute(PeerManager pm){
	pm.tm.onJoinRingAckMessageReceived(this);
	}
}
