package communication;

import common.Player;

import distributed.PeerManager;

public class ExitRingSetPrevMessage extends Message {
	public Player newPrev;
	
	@Override
	public void execute(PeerManager pm){
		pm.tm.onExitRingSetPrevMessageReceived(this);
	}
}
