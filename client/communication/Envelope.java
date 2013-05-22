package communication;

import java.io.DataOutputStream;
//Contiene il messaggio nella coda. Utilizzato per non serializzare DataOutputStream
public class Envelope {
	Message message;
	DataOutputStream destination;

	public Envelope(Message m, DataOutputStream d){
		message=m;
		destination=d;
	}
	
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public DataOutputStream getDestination() {
		return destination;
	}
	public void setDestination(DataOutputStream destination) {
		this.destination = destination;
	}
}
