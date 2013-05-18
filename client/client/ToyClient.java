package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.LinkedList;

import common.*;
import communication.Message;
import communication.TokenMessage;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import distributed.CustomMarshaller;
import distributed.ListenDispatcher;
import distributed.MessageDispatcher;
import distributed.MessageHandlerThread;
import distributed.PeerManager;

public class ToyClient {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws JAXBException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws DOMException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException, JAXBException, DOMException, ParserConfigurationException, SAXException, InterruptedException {
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
		
		*/
		
		Player p3=new Player();
		p3.setAddr("localhost");
		p3.setPort(9855);
		
		Player p1=new Player();
		p1.setAddr("localhost");
		p1.setPort(9856);
		
		Player p2=new Player();
		p2.setAddr("localhost");
		p2.setPort(9857);
		
		LinkedList<Player> plist=new LinkedList<Player>();
		plist.add(p1);
		plist.add(p2);
		plist.add(p3);
			
		CustomMarshaller cm=new CustomMarshaller();
		TokenMessage m=new TokenMessage();
		
		MessageDispatcher mh=MessageDispatcher.getMessageDispatcher();
		mh.enqueue(m);
		mh.enqueue(m);
		mh.enqueue(m);
		mh.enqueue(m);
		Thread.sleep(2500);
		mh.enqueue(m);
		mh.enqueue(m);
		mh.enqueue(m);
		
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
