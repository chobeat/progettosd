package distributed;

public class AckWaiter {
	public int counter;
	
	public AckWaiter(){
		counter=0;
	}
	public synchronized void waitForAck(int n){
		counter=n;
		while(counter>0){
			try {
				wait();
				System.out.println("Mi sveglio. Counter= "+counter);
				counter--;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return;
	}
	
}
