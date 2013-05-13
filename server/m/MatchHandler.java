package m;

import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.xml.internal.ws.util.Pool.Unmarshaller;

import common.Match;
import common.Player;

// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/match")
public class MatchHandler {
  // This method is called if TEXT_PLAIN is request
	Server s;

	
	
 public	 MatchHandler(){
	 s=Server.getServer();
 }
  @POST
  @Path("/join")

  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)

  @Produces(MediaType.APPLICATION_JSON)
  public Response joinMatch(MultivaluedMap<String, String> formParams) throws JSONException {
	 
	  Player player=JAXB.unmarshal(new StringReader(formParams.get("player").get(0)), Player.class);
	  int ID= Integer.parseInt(formParams.get("match").get(0));
	  Match m=s.joinMatch(player, ID);
	  return Response.ok(m).build();
  }

  @POST
  @Path("/create")

  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)

  @Produces(MediaType.APPLICATION_JSON)
  public Response createMatch(MultivaluedMap<String, String> formParams) throws JSONException {
	  
	  
	  Player starter=JAXB.unmarshal(new StringReader(formParams.get("player").get(0)), Player.class);
	  Match m=s.createMatch(formParams.get("name").get(0),starter);
	  return Response.ok(m).build();
  
  }
  
  @GET
  @Path("/list")
  @Produces(MediaType.APPLICATION_XML)
  
  public Match[] matchList()throws JSONException{
	  
	 Match[]  list= s.getMatchArray();
	  return list;
  }
  
  @POST
  @Path("/createplayer")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)

  @Produces(MediaType.APPLICATION_JSON)
  public Response createPlayer(MultivaluedMap<String, String> formParams) throws JSONException {
	   
	  
	  //Player p= JAXB.unmarshal(new StringReader(data), Player.class);
		Player p=s.createPlayer(formParams.get("name").get(0),formParams.get("addr").get(0),Integer.parseInt(formParams.get("port").get(0)));
	  
	  return Response.ok(p).build();
  
  }
  
  

} 