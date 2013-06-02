package communication;

import javax.xml.bind.annotation.XmlRootElement;

import common.Player;

import distributed.PeerManager;
@XmlRootElement
public class ExitRingSetNextMessage extends Message {

	public Player newNext;
	
	@Override
	public void execute(PeerManager pm){
		pm.tm.onExitRingSetNextMessageReceived(this);
	}
}
