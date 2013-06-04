package game;

import java.io.IOException;
import java.util.Random;

import javax.xml.bind.JAXBException;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import communication.AckMessage;
import communication.MoveMessage;

import distributed.PeerManager;

public class Game {
	PeerManager pm;
public	Position currentPosition;
    int points;
    int WINNING_SCORE=3;
    
	public Game(PeerManager pm) {
		points=0;
		currentPosition=new Position(new Random().nextInt(Position.MAX_GRID_SIZE),new Random().nextInt(Position.MAX_GRID_SIZE));
		System.out.println("Parto in "+currentPosition);
		this.pm=pm;
	}
	
	public boolean scorePoint(){
		points++;
		if(points>=WINNING_SCORE){
			return true;
		}
		
		return false;
	}
	
	public Position move(String direction) throws InvalidInputException{
		switch(direction){
		case("1"):currentPosition.lessY();break;
		case("2"):currentPosition.addX();break;
		case("3"):currentPosition.addY();break;
		case("4"):currentPosition.lessX();break;
		case("q"):pm.gameLost(null);
		case("w"):pm.win();
		
		default: throw new InvalidInputException();	
		}
		
		return currentPosition;
	}
	public void onMoveMessageReceived(MoveMessage m){
		System.out.println("Sono"+ pm.main.me.getPort()+" in "+currentPosition+" ,"+m.sender.getPort()+" è in "+m.newPosition);
		
		if(m.newPosition.equals(currentPosition)){

			System.err.println("Sono stato mangiato");
			pm.gameLost(m.sender);

		}
	}

	
}
