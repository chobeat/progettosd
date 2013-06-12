package game;

import java.io.IOException;
import java.util.Random;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import communication.Envelope;
import communication.MoveAck;
import communication.MoveMessage;

import distributed.PeerManager;

public class Game {
	PeerManager pm;
	public Position currentPosition;
	int points;
	int WINNING_SCORE = 3;

	public String printPoints(){
		if(points==1)
			return "un punto";
		else
			return this.points+" punti";
		
	}
	
	public Game(PeerManager pm) {
		points = 0;
		currentPosition = new Position(-1,-1);
		this.pm = pm;
	}

	public boolean scorePoint() {
		points++;
		if (points >= WINNING_SCORE) {
			return true;
		}

		return false;
	}

	public int parseCommand(String direction) throws InvalidInputException{
		int result;
		switch (direction) {
		case("1"): result=1 ;break;
		case("2"): result=2 ;break;
		case("3"): result=3 ;break;
		case("4"): result=4 ;break;
		//quit-test
		case("q"): result=-1;break;
		//win-test
		case("w"): result=-2;break;
		//first move
		case("f"): result=-3;break;
		default: throw new InvalidInputException();
		
		}
		return result;
		
	}
	
	public Position move(int direction){
		switch (direction) {
		case (1):
			currentPosition.lessY();
			break;
		case (2):
			currentPosition.addX();
			break;
		case (3):
			currentPosition.addY();
			break;
		case (4):
			currentPosition.lessX();
			break;
		case (-1):
			pm.gameLost();
		case (-2):
			pm.win();
		//first random move
		case (-3):
			currentPosition=new Position((new Random()).nextInt(Position.MAX_GRID_SIZE),(new Random()).nextInt(Position.MAX_GRID_SIZE));

		
		}
		
		System.out.println("Sei in "+currentPosition);
		return currentPosition;
	}

	public void onMoveMessageReceived(MoveMessage m) {
	
		System.out.println("Sono" + pm.main.me.getPort() + " in "
				+ currentPosition + " ," + m.sender.getPort() + " è in "
				+ m.newPosition);
		
		
		MoveAck ack = new MoveAck();
		Envelope e = new Envelope(ack);
		ack.sender=pm.main.me;
		try {
			e.setDestination(pm.connectionList.get(m.sender.getPort())
					.getOutput());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//valuto la mia posizione rispetto a quella della Move ricevuta
		if (m.newPosition.equals(currentPosition)) {

			System.out.println("Sei stato mangiato dal giocatore "+m.sender.getName()+" :(");
			
			ack.eaten = true;
			ack.prev=pm.tm.prev.player;

			ack.next=pm.tm.next.player;

			pm.send(e);
			System.out.println("Hai perso, prova di nuovo");
			pm.gameLost();

		} else {
			ack.eaten = false;
			pm.send(e);
		}
	}

}
