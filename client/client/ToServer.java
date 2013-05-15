package client;

import java.io.StringWriter;
import java.net.URI;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse.Status;
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
	public ToServer(Main main){
		
		System.out.println("aaa");
		config = new DefaultClientConfig();
		System.out.println("aaa");
		client=Client.create(config);
		System.out.println("aaa");
		this.main=main;
	}
	
	
	public Match joinMatch(int number) throws JAXBException{
		
		Match match=matchCache[number-1];
		
		
		final JAXBContext contextP = JAXBContext.newInstance(Player.class);
		final Marshaller marshallerP = contextP.createMarshaller();
	       final StringWriter stringWriterP = new StringWriter();
	       
		marshallerP.marshal(main.me, stringWriterP); 
     
		WebResource service = client.resource(getBaseURI());
		Form params = new Form();
		params.add("match", match.getID());
		
		params.add("player", stringWriterP.toString());
		
		
		ClientResponse response= service.path("match")
				.path("join")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, params);
		
		if(response.getStatus()==410){
			throw new IndexOutOfBoundsException();
		}
		System.out.println(response.toString());
		return response.getEntity(Match.class);
		
	}
	
	public boolean quit() throws JAXBException{
		final JAXBContext context = JAXBContext.newInstance(Player.class);
		final Marshaller marshaller = context.createMarshaller();
		final StringWriter stringWriter = new StringWriter();
		   
		
		WebResource service = client.resource(getBaseURI());
		Form params = new Form();
		params.add("match", main.activeMatch.getID());
		marshaller.marshal(main.me, stringWriter);
		params.add("player", stringWriter.toString());
		try{service.path("match")
				.path("removeplayer")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept(MediaType.APPLICATION_JSON)
				.post(common.Match.class, params);
		}
		catch(Exception e){return false;}
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
		common.Match m = (common.Match) service.path("match")
				.path("create")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept(MediaType.APPLICATION_JSON)
				.post(common.Match.class, params);

		return m;

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
		main.me = p;
		return p;
	}

	public String getMatchList() throws EmptyMatchListException {
		WebResource service = client.resource(getBaseURI());
		Match[] l = service.path("match").path("list")
				.accept(MediaType.APPLICATION_XML).get(Match[].class);
		if (l.length == 0)
			throw new EmptyMatchListException();
		String res = "";{
			System.out.println("Sono morto");
			
		}
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
