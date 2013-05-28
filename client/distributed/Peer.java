package distributed;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import common.Player;

public class Peer {
	public DataOutputStream output;
	Player player;
	Socket socket;
	
	public Peer(Socket s, Player p) throws IOException{
		socket=s;
		this.player=p;
		output= new DataOutputStream(s.getOutputStream());
		 
	}

}
