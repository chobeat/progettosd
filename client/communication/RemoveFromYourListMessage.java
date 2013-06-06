package communication;

import javax.xml.bind.annotation.XmlRootElement;

import common.Player;

import distributed.PeerManager;
@XmlRootElement
public class RemoveFromYourListMessage extends Message{
	
	public Player target;

	@Override
	public void execute(PeerManager pm){
		pm.onRemoveFromYourListMessage(this);
	}
}
