package communication;

import java.io.BufferedWriter;
import java.net.Socket;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {
	public final String type;
	private BufferedWriter destination;
	
	public  BufferedWriter getDestination() {
		return destination;
	}





	public void setDestination( BufferedWriter destination) {
		this.destination = destination;
	}





	public Message(){
		type=this.getClass().getName();
	}
	
	



	public String getType() {
		return type;
	}

	public void execute(){};
}
