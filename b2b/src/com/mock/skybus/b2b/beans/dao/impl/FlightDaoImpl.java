package com.mock.skybus.b2b.beans.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mock.skybus.b2b.beans.dao.FlightDao;
import com.mock.skybus.b2b.models.mvc.FlightsWrapper;
import com.mock.skybus.b2b.models.mvc.PathsWrapper;
import com.mock.skybus.b2b.models.orm.Book;
import com.mock.skybus.b2b.models.orm.Flight;
import com.mock.skybus.b2b.models.orm.Location;
import com.mock.skybus.b2b.mvc.controller.FlightController;

/**
 * Implement the flightDao interface and access the database for the Flight
 * object model
 * 
 * @author ZGT43
 *
 */
@Repository
@Transactional
public class FlightDaoImpl implements FlightDao {

	private Logger log = LoggerFactory.getLogger(FlightController.class);

	@Autowired
	SessionFactory factory;

	/**
	 * Save a new flight object to the database. Used for persisting the Flight
	 * objects in the FlightModel object
	 */
	@Override
	public void saveFlight(Flight flight) {
		log.info("entering FlightDaoImpl.saveFlight(Flight");
		Session session = factory.getCurrentSession();
		session.save(flight);
		log.info("leaving FlightDaoImpl.saveFlight(Flight");
	}

	/**
	 * Update a flight object that already exists in the database with new data
	 * values. Used for persisting the Flight objects in the FlightModel object
	 */
	@Override
	public void updateFlight(Flight flight) {
		log.info("entering FlightDaoImpl.updateFlight(Flight {})",
				flight.getId());
		Session session = factory.getCurrentSession();
		session.update(flight);
		log.info("leaving FlightDaoImpl.updateFlight(Flight");
	}

	/**
	 * Retrieves a list of all flights from the database to display on the
	 * homepage.
	 */
	@Override
	public List<Flight> getFlights() {
		log.info("entering and leaving FlightDaoImpl.getFlights()");
		Session session = factory.getCurrentSession();
		return session.createQuery("FROM Flight ORDER BY id ASC").list();
	}

	/**
	 * retrieves all the flights for a user using the book relations table
	 * represents a fair for passage from an origin to a destination including
	 * many flights. Gets a list of conceptual tickets that the user has booked
	 * actual flights for. The flights are packaged along with extra details for
	 * transport across REST.
	 */
	@Override
	public PathsWrapper getFlights(String username) {
		Session session = factory.getCurrentSession();
		log.info("entering FlightDaoImpl.getFlights(username) {}", username);
		List<Book> books = session
				.createQuery(
						"SELECT DISTINCT b FROM Book b, User u, Flight f WHERE b.user.name = :username AND b.canceled = 0")
				.setString("username", username).list();

		List<Integer> fairs = session
				.createQuery(
						"SELECT DISTINCT b.trip FROM Book b, User u, Flight f WHERE b.user.name = :username AND b.canceled = 0")
				.setString("username", username).list();

		log.warn(
				"Query retrieved a list of bookings of size {} , and a list of fairs of size {}",
				books.size(), fairs.size());

		List<FlightsWrapper> path = new ArrayList<>();
		PathsWrapper paths = new PathsWrapper();

		Boolean delays = false;

		/*
		 * separate the flights by trip id if a flight is delayed, the boolean
		 * delays is set to package that fact in the ticket (flightsWrapper)
		 */
		for (Integer fair : fairs) {
			List<Flight> flights = new ArrayList<>();
			for (Book book : books) {
				if (fair == book.getTrip()) {
					flights.add(book.getFlight());
					if (book.getFlight().isPostponed()) {
						delays = true;
						log.warn(
								"A delay was found in fair number {} for user {} ",
								fair, username);
					}
				}
			}

			/*
			 * determine if all the flights in a path have arrived, do not
			 * propagate the path
			 */
			Boolean allArried = true;
			for (Flight flight : flights) {
				if (!flight.isArrived()) {
					allArried = false;
					log.warn("Fair {} has expired for user {}", fair, username);
				}
			}

			/*
			 * unless all the flights in a path have arrived propagate the path
			 */
			if (!allArried) {
				FlightsWrapper flightsWrapper = new FlightsWrapper();
				flightsWrapper.setList(flights);
				flightsWrapper.setDistance(fair);
				path.add(flightsWrapper);
				log.warn("Propagating fair {} for user {}", fair, username);
			}

		}

		/*
		 * use the distance variable in flight wrapper to represent the trip id.
		 */
		paths.setPaths(path);
		paths.setDelays(delays);

		log.info("leaving FlightDaoImpl.getFlights(username) {} with {} trips",
				username, path.size());
		return paths;
	}

