package client;

import java.io.StringWriter;
import java.net.URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;
import common.Match;
import common.Player;

public class ToServer {

	ClientConfig config;
	Client client;
	Match[] matchCache;
	Main main;

	public ToServer(Main main) {

		config = new DefaultClientConfig();
		client = Client.create(config);
		this.main = main;
	}

	public Match joinMatch(int localID) throws JAXBException {

		Match match = matchCache[localID - 1];

		final JAXBContext contextP = JAXBContext.newInstance(Player.class);
		final Marshaller marshallerP = contextP.createMarshaller();
		final StringWriter stringWriterP = new StringWriter();

		marshallerP.marshal(main.me, stringWriterP);

		WebResource service = client.resource(getBaseURI());
		Form params = new Form();
		params.add("match", match.getId());

		params.add("player", stringWriterP.toString());

		ClientResponse response = service.path("match").path("join")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, params);

		if (response.getStatus() == 410) {
			matchCache[localID - 1] = null;

			throw new IndexOutOfBoundsException();
		}
		Match m = response.getEntity(Match.class);
		return m;

	}

	public boolean removePlayer(Player p) throws JAXBException {
		final JAXBContext context = JAXBContext.newInstance(Player.class);
		final Marshaller marshaller = context.createMarshaller();
		final StringWriter stringWriter = new StringWriter();

		WebResource service = client.resource(getBaseURI());
		Form params = new Form();
		params.add("match", main.activeMatch.getId());
		marshaller.marshal(p, stringWriter);
		params.add("player", stringWriter.toString());
		System.out.println("Provo a rimuovere "+p.getPort() +" dal match "+main.activeMatch.getId());
		try {
			service.path("match").path("removeplayer")
					.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
					.accept(MediaType.APPLICATION_JSON).post(params);
			// Uso post invece di delete per incompatibilità
			// dell'implementazione di HttpURLConnection
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public boolean endMatch() throws JAXBException {
		final JAXBContext context = JAXBContext.newInstance(Player.class);

		WebResource service = client.resource(getBaseURI());
		Form params = new Form();
		final Marshaller marshaller = context.createMarshaller();
		final StringWriter stringWriter = new StringWriter();

		params.add("match", main.activeMatch.getId());
		marshaller.marshal(main.me, stringWriter);


		params.add("winner", stringWriter.toString());

		try {
			service.path("match").path("end")
					.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
					.accept(MediaType.APPLICATION_JSON)
					.post(Match.class,params);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public Match createMatch(String name) throws JAXBException {
		final JAXBContext context = JAXBContext.newInstance(Player.class);
		final Marshaller marshaller = context.createMarshaller();

		// Create a stringWriter to hold the XML
		final StringWriter stringWriter = new StringWriter();

		WebResource service = client.resource(getBaseURI());
		Form params = new Form();
		params.add("name", name);
		marshaller.marshal(main.me, stringWriter);
		params.add("player", stringWriter.toString());
		ClientResponse cr = service.path("match").path("create")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, params);
		return cr.getEntity(Match.class);

	}

	public Player createPlayer(String name, int port) {
		WebResource service = client.resource(getBaseURI());
		Form params = new Form();
		params.add("name", name);
		params.add("addr", "localhost");

		params.add("port", port);
		common.Player p = (common.Player) service.path("match")
				.path("createplayer")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept(MediaType.APPLICATION_JSON)
				.post(common.Player.class, params);

		return p;
	}

	public Match[] getMatches()throws EmptyMatchListException {
		WebResource service = client.resource(getBaseURI());
		Match[] l = service.path("match").path("list")
				.accept(MediaType.APPLICATION_XML).get(Match[].class);
		return l;
		
	}
	
	public String getMatchList() throws EmptyMatchListException {
		Match[] l=getMatches();
		if (l.length == 0)
			throw new EmptyMatchListException();
		
		String res = "";
		matchCache = l;
		int counter = 0;
		for (Match m : l) {
			res = res + ++counter + "- " + m.name + "\n";
		}
		return res;

	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:9876/progettosd").build();
	}

}
