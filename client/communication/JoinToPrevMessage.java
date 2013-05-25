package communication;

import javax.xml.bind.annotation.XmlRootElement;

import distributed.PeerManager;

@XmlRootElement
public class JoinToPrevMessage extends Message {

	@Override
	public void execute(PeerManager pm){
		
	pm.tm.onJoinToPrevMessageReceived(this);	
	
	}
}
