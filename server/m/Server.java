package m;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import common.Match;
import common.Player;

public class Server {

	public static Server s;
	private int currID = 0;
	private HashMap<Integer, Match> matchList;
	private LinkedList<Player> playerList;

	public Server() {
		matchList = new HashMap<Integer, Match>();
		playerList = new LinkedList<Player>();
	}

	public synchronized int getNextMatchID() {
		return ++currID;

	};

	public static synchronized Server getServer() {
		if (s == null)
			s = new Server();
		return s;
	}

	public HashMap<Integer, Match> getMatchList() {
		return matchList;
	}

	public Match[] getMatchArray() {
	 return matchList.values().toArray(new Match[matchList.size()]);
		
	}

	public void setMatchList(HashMap<Integer, Match> matchList) {
		this.matchList = matchList;
	}

	public LinkedList<Player> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(LinkedList<Player> playerList) {
		this.playerList = playerList;
	}

	public synchronized Match createMatch(String name, Player starter) {
		Match m = new Match();
		m.name = name;
		starter.setId(m.nextID());
		m.addPlayer(starter);
		m.startMatch();
		m.setID( getNextMatchID());
		matchList.put(m.getID(), m);
		return m;

	}

	public Match joinMatch(Player p,int id){
		Match m=matchList.get(id);
		p.setId(m.nextID());
		m.addPlayer(p);
		System.out.println(m);
		return m;
	}
	
	public Player createPlayer(String name, String addr, int port) {
		Player p = new Player(name, addr, port);
		p.setId(-1);
		playerList.add(p);
		return p;
	}

}
