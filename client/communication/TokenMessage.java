package communication;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TokenMessage extends Message {
	public int counter=222;
	
	
	public void execute(){
		System.out.println("Sono un piccione");
		
	}
}
