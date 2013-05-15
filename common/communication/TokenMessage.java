package communication;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TokenMessage extends Message {

	public void execute(){
		System.out.println("Sono un piccione");
		
	}
}
