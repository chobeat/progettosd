package communication;

import java.io.IOException;
import java.util.Random;

import javax.xml.bind.annotation.XmlRootElement;

import distributed.PeerManager;

@XmlRootElement
public class TokenMessage extends Message {
	public long counter;
	
	public TokenMessage(){
		
	}
	@Override
	public void execute(PeerManager pm) {
		try {
			pm.tm.onTokenReceived(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
