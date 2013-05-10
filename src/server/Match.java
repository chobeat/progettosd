package server;

import java.util.List;

public class Match {
 public List<Player> playerList;
 public String name;
 public Player winner;
 public boolean live=false;
 
 public void addPlayer(Player p){
	if(!playerList.contains(p))
		playerList.add(p);
	
	
 }
 public synchronized void removePlayer(Player p){
	
	 playerList.remove(p);
	 
	 if(playerList.size()==1)
	 {
		 declareVictory(playerList.get(0));
	  return;
	 }
	 

	 if(playerList.size()<1)
	  abortMatch();
	  
 }
 
 public void abortMatch(){
	 live=false;
 }
 
 public void startMatch(){
	 live=true;
	 
 }
 
 
 public void declareVictory(Player p){
	 winner=p;
	 live=false;
	 
 }
 
}
