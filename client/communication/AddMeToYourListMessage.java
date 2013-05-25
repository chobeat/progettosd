package communication;

import javax.xml.bind.annotation.XmlRootElement;

import distributed.PeerManager;
@XmlRootElement
public class AddMeToYourListMessage extends Message {

	
	@Override
	public void execute(PeerManager pm){
		 pm.onAddMeToYourListMessageReceived(this);
		
	}

	
}
