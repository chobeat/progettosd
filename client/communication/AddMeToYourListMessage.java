package communication;

import game.Position;

import javax.xml.bind.annotation.XmlRootElement;

import distributed.PeerManager;
@XmlRootElement
public class AddMeToYourListMessage extends Message {
	public Position position;
	
	@Override
	public void execute(PeerManager pm){
		 pm.onAddMeToYourListMessageReceived(this);
		
	}

	
}
