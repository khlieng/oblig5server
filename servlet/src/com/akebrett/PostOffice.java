package com.akebrett;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

@Path("/tomcat")
public class PostOffice {
	private static final String API_KEY = "AIzaSyBEqUE_rU3owjLkHFq8mxl8-r6wKnv4hY0";
	private static final Sender sender = new Sender(API_KEY);
	private static Random rand = new Random();

	//
	// Registrerer bruker, putter den i databasen og sender en id tilbake
	//
	@Path("/register")
	@POST
	public void register(@FormParam("regId") String gcmId, @FormParam("name") String name) {
		User user = Users.get(gcmId);
		if (user != null) {
			Message msg = new Message.Builder().addData("cmd", "id").addData("id", user.getId() + "").build();
			pushMessageGCM(msg, gcmId);
			pushMessageWeb("areg:" + user.getId());
		}
		else {
			User newUser = new User(gcmId, name);
			int[] color = createColor();
			newUser.setColor(color[0] + "," + color[1] + "," + color[2]);
			
			// Putter brukeren i databasen og sender iden tilbake
			int id = Users.insert(newUser);
			Message msg = new Message.Builder().addData("cmd", "id").addData("id", id + "").build();
			pushMessageGCM(msg, gcmId);
			
			// Forteller alle om den nye brukeren
			Message msgNew = new Message.Builder()
				.addData("cmd", "new")
				.addData("id", id + "")
				.addData("name", name).build();
			pushMessageGCM(msgNew);
			
			pushMessageWeb("reg:" + id + ":" + name + ":" + newUser.getColor());
		}
	}
	
	//
	// Avregistrerer bruker
	//
	@Path("/unregister")
	@POST
	public void unregister(@FormParam("id") int id) {
		dropUser(id);
	}
	
	//
	// Tar imot en melding og sender den videre dit den skal
	//
	@Path("/send")
	@POST
	public void sendMessage(@FormParam("msg") String message, @FormParam("id") int id, @FormParam("rcv") String receiver) {
		User user = Users.get(id);
		
		// Meldingen kommer fra android
		if (user != null) {
			pushMessageWeb("msg:" + id + ":" + message);
			Message.Builder builder = new Message.Builder()
				.addData("cmd", "message")
				.addData("snd", user.getName())
				.addData("msg", message)
				.addData("clr", user.getColor());
				
			// Sjekker om meldingen er privat
			if (receiver != null && !receiver.equals("")) {
				User rcv = Users.getByName(receiver);
				builder.addData("des", "1");
				pushMessageGCM(builder.build(), rcv.getGcmId());
			}
			else {
				builder.addData("des", "2");
				pushMessageGCM(builder.build());
			}
		}
		// Meldingen kommer fra web
		else {
			pushMessageWeb("msg:-1:" + message);
			Message msg = new Message.Builder()
				.addData("cmd", "message")
				.addData("snd", "Web")
				.addData("msg", message)
				.addData("des", "2")
				.addData("clr", "0,0,0").build();
			pushMessageGCM(msg);
		}
	}
	
	//
	// Tar imot en posisjonsoppdatering og sender den videre til alle
	//
	@Path("/pos")
	@POST
	public void updatePos(@FormParam("lat") String lat, @FormParam("lng") String lng, @FormParam("id") int id) {
		User user = Users.get(id);

		if (user != null) {
			Message msg = new Message.Builder()
				.addData("cmd", "pos")
				.addData("lat", lat)
				.addData("lng", lng)
				.addData("id", id + "")
				.addData("clr", user.getColor()).build();
			pushMessageGCM(msg);
			pushMessageWeb("pos:" + lat + ":" + lng + ":" + id + ":" + user.getColor() + ":" + user.getName());
			pushMessageWeb("msg:-1:" + lat + "," + lng + "," + id);
					
			user.setLastPos(lat + "," + lng);
			Users.update(user);
		}
	}
	
	//
	// Sender tilbake en tabseparert liste over registrerte brukere
	//
	@Path("/users")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getUsers() {
		StringBuilder result = new StringBuilder();
		
		List<User> users = Users.getAll();
		for (User user : users) {
			result.append(user.getId() + "\t");
			result.append(user.getName() + "\t");
			result.append(user.getlastPos() + "\t");
			result.append(user.getColor() + "\n");
		}
		
		return result.toString();
	}
	
	//
	// Fjerner brukeren fra databasen og sender en melding til alle om det
	//
	private void dropUser(int id) {
		User user = Users.get(id);

		if (user != null) {
			Message msg = new Message.Builder()
				.addData("cmd", "drop")
				.addData("id", id + "")
				.addData("name", user.getName()).build();
			pushMessageGCM(msg);
			pushMessageWeb("drop:" + id);
			
			Users.delete(id);
		}
	}
	
	//
	// Skyver meldingen til alle webklienter
	//
	private void pushMessageWeb(String message) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("msg", message);
		HTTP.POST("http://localhost:3001/push", params);
	}
	
	//
	// Skyver meldingen til alle GCM-klienter
	//
	private void pushMessageGCM(Message message) {
		List<String> gcmIds = Users.getGcmIds();
		if (gcmIds.size() > 0) {
			try {
				sender.send(message, gcmIds, 5);
			} catch (Exception e) { }
		}
	}
	
	//
	// Skyver meldingen til den spesifisert GCM-klienten
	//
	private void pushMessageGCM(Message message, String id) {
		try {
			sender.send(message, id, 5);
		} catch(Exception e) { }
	}
	
	private int[] createColor() {
		int[] c = new int[3];
		boolean done = false;
		while (!done) {
			c[0] = rand.nextInt(256);
			c[1] = rand.nextInt(256);
			c[2] = rand.nextInt(256);
			
			// Sjekke at fargen ikke er for mørk
			if (c[0] > 127 || c[1] > 127 || c[2] > 127) {
				done = true;
			}
		}
		return c;		
	}
}
