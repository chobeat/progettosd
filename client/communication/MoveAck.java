package communication;

import javax.xml.bind.annotation.XmlRootElement;

import common.Player;

@XmlRootElement
public class MoveAck extends Message {

	public Player prev;

	public Player next;
	public boolean eaten;

}
