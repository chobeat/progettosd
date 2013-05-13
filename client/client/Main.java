package client;

import common.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.bind.JAXBException;

public class Main {
	ToServer server = new ToServer();
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Main m = new Main();
		m.runClient();
	}

	public void runClient() throws IOException, JAXBException {
		System.out.println("Quale nome vuoi usare?");
		String name = in.readLine();
		System.out.println("Quale porta vuoi usare?");
		int port = Integer.parseInt(in.readLine());
		System.out.println(server.createPlayer(name, port));
		// Match selection
		Match currentMatch = this.matchSelection();
		System.out.println(currentMatch);

	}

	private Match matchSelection() throws IOException, JAXBException {

		try {
			System.out.println("Benvenuto in MMOG! Le partite in corso sono:\n"
					+ server.getMatchList());
			System.out
					.println("Per scegliere una partita digita il numero corrispondente, per crearne una digita 0, per uscire digita q. Cosa vuoi fare?");

		} catch (EmptyMatchListException e) {
			System.out
					.println("Benvenuto in MMOG! Non ci sono partite presenti. Per crearne una digita 0, per uscire digita q. Cosa vuoi fare?");

		}
		String selection;
		Match m = null;

		boolean badID = true;
		while (badID) {
		selection = in.readLine();
		switch (selection) {
		case ("q"):
			System.exit(0);
		break;
		case ("0"):
			m = server.createMatch(askMatch());
			badID=false;
			break;
		default: 
				try {
					m = server.joinMatch(Integer.parseInt(selection));
					badID = false;
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Identificativo non valido");
					badID = true;
				}

			}
		break;
		
		}

		return m;

	}

	String askMatch() throws IOException {
		System.out.println("Inserisci il nome della partita che vuoi creare");
		return in.readLine();

	}

	

}
