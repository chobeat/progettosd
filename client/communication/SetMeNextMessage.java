package communication;

import javax.xml.bind.annotation.XmlRootElement;

import distributed.PeerManager;

@XmlRootElement
public class SetMeNextMessage extends Message {

	@Override
	public void execute(PeerManager pm){
	pm.tm.onSetMeNextMessageReceived(this);	
	
	}
}