	/*
	 * Based on the users selected origin and location, get a list of flights
	 * from the origin, and a list of flights from the destination. Also get a
	 * list of all flights from the database. Call a method to use those lists
	 * to find paths from the origin to the destination.
	 */
	@Override
	public List<FlightsWrapper> getFlights(Location origin, Location destination) {
		log.info("entering FlightDaoImpl.getFlights( {} {},  {} {})",
				origin.getCity(), origin.getState(), destination.getCity(),
				destination.getState());
		Session session = factory.getCurrentSession();
		List<Flight> flightsFromOrigin = session
				.createQuery(
						"SELECT f FROM Flight f WHERE f.locationByOrigin IN (SELECT l FROM Location l WHERE l.city LIKE :city AND l.state LIKE :state) AND seats < 6 AND departure > -1 AND arrived = 0")
				.setString("city", origin.getCity())
				.setString("state", origin.getState()).list();
		List<Flight> flights = session
				.createQuery(
						"FROM Flight f WHERE seats < 6 AND arrived = 0  AND departure > 0")
				.list();
		List<Flight> flightsToDestination = session
				.createQuery(
						"SELECT f FROM Flight f WHERE f.locationByDestination IN (SELECT l FROM Location l WHERE l.city LIKE :city AND l.state LIKE :state) AND seats < 6 AND departure > -1 AND arrived = 0")
				.setString("city", destination.getCity())
				.setString("state", destination.getState()).list();

		log.warn(
				"Retrieved 3 lists of flights from the database of size; flightsFromOrigin:{} flights:{} flightsToDestination:{}",
				flightsFromOrigin.size(), flights.size(),
				flightsToDestination.size());

		List<FlightsWrapper> paths = mapPaths(origin, destination, flights,
				flightsFromOrigin, flightsToDestination);

		log.info("leaving FlightDaoImpl.getFlights( {} {},  {} {})",
				origin.getCity(), origin.getState(), destination.getCity(),
				destination.getState());
		return paths;
	}

