package communication;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DeathMessage extends Message {
	
	
	public void execute(){
		System.out.println("Sono morto");
		
	}
}
