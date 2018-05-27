package com.mock.skybus.web.beans;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mock.skybus.web.clients.BookClient;
import com.mock.skybus.web.clients.FlightClient;
import com.mock.skybus.web.clients.LocationClient;
import com.mock.skybus.web.clients.UserClient;
import com.mock.skybus.web.models.mvc.FlightsWrapper;
import com.mock.skybus.web.models.orm.Book;
import com.mock.skybus.web.models.orm.Flight;
import com.mock.skybus.web.models.orm.Location;
import com.mock.skybus.web.models.orm.User;

/**
 * Underlying java operations for travelBean.xhtml, so most methods are tied to
 * buttons in that GUI. Paths has many path, it represents a list of flights the
 * the user can book and is populated when the user searches for a flight for a
 * chosen origin and destination.
 * 
 * @author ZGT43
 *
 */
@Component
@Scope("session")
public class TravelBean {

	Logger log = LoggerFactory.getLogger(LoginBean.class);

	@Autowired
	AuthBean authBean;

	FlightClient flightClient = new FlightClient();
	LocationClient locationClient = new LocationClient();
	BookClient bookClient = new BookClient();
	UserClient userClient = new UserClient();

	Location origin = new Location();
	Location destination = new Location();
	Location location = new Location();
	List<Location> locations;

	List<FlightsWrapper> paths = new ArrayList<FlightsWrapper>();
	FlightsWrapper path = new FlightsWrapper();

	/**
	 * When a user clicks on the set origin button in the GUI, this method is
	 * fired to set the origin to the chosen location. The value of the location
	 * is variable is determined by the Travel.xhtml.
	 */
	public void setOrigin() {
		log.info("entering TravelBean.setOrigin()");
		origin = location;
		log.info("leaving TravelBean.setOrigin()");
	}

	/**
	 * Sets the destination variable to the location chosen by the user.
	 */
	public void setDestination() {
		log.info("entering TravelBean.setDestination()");
		destination = location;
		log.info("leaving TravelBean.setDestination()");
	}

	/**
	 * Calls flightClient.getFlights(origin, destination) to make an HTTP
	 * request to the back end to retrieve a list of flights that the user could
	 * book from the chosen origin to the chosen destination.
	 */
	public void search() {
		log.info("entering TravelBean.search()");
		paths = flightClient.getFlights(origin, destination);
		log.info("leaving TravelBean.search()");
	}

	/**
	 * Sends a Book object to the back end to create a field in the book table
	 * that represents the relationship between the flights and the user.
	 * Conceptually it is a single ticket. If all the seats on a flight are
	 * filled, any other fair displayed on the page with that flight is removed.
	 */
	public void book() {
		log.info("entering TravelBean.book()");
		List<Book> books = new ArrayList<>();

		User user = new User();
		user = userClient.getUser(authBean.getUsername());

		List<Flight> flights = path.getList();
		for (Flight flight : flights) {
			Book book = new Book();
			book.setUser(user);
			book.setFlight(flight);
			books.add(book);
			log.warn("Booking flight {} for user {} ", flight.getId(), user.getName());

			if (flight.getSeats() == 5) {
				for (FlightsWrapper hopper : paths) {
					List<Flight> hops = path.getList();
					for (Flight hop : hops) {
						if (hop.getSeats() == 5) {
							log.warn("Other flights in other paths have been filled, removing {} from the display", hopper.getDistance());
							paths.remove(hopper);
						}
					}
				}
			}
		}

		bookClient.saveBook(books);

		if (paths.contains(path)) {
			paths.remove(path);
		}
		log.info("leaving TravelBean.book()");
	}

	/**
	 * Navigate the user to the travel page. populate the locations list with
	 * the available locations by calling a method that sends and HTTP request
	 * to the back end to retrieve the locations from the database.
	 * 
	 * @return
	 */
	public String travel() {
		log.info("entering TravelBean.travel()");

		origin = new Location();
		destination = new Location();
		location = new Location();
		locations = locationClient.getLocations();

		log.info("leaving TravelBean.travel()");
		return "travel";
	}

	public Location getOrigin() {
		return origin;
	}

	public void setOrigin(Location origin) {
		this.origin = origin;
	}

	public Location getDestination() {
		return destination;
	}

	public void setDestination(Location destination) {
		this.destination = destination;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public List<FlightsWrapper> getPaths() {
		return paths;
	}

	public void setPaths(List<FlightsWrapper> paths) {
		this.paths = paths;
	}

	public FlightsWrapper getPath() {
		return path;
	}

	public void setPath(FlightsWrapper path) {
		this.path = path;
	}
}
