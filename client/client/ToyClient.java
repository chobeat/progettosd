package client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import common.*;
import communication.DeathMessage;
import communication.TokenMessage;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.*;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class ToyClient {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException {
		// TODO Auto-generated method stub

		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		// Fluent interfaces
		/*Form params= new Form();
	    params.add("name", "nome");
	    params.add("addr", "localhost");

	    params.add("port", 5555);
		common.Player p=(common.Player)service.path("match").path("createplayer").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON).post(common.Player.class,params);
		System.out.println(p.getName());
*/
		TokenMessage m=new TokenMessage();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(communication.TokenMessage.class);

			final Marshaller marshaller = context.createMarshaller();

	        final StringWriter stringWriter = new StringWriter();

			marshaller.marshal(m, stringWriter);	
			String onTheNet=stringWriter.toString();
			System.out.println(onTheNet);
			//--------------------------------------
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new ByteArrayInputStream(onTheNet.getBytes()));
			doc.getDocumentElement().normalize();
			 doc.getElementById("type");
			communication.Message received=(communication.Message) JAXB.unmarshal(new StringReader(onTheNet), Class.forName(doc.getElementsByTagName("type").item(0).getTextContent()));
			Class.forName(received.type).cast(received);  
			received.execute();
		} catch (JAXBException | SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public void runClient() throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int i=0;
		while(i++<4){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(in.readLine());
				
			
		}
	}
	
	private static URI getBaseURI() {
		return UriBuilder.fromUri(
				"http://localhost:9876/progettosd").build();
	}

}
