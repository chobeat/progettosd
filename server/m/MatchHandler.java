package m;

import java.io.StringReader;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXB;
import org.codehaus.jettison.json.JSONException;
import common.Match;
import common.Player;

@Path("/match")
public class MatchHandler {

	Server s;

	public MatchHandler() {
		s = Server.getServer();
	}

	@PUT
	@Path("/join")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response joinMatch(MultivaluedMap<String, String> formParams)
			throws JSONException {
		Player player = JAXB.unmarshal(new StringReader(formParams
				.get("player").get(0)), Player.class);
		int ID = Integer.parseInt(formParams.get("match").get(0));
		//Richiedo l'entrata nel match identificato da ID
		Match m = s.joinMatch(player, ID);
		//se non ho successo, ritorno 410
		if (m == null) {
			return Response.status(Status.GONE).build();
		}
		//altrimenti ritorno il match
		return Response.ok(m).build();
	}

	
	@GET
	@Path("/view/{id}")
	@Produces(MediaType.TEXT_HTML)
	public Response view(@PathParam(value = "id") int id) throws JSONException {
		
		Match arr[] = s.getMatchArray();
		return Response.ok(arr[id].toString()).build();
	}

	// Uso post invece di delete per incompatibilità dell'implementazione di
	// HttpURLConnection
	@POST
	@Path("/removeplayer")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removePlayer(MultivaluedMap<String, String> formParams)
			throws JSONException {

		Player player = JAXB.unmarshal(new StringReader(formParams
				.get("player").get(0)), Player.class);
		int id = Integer.parseInt(formParams.get("match").get(0));
		System.out.println("Rimuovo " + id);
		s.removePlayer(id, player);
		return Response.ok().build();
	}

	@POST
	@Path("/end")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_XML)
	public Response endMatch(MultivaluedMap<String, String> formParams)
			throws JSONException {
		int id = (Integer.parseInt(formParams.get("id").get(0)));
		//Provo a terminare il match  identificato da ID
		if (s.endMatch(id))
			return Response.ok().build();
		else
			return Response.notModified().build();
	}

	@POST
	@Path("/endall")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_XML)
	public Response endAllMatch(MultivaluedMap<String, String> formParams)
			throws JSONException {
		//termino tutti i match e pulisco il server
		s.endAllMatch();
		return Response.ok().build();
	}

	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMatch(MultivaluedMap<String, String> formParams)
			throws JSONException {

		Player starter = JAXB
				.unmarshal(new StringReader(formParams.get("player").get(0)),
						Player.class);
		//creo un match con un nome ed uno starter. Lo ritorno al chiamante
		Match m = s.createMatch(formParams.get("name").get(0), starter);
		return Response.ok(m).build();

	}

	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_XML)
	public Match[] matchList() throws JSONException {

		Match[] list = s.getMatchArray();
		return list;
	}

	@POST
	@Path("/createplayer")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPlayer(MultivaluedMap<String, String> formParams)
			throws JSONException {

		//Creo un giocatore e lo ritorno
		Player p = s.createPlayer(formParams.get("name").get(0), formParams
				.get("addr").get(0), Integer.parseInt(formParams.get("port")
				.get(0)));

		return Response.ok(p).build();

	}

}