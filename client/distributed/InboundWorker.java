package distributed;

import communication.Message;

public class InboundWorker extends Thread{
	
	PeerManager pm;
	public InboundWorker(PeerManager pm){
		this.pm=pm;
	}
	@Override
	public void run(){
	Message m;
		while(true){
			try {
				m=pm.inboundMessageQueue.take();
				m.execute(pm);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
