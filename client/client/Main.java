package client;

import common.*;
import communication.MoveMessage;
import game.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;

import org.eclipse.jdt.core.compiler.InvalidInputException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

import distributed.PeerManager;

import test.customTest;

public class Main {
	public ToServer server;
	BufferedReader in;
	public Player me;
	Match activeMatch;
	PeerManager peerManager;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		cleanServer();
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
		me = server.createPlayer(name, port);
		// Match selection
		activeMatch = this.matchSelection();
		System.out.println("Match selezionato");
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
		System.out.println("Inizio partita " + activeMatch.name);

		peerManager = new PeerManager(this, me, activeMatch.playerList);
		
		peerManager.startMatch();
		String selection;
		synchronized(peerManager.tm.tokenWaiter){
			
		try {
			peerManager.tm.tokenWaiter.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		while (true) {
		selection = in.readLine();
		
			if (selection == null)
				continue;
			try {
				Thread.sleep(150);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				Position p=peerManager.game.move(selection);
				MoveMessage m=new MoveMessage();
				m.sender=me;
				try {
					m.newPosition=p.clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				peerManager.sendAllWithMoveAckAtToken(m);
			} catch (InvalidInputException e) {
			System.out.println("Input non valido. Riprova");
				continue;
			}

		}

	}

	public static String generateRandomPlayer() {
		return "" + UUID.randomUUID().toString() + "\n" + ++port + "\n";

	}

	static int port = 13333;

	public static void runParallelTest() throws InterruptedException,
			IOException, JAXBException {

		Thread first = new customTest(generateRandomPlayer() + "0\n partita\n");
		first.start();
		first.join(1000);
		String clients[] = {

				generateRandomPlayer() +"1\n",
				generateRandomPlayer() +"1\n",
				generateRandomPlayer() +"1\n",
		generateRandomPlayer() +"1\n"+generateRandomMoves(),
		generateRandomPlayer() +"1\n",
		generateRandomPlayer() +"1\n",
		generateRandomPlayer() +"1\n"+generateRandomMoves(),
		generateRandomPlayer() +"1\n"+generateRandomMoves(),  };
		for (String i : clients) {
			Thread t = new customTest(i);
			t.start();
			Thread.sleep(100);
		}
	}

	public static String generateRandomMoves(){
		String res="";
		for(int i=00;i<99;i++){
			res=res+((new Random()).nextInt(4)+1)+"\n";
			
		}
		return res;
		
	}
	public static void cleanServer() {

		ClientConfig config;
		Client client;
		config = new DefaultClientConfig();
		client = Client.create(config);

		WebResource service = client.resource(UriBuilder.fromUri(
				"http://localhost:9876/progettosd").build());

		Form form = new Form();
		form.add("id", 1);
		try {
			service.path("match").path("endall")
					.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
					.accept(MediaType.APPLICATION_XML).post(form);
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
