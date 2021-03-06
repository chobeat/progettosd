package m;

import java.util.HashMap;
import java.util.LinkedList;
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
		m.addPlayer(starter);
		
		m.setId( getNextMatchID());
		matchList.put(m.getId(), m);
		return m;

	}
	
	public synchronized Match removePlayer(int matchID,Player p){
		Match m=this.getMatchByID(matchID);
		m.removePlayer(p);
		return m;
	}
	
	public void endAllMatch(){
		
		matchList.clear();
	}

	public Match joinMatch(Player p,int id){
		try{
		Match m=matchList.get(id);

		m.addPlayer(p);
		return m;
		} catch(RuntimeException e){
			return null;
		}
	}
	
	public synchronized Match getMatchByID(int ID){
		return matchList.get(ID);
		
	}
	
	public synchronized boolean endMatch(int ID){
		getMatchByID(ID).end();
		return matchList.remove(ID)==null;
		 
	}
	
	public synchronized Player createPlayer(String name, String addr, int port) {
		Player p = new Player(name, addr, port);
		
		playerList.add(p);
		return p;
	}

}
