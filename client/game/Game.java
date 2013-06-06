package game;

import java.io.IOException;
import java.util.Random;

import javax.xml.bind.JAXBException;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import communication.AckMessage;
import communication.Envelope;
import communication.MoveAck;
import communication.MoveMessage;

import distributed.PeerManager;

public class Game {
	PeerManager pm;
	public Position currentPosition;
	int points;
	int WINNING_SCORE = 3;

	public Game(PeerManager pm) {
		points = 0;
		currentPosition = new Position();
		System.out.println("Parto in " + currentPosition);
		this.pm = pm;
	}

	public boolean scorePoint() {
		points++;
		if (points >= WINNING_SCORE) {
			return true;
		}

		return false;
	}

	public Position move(String direction) throws InvalidInputException {
		switch (direction) {
		case ("1"):
			currentPosition.lessY();
			break;
		case ("2"):
			currentPosition.addX();
			break;
		case ("3"):
			currentPosition.addY();
			break;
		case ("4"):
			currentPosition.lessX();
			break;
		case ("q"):
			pm.gameLost();
		case ("w"):
			pm.win();

		default:
			throw new InvalidInputException();
		}

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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (m.newPosition.equals(currentPosition)) {

			System.out.println("Sono stato mangiato");

			// pm.tm.exitRing();
			ack.eaten = true;
			ack.prev=pm.tm.prev.player;

			ack.next=pm.tm.next.player;

			System.out.println("Ho mandato ack con prev "+pm.tm.prev.player.getPort() +" e next "+pm.tm.next.player.getPort());
			pm.send(e);

			pm.gameLost();

		} else {
			ack.eaten = false;
			pm.send(e);
		}
		System.out.println("Ho mandato ack");
	}

}
