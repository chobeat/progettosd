package communication;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {
	public final String type;
	public Message(){
		type=this.getClass().getName();
	}

	public void execute(){};
}
