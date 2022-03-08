package uk.ac.mmu.advprog.hackathon;

import static spark.Spark.get;
import static spark.Spark.port;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles the setting up and starting of the web service You will be adding
 * additional routes to this class, and it might get quite large Feel free to
 * distribute some of the work to additional child classes, like I did with DB
 * 
 * @author Gergo Nemeth, 20055710
 */
public class AMIWebService {

	/**
	 * Main program entry point, starts the web service
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		port(8088);

		// Simple route so you can check things are working...
		// Accessible via http://localhost:8088/test in your browser
		get("/test", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				try (DB db = new DB()) {
					return "Number of Entries: " + db.getNumberOfEntries();
				}
			}
		});

		get("/lastsignal", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				try (DB db = new DB()) {

					String signal_id = request.queryParams("signal_id");
					String res = null;
					res = signal_id != null ? db.getLastSignalValue(signal_id) : "no results";

					return "Last signal was: " + res;
				}
			}
		});

		get("/frequency", new Route() { // test this again

			@Override
			public Object handle(Request request, Response response) throws Exception {

				String motorway = request.queryParams("motorway");

				if (motorway == null || motorway == "") {
					return "[]";
				}

				try (DB db = new DB()) {
					response.type("application/json");
					return db.getFrequency(motorway).toString();

				}
			}

		});

		get("/groups", new Route() {

			@Override
			public Object handle(Request request, Response response) {
				try (DB db = new DB()) {
					response.type("application/xml");
					return db.getGroups();
				}
			}
		});

		get("/signalsattime", new Route() {

			@Override
			public Object handle(Request request, Response response) {

				String group = request.queryParams("group");
				String time_raw = request.queryParams("time");

				String time = Helpers.sanitizeURL(time_raw);
				String formatted_time = Helpers.checkTime(time);

				try (DB db = new DB()) {
					response.type("application/xml");
					return db.getSignalAtTime(group, formatted_time);
				}
			}
		});

		System.out.println("Server up! Don't forget to kill the program when done!");
	}

}
