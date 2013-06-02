package communication;

import javax.xml.bind.annotation.XmlRootElement;

import distributed.PeerManager;
@XmlRootElement
public class VictoryMessage extends Message {

	@Override
	public void execute(PeerManager pm){
		pm.onVictoryMessageReceived();
		
	}
}
