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
	public ToServer(Main main){
		
		config = new DefaultClientConfig();
		client=Client.create(config);
		this.main=main;
	}
	
	
	public Match joinMatch(int localID) throws JAXBException{
		
		Match match=matchCache[localID-1];
		
		
		final JAXBContext contextP = JAXBContext.newInstance(Player.class);
		final Marshaller marshallerP = contextP.createMarshaller();
	       final StringWriter stringWriterP = new StringWriter();
	       
		marshallerP.marshal(main.me, stringWriterP); 
     
		WebResource service = client.resource(getBaseURI());
		Form params = new Form();
		params.add("match", match.getId());
		
		params.add("player", stringWriterP.toString());
		
		
		ClientResponse response= service.path("match")
				.path("join")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, params);
		
		
		if(response.getStatus()==410){
			matchCache[localID-1]=null;
		
			throw new IndexOutOfBoundsException();
		}

		return response.getEntity(Match.class);
		
	}
	
	public boolean quit() throws JAXBException{
		final JAXBContext context = JAXBContext.newInstance(Player.class);
		final Marshaller marshaller = context.createMarshaller();
		final StringWriter stringWriter = new StringWriter();
		   
		
		WebResource service = client.resource(getBaseURI());
		Form params = new Form();
		params.add("match", main.activeMatch.getId());
		marshaller.marshal(main.me, stringWriter);
		params.add("player", stringWriter.toString());
		try{service.path("match")
				.path("removeplayer")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept(MediaType.APPLICATION_JSON)
				.delete(common.Match.class, params);
		}
		catch(Exception e){return false;}
		return true;
		
	}

	public boolean endMatch() throws JAXBException{
			   
		
		WebResource service = client.resource(getBaseURI());
		
		try{service.path("match")
				.path("removematch")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept(MediaType.APPLICATION_JSON)
				.delete(Match.class,main.activeMatch.getId());
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
