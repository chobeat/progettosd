package common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Match {
	public HashMap<Integer,Player> playerList;
	public String name;
	public Player winner;
	public boolean live = false;
	private int ID;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public Match() {

		playerList = new HashMap<Integer,Player>();
	}


	public void addPlayer(Player p) {
	
		
		playerList.put(p.getPort(), p);
		
		

	}

	public synchronized void removePlayer(int port) {

		playerList.remove(port);
	

	}
	public void declareVictory(int id) {
		Player p=playerList.get(id);
		winner = p;
		live = false;

	}

	@Override
	public String toString() {
		String res = "Match: " + this.name + "\nPartecipanti:";
		for (Player p : this.playerList.values()) {
			res = res + "\n" + p;

		}
		return res;
	}

}
