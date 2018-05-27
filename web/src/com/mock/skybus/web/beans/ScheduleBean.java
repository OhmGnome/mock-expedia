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
import com.mock.skybus.web.clients.UserClient;
import com.mock.skybus.web.models.mvc.FlightsWrapper;
import com.mock.skybus.web.models.mvc.PathsWrapper;
import com.mock.skybus.web.models.orm.Book;
import com.mock.skybus.web.models.orm.Flight;
import com.mock.skybus.web.models.orm.Location;
import com.mock.skybus.web.models.orm.User;

/**
 * underlying java operations for scheduleBean.xhtml, so most methods are tied
 * to buttons in that GUI. Trips has many trip, it represents a list of flights
 * that the user has booked from one location to another. It is the object
 * manipulatable by schedule.xhtml Paths has many path, it represents a list of
 * flights the the user can book and is populated when the user attempts to
 * reroute a flight that the user has booked. It is the other object
 * manipulatable by schedule.xhtml
 * 
 * @author ZGT43
 *
 */
@Component
@Scope("session")
public class ScheduleBean {

	Logger log = LoggerFactory.getLogger(ScheduleBean.class);

	@Autowired
	AuthBean authBean;
	@Autowired
	LoginBean loginBean;

	FlightClient flightClient = new FlightClient();
	BookClient bookClient = new BookClient();
	UserClient userClient = new UserClient();

	List<FlightsWrapper> paths = new ArrayList<FlightsWrapper>();
	FlightsWrapper path = new FlightsWrapper();

	List<FlightsWrapper> trips = new ArrayList<FlightsWrapper>();
	FlightsWrapper trip = new FlightsWrapper();

	/**
	 * trip.getDistance() is a tripId in this case scheduleBean.xhtml selects a
	 * trip. When he cancel button is pressed, cancel() is fired. Cancel() calls
	 * on the backend to book a relation between the flights and the user. When
	 * the trip is canceled, it is removed from the list of booked flights.
	 */
	public void cancel() {
		log.info("entering ScheduleBean.cancel()");
		
		Flight flight = trip.getList().get(0);
		Book book = new Book();
		User user = userClient.getUser(authBean.getUsername());
		book.setTrip(trip.getDistance());
		book.setFlight(flight);
		book.setUser(user);
		bookClient.cancel(book);
		log.warn("cancelling flights for trip id {} for user {}", book.getTrip(), user.getName());
		trips.remove(trip);
	
		log.info("leaving ScheduleBean.cancel()");
	}

	/**
	 * The user may choose to reroute a travel plan. Flight paths are generated
	 * in the backend by an origin and a destination location. the origin is set
	 * to the origin of the first flight in the list, because that respects the
	 * order in which the flights were retrieved from the backend, and
	 * represents the current location of the user. If a flight in the list of
	 * flights to be canceled is postponed, set the origin to that flight
	 * instead. The destination is set to the destination of the last flight in
	 * the list.
	 */
	public void reroute() {
		log.info("entering scheduleBean.reroute()");
		Location origin = new Location();
		Location destination = new Location();
		Location home = new Location();

		List<Flight> flights = trip.getList();
		Flight flight = flights.get(0);
		origin = flight.getLocationByOrigin();

		for (Flight hop : flights) {
			if (hop.isPostponed() && hop.getDeparture() > -1) {
				origin = hop.getLocationByOrigin();
				log.warn("delay detected, rerouting travel plan at {} {}",
						origin.getCity(), origin.getState());
			}
		}

		home = flight.getLocationByOrigin();

		flight = flights.get(flights.size() - 1);
		destination = flight.getLocationByDestination();
		log.warn("rerouting travel plan to {} {}", destination.getCity(), destination.getState());
		
		paths = flightClient.reroute(origin, destination, home);
		cancel();
		log.info("leaving scheduleBean.reroute()");
	}

	/**
	 * After a scheduled fair has been rerouted the user may book a new fair. if
	 * a fair that the user has just booked contains a flight that has an amount
	 * of passengers just under the limit (such that it will be over when it is
	 * booked), remove that fair from both of the lists. when a new fair is
	 * booked, remove that fair from the routing list on the right and add it to
	 * the booked fairs list on the left.
	 */
	public void book() {
		log.info("entering ScheduleBean.book()");
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
							trips.remove(hopper);
						}
					}
				}
			}
		}

		bookClient.saveBook(books);
		trips.add(path);
		if (paths.contains(path)) {
			paths.remove(path);
		}
		log.info("leaving ScheduleBean.book()");
	}

	/**
	 * Preloads data pertinent to scheduleBean and schedule.xhtml, the travel
	 * plans that the user has booked. Checks for delays related to the user, If
	 * delays are detected, allow a boolean in the login bean to display a
	 * warning to the user that a flight that the user has booked has been
	 * delayed.
	 * 
	 * @return
	 */
	public String schedule() {
		log.info("entering ScheduleBean.schedule()");
		
		PathsWrapper paths = flightClient.getFlights(authBean.getUsername());
		if (paths.getDelays()) {
			loginBean.setFlightDelayed(true);
			log.warn("delay for {} detected", authBean.getUsername());
		}
		trips = paths.getPaths();
		
		log.info("leaving ScheduleBean.schedule()");
		return "schedule";
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

	public List<FlightsWrapper> getTrips() {
		return trips;
	}

	public void setTrips(List<FlightsWrapper> trips) {
		this.trips = trips;
	}

	public FlightsWrapper getTrip() {
		return trip;
	}

	public void setTrip(FlightsWrapper trip) {
		this.trip = trip;
	}
}