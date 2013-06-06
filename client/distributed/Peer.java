package distributed;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.xml.bind.JAXBException;

import common.Player;

public class Peer {
	public Player player;
	private Socket socket;
	PeerManager pm;
	public Peer(PeerManager pm,Socket s, Player p) throws IOException{
		if(s==null||p==null){
			throw new RuntimeException();
		}
		this.pm=pm;
		socket=s;
		this.player=p;
		 
	}
	
	public DataOutputStream getOutput() throws IOException {
		return new DataOutputStream(pm.connectionList.get(player.getPort()).socket.getOutputStream());
		
	}
	public Socket getSocket(){
		if(socket==null){
			try {
				socket=pm.addToPeerList(player).socket;
			} catch (JAXBException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return socket;
		
	}
}
