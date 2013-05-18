package common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Match {
	public List<Player> playerList;
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

		playerList = new LinkedList<Player>();
	}


	public void addPlayer(Player p) {
	
		
		playerList.add(p);
		
		

	}

	public synchronized void removePlayer(Player p) {
		
		for(Player i:playerList){
		
			if(i.getPort()==p.getPort()){
				playerList.remove(i);
			}
			
		}
	

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
		for (Player p : this.playerList) {
			res = res + "\n" + p;

		}
		return res;
	}

}