	/**
	 * Get a flight path from an origin location to a destination location by
	 * matching one flights destination to the origin of another, using three
	 * lists to sequentially iterate and compare locations. At every conditional
	 * statement, when the if statement is met, a path from the origin to the
	 * destination is found, the path is packaged within a flightWrapper for
	 * REST transport and another iteration starts. The etas of each flight are
	 * summed to represent the total distance of the flight and packaged with
	 * the flightWrapper. By the end of the function a list of flightsWrapper
	 * objects have been generated.
	 * @param origin
	 * @param destination
	 * @param flights
	 * @param flightsFromOrigin
	 * @param flightsToDestination
	 * @return
	 */
	private List<FlightsWrapper> mapPaths(Location origin,
		Location destination, List<Flight> flights,
		List<Flight> flightsFromOrigin, List<Flight> flightsToDestination) {

		log.info("entering FlightDaoImpl.mapPaths()");

		List<FlightsWrapper> paths = new ArrayList<>();
		for (Flight flight1 : flightsFromOrigin) {
			Integer distance = 0;
			Integer flight1DestId = flight1.getLocationByDestination().getId();

			/*
			* if a single flight exists form the desired origin to the desired
			* destination
			*/
			if (flight1DestId == destination.getId()) {
				List<Flight> path = new ArrayList<>();
				path.add(flight1);
				distance = flight1.getEta();

				FlightsWrapper flightsWrapper = new FlightsWrapper();
				flightsWrapper.setList(path);
				flightsWrapper.setDistance(distance);
				paths.add(flightsWrapper);

				log.warn(
						"A direct flight was found, flight number {} with distance {}",
						flight1.getId(), distance);

			
			/*
			* divide and conquer all the flights to find the path for up to 2 hops.
			*/
			} else {
				ArrayList<PathsWorker> pathsWorkers = new ArrayList<>();
				PathsWorker pathsWorker = new PathsWorker();
				pathsWorker.destination = destination;
				pathsWorker.flight1 = flight1;
				pathsWorker.flightsToDestination = flightsToDestination;
				
				if (flights.size() > 3){
					int size = (int) Math.floor(flights.size()/4);
					for (int i = 0; i < 4; i++){
						int start = size * i;
						int end;
						if (i != 3) end = size * (i + 1);
						else end = flights.size() - 1;

						pathsWorker.flights = flights.subList(start, end);
						
						try {
							PathsWorker clone = (PathsWorker) pathsWorker.clone();
							clone.paths = paths;
							pathsWorkers.add(clone);
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
					}
				}else{
					pathsWorker.paths = paths;
					pathsWorkers.add(pathsWorker);
				}

				for (PathsWorker p : pathsWorkers){
					p.join();
				}
			}
		}
		log.info("leaving FlightDaoImpl.mapPaths() with {} paths", paths.size());
		return paths;
	}


	private class PathsWorker implements Runnable, Cloneable{
		Flight flight1;
		List<Flight> flights;
		List<Flight> flightsToDestination;
		Location destination;
		volatile List<FlightsWrapper> paths;

		Integer distance = 0;

		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

		@Override
		public void run() {
			for (Flight flight2 : flights) {
				Integer flight2OriginId = flight2.getLocationByOrigin()
						.getId();
				Integer flight2DestId = flight2.getLocationByDestination()
						.getId();

				/*
				* if another flight's origin is that of the first flights
				* destination it is circular and should be skipped
				*/
				if (flight2OriginId == flight1.getLocationByDestination().getId()) {

					/*
					* if the destination of flight 2 = the desired
					* destination
					*/
					if (flight2DestId == destination.getId()
							&& flight1.getEta() + flight1.getDeparture() > flight2
									.getDeparture()) {
						List<Flight> path = new ArrayList<>();
						path.add(flight1);
						path.add(flight2);
						distance = flight1.getEta() + flight2.getEta();

						FlightsWrapper flightsWrapper = new FlightsWrapper();
						flightsWrapper.setList(path);
						flightsWrapper.setDistance(distance);
						paths.add(flightsWrapper);

						log.warn(
								"A flight with a layover was found, flight number {} and flight number {} with distance {}",
								flight1.getId(), flight2.getId(), distance);

						/*
						* if the destination of flight 2 = the origin of a
						* third flight to the desired destination
						*/
					} else {
						for (Flight flight3 : flightsToDestination) {
							Integer flight3OriginId = flight3
									.getLocationByOrigin().getId();
							if (flight2DestId == flight3OriginId
									&& flight2.getEta()
											+ flight2.getDeparture() > flight3
												.getDeparture()) {
								List<Flight> path = new ArrayList<>();
								path.add(flight1);
								path.add(flight2);
								path.add(flight3);
								distance = flight1.getEta()
										+ flight2.getEta()
										+ flight3.getEta();

								FlightsWrapper flightsWrapper = new FlightsWrapper();
								flightsWrapper.setList(path);
								flightsWrapper.setDistance(distance);
								paths.add(flightsWrapper);

								log.warn(
										"A flight with a layover was found, flight number {} and flight number {} and flight number {} with distance {} ",
										flight1.getId(), flight2.getId(),
										flight3.getId(), distance);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * reroutes a travel plan (a list of flights). Gets a list of flights whos
	 * origin is the location chosen by scheduleBean.reroute() in the front end.
	 * It is the origin of the last flight with delays in the list of flights in
	 * the fair. The home location is the origin of the first flight in the list
	 * of flights of the ticket. If a flight path cannot be found to the
	 * destination, reroute the passenger home.
	 * 
	 */
	@Override
	public List<FlightsWrapper> reroute(Location origin, Location destination,
			Location home) {
		log.info("entering FlightDaoImpl.reroute( {} {},  {} {}, {} {})",
				origin.getCity(), origin.getState(), destination.getCity(),
				destination.getState(), home.getCity(), home.getState());
		Session session = factory.getCurrentSession();
		List<Flight> flightsFromOrigin = session
				.createQuery(
						"SELECT f FROM Flight f WHERE f.locationByOrigin IN (SELECT l FROM Location l WHERE l.city LIKE :city AND l.state LIKE :state) AND seats < 6 AND departure > -1 AND arrived = 0")
				.setString("city", origin.getCity())
				.setString("state", origin.getState()).list();
		List<Flight> flights = session
				.createQuery(
						"FROM Flight f WHERE seats < 6 AND arrived = 0  AND departure > 0")
				.list();
		List<Flight> flightsToDestination = session
				.createQuery(
						"SELECT f FROM Flight f WHERE f.locationByDestination IN (SELECT l FROM Location l WHERE l.city LIKE :city AND l.state LIKE :state) AND seats < 6 AND departure > -1 AND arrived = 0")
				.setString("city", destination.getCity())
				.setString("state", destination.getState()).list();

		List<FlightsWrapper> paths = mapPaths(origin, destination, flights,
				flightsFromOrigin, flightsToDestination);

		if (paths.size() == 0) {
			log.warn("No paths found from {} {} rerouting home {} {}",
					origin.getCity(), origin.getState(), home.getCity(),
					home.getState());
			paths = mapPaths(home, origin, flights, flightsFromOrigin,
					flightsToDestination);
			log.info("leaving FlightDaoImpl.reroute()");
			return paths;
		} else {
			log.warn("Paths found from {} {} to scheduled destination {} {}",
					origin.getCity(), origin.getState(), destination.getCity(),
					destination.getState());
			log.info("leaving FlightDaoImpl.reroute()");
			return paths;
		}

	}

	/**
	 * retrieve a list of all ids in the flight table of the database used to
	 * determine of a flight already exists in the database when converting the
	 * flight model to a flight table.
	 */
	@Override
	public List<Integer> getIds() {
		log.info("entring and leaving FlightDaoImpl.getIds()");
		Session session = factory.getCurrentSession();
		return session.createQuery("SELECT f.id FROM Flight f ORDER BY id ASC")
				.list();
	}

	/**
	 * get a flight from the database given a specific id used to handle
	 * converting the flight model to a flight table used to update the flight
	 * table with updates from JMS
	 */
	@Override
	public Flight getFlight(Integer id) {
		log.info("entering FlightDaoImpl.getFlight({})", id);
		Session session = factory.getCurrentSession();
		Flight flight = (Flight) session
				.createQuery("FROM Flight f WHERE id = :id")
				.setInteger("id", id).uniqueResult();
		log.info("leaving FlightDaoImpl.getFlight({})", id);
		return flight;
	}
}
