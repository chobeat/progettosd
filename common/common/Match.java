package common;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Match {
	public List<Player> playerList;
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

	public int pIDCount = 0;

	public Match() {

		playerList = new LinkedList<Player>();
	}

	public int nextID() {
		return ++pIDCount;
	}

	public void addPlayer(Player p) {
		if (!playerList.contains(p))
			playerList.add(p);

	}

	public synchronized void removePlayer(Player p) {

		playerList.remove(p);

		if (playerList.size() == 1) {
			declareVictory(playerList.get(0));
			return;
		}

		if (playerList.size() < 1)
			abortMatch();

	}

	public void abortMatch() {
		live = false;
	}

	public void startMatch() {
		live = true;

	}

	public void declareVictory(Player p) {
		winner = p;
		live = false;

	}

	@Override
	public String toString() {
		String res = "Match: " + this.name + "\nPartecipanti:";
		for (Player p : this.playerList) {
			res = res + "\n" + p;

		}
		return res;
	}

}
