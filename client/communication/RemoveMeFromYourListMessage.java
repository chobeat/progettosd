package communication;

import javax.xml.bind.annotation.XmlRootElement;

import distributed.PeerManager;

@XmlRootElement
public class RemoveMeFromYourListMessage extends Message{

	@Override
	public void execute(PeerManager pm){
	pm.onRemoveMeFromYourListMessageReceived(this);	
	
	}
	
}
