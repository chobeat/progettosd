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
	Position currentPosition;
	
	public Game(PeerManager pm) {
		currentPosition=new Position(new Random().nextInt()%99,new Random().nextInt()%99);
		this.pm=pm;
	}
	
	public Position move(String direction) throws InvalidInputException{
		switch(direction){
		case("1"):currentPosition.lessY();break;
		case("2"):currentPosition.addX();break;
		case("3"):currentPosition.addY();break;
		case("4"):currentPosition.lessX();break;
		default: throw new InvalidInputException();	
		}
		
		return currentPosition;
	}
	public void onMoveMessageReceived(MoveMessage m){
		System.out.println("Ricevo una move da "+m.sender.getPort());
		if(m.newPosition.equals(currentPosition))
			pm.gameLost();
		
		try {
			pm.send(new AckMessage(), m.sender);
		} catch (IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
