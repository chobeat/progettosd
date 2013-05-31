package communication;

import java.io.DataOutputStream;
import java.io.IOException;

import distributed.Peer;
//Contiene il messaggio nella coda. Utilizzato per non serializzare DataOutputStream
public class Envelope {
	Message message;
	DataOutputStream destination;

	public Envelope(Message m){
		message=m;
	}
	
	public Envelope(Message m, DataOutputStream d){
		message=m;
		destination=d;
	}
	public Envelope(Message m,  Peer p){
		message=m;
		try {
			destination=p.getOutput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
