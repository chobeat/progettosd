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
	private int id;

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
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

	public void end(){
		System.out.println("Match "+id +"terminato");
		
	}
	@Override
	public String toString() {
		String res = "Match numero "+id+": " + this.name + "\nPartecipanti:";
		for (Player p : this.playerList.values()) {
			res = res + "\n" + p;

		}
		return res;
	}

}
