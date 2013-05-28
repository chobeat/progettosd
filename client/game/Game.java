package game;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import communication.MoveMessage;

import distributed.PeerManager;

public class Game {
	PeerManager pm;
	Position currentPosition;
	
	public Game(PeerManager pm) {
		
		this.pm=pm;
	}
	
	public Position move(String direction) throws InvalidInputException{
		switch(direction){
		case("1"):currentPosition.lessY();
		case("2"):currentPosition.addX();
		case("3"):currentPosition.addY();
		case("4"):currentPosition.lessX();
		default: throw new InvalidInputException();	
		}
		
		//return currentPosition;
	}
	public void onMoveMessageReceived(MoveMessage m){
		
		if(m.newPosition.equals(currentPosition))
			pm.gameLost();
		
		
	}
	
}
