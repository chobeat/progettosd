package communication;

import common.Player;

import distributed.PeerManager;

public class ExitRingSetNextMessage extends Message {

	public Player newNext;
	
	@Override
	public void execute(PeerManager pm){
		pm.tm.onExitRingSetNextMessageReceived(this);
	}
}
