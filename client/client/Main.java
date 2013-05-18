package client;

import common.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import com.sun.corba.se.impl.transport.ListenerThreadImpl;

import distributed.ListenThread;
import distributed.PeerManager;

import test.customTest;

public class Main {
	ToServer server;
	BufferedReader in;
	Player me;
	Match activeMatch;
	PeerManager peerManager;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// new customTest(generateRandomPlayer()+"1\ntestquit\n").start();
		 runParallelTest();
		//runPlain();
	}

	public static void runPlain() throws IOException, JAXBException,
			ActiveMatchNotPresent {
		Main m = new Main(System.in);
		m.runClient();

	}

	public Main(InputStream input) throws IOException {
		in = new BufferedReader(new InputStreamReader(input));
	}

	public void runClient() throws IOException, JAXBException,
			ActiveMatchNotPresent {
		server = new ToServer(this);
		System.out.println("Quale nome vuoi usare?");
		String name = in.readLine();
		System.out.println("Quale porta vuoi usare?");
		int port = Integer.parseInt(in.readLine());
		System.out.println(server.createPlayer(name, port));
		// Match selection
		activeMatch = this.matchSelection();

		playMatch();

	}

	private Match matchSelection() throws IOException, JAXBException,
			ActiveMatchNotPresent {

		System.out.println("Benvenuto in MMOG!");

		String selection;
		Match m = null;

		boolean badID = true;
		while (badID) {
			try {

				System.out
						.println("Le partite in corso sono:\n"
								+ server.getMatchList()
								+ "\nPer scegliere una partita digita il numero corrispondente, per crearne una digita 0, per uscire digita q. Cosa vuoi fare?");

			} catch (EmptyMatchListException e) {
				System.out
						.println("Non ci sono partite presenti. Per crearne una digita 0, per uscire digita q. Cosa vuoi fare?");

			}
			selection = in.readLine();

			switch (selection) {
			case ("q"):
				System.exit(0);
				break;
			case ("0"):
				m = server.createMatch(askMatch());
				badID = false;
				break;
			default: {
				try {
					m = server.joinMatch(Integer.parseInt(selection));
					badID = false;
				} catch (ArrayIndexOutOfBoundsException | NullPointerException
						| NumberFormatException e) {
					System.out.println("Identificativo non valido");
					badID = true;
				} catch (IndexOutOfBoundsException e) {
					System.out.println("La partita è stata cancellata");
					badID = true;
				}

			}
			}

		}
		if (m == null)
			throw new ActiveMatchNotPresent();
		else

			return m;

	}

	String askMatch() throws IOException {
		System.out.println("Inserisci il nome della partita che vuoi creare");
		return in.readLine();

	}

	public void playMatch() throws IOException, JAXBException,
			ActiveMatchNotPresent {
		peerManager=new PeerManager(this,me, activeMatch.playerList);
		
		if (activeMatch == null) {
			throw new ActiveMatchNotPresent();
		}
		
		ListenThread lt=new ListenThread(peerManager);
		String selection;
		while (true) {

			selection = in.readLine();

			switch (selection) {
			case ("1"): {
			}
			case ("2"): {
			}
			case ("3"): {
			}
			case ("4"): {
			}
			case ("testquit"): {
				System.out.println("Esco");
				server.quit();
				System.exit(0);
			}
			default: {
				System.out.println("comando non valido");
				System.exit(0);
			}
			}

		}

	}

	public static String generateRandomPlayer() {
		return "" + UUID.randomUUID().toString() + "\n" + ++port + "\n";

	}

	static int port = 10000;

	public static void runParallelTest() throws InterruptedException {

		Thread first = new customTest(generateRandomPlayer()
				+ "0\n partita\ntestquit");
		first.start();
		first.join();

		String clients[] = { // generateRandomPlayer()+"1\n\",
		generateRandomPlayer() + "1\ntestquit",
		// generateRandomPlayer()+"1\ntestquit",

		};
		for (String i : clients) {
			new customTest(i).start();
		}

	}

}
