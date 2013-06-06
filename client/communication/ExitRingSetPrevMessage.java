package communication;

import javax.xml.bind.annotation.XmlRootElement;

import common.Player;

import distributed.PeerManager;
@XmlRootElement
public class ExitRingSetPrevMessage extends Message {
	public Player newPrev;
	public Player target;
	
	@Override
	public void execute(PeerManager pm){
		pm.tm.onExitRingSetPrevMessageReceived(this);
	}
}
