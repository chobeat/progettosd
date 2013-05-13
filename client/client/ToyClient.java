package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import common.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

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
	 */
	public static void main(String[] args) {
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
		ToServer server = new ToServer();
		
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
