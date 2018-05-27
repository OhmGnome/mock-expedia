package com.mock.skybus.b2b.beans.clients;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mock.skybus.b2b.models.b2b.Flight;
import com.mock.skybus.b2b.models.b2b.FlightModel;
import com.mock.skybus.b2b.beans.dao.FlightDao;
import com.mock.skybus.b2b.beans.dao.LocationDao;
import com.mock.skybus.b2b.models.orm.Location;

/**
 * FlightModelClient uses REST to retrieve the FlightModel object from the
 * server. FlightModelClient stores unique flights and locations in the database
 * in a separate table. if a flight already exists in the database, the eta and
 * departure information are updated. FlightModelClient sends an HTTP request
 * after the server loads, and again at every new day using a timer. As a
 * sidenote, the easy way would be to just set the flightId from the flight
 * model when I book flights, instead I made a Flight table because it was
 * challenging and I wanted to my project to be unique.
 *
 * @author ZGT43
 *
 */
@Component
public class FlightModelClient {

	private Logger log = LoggerFactory.getLogger(FlightModelClient.class);

	@Autowired
	FlightDao flightDao;
	@Autowired
	LocationDao locationDao;

	long tommorrow;
	Timer timer;

	/**
	 * Start a timer with a quasi arbitrary count-down to call a method to make
	 * an HTTP request to get the flight model after the server starts
	 */
	@PostConstruct
	public void init() {
		log.info("entering FlightModelClient.init(), starting timer");
		timer = new Timer();
		long boot = 10000;
		log.warn("timer set for {} milliseconds", boot);
		timer.schedule(new Timeslip(), boot);
		log.info("leaving FlightModelClient.init()");
	}

	/**
	 * The timer object that calls a method to make an HTTP request to get the
	 * flight model when the count-down is reached. The count-down is determined
	 * by secondsTillNextDay from the flight model object.
	 *
	 * @author ZGT43
	 *
	 */
	private class Timeslip extends TimerTask {
		public void run() {
			log.info("entering timeslip");
			getFlightModel();
			log.info("leaving timeslip");
		}
	}

	/**
	 * retrieve the FlightModel object from the server. Store unique flights and
	 * locations in the database in a separate table. if a flight already exists
	 * in the database, the eta and departure information are updated. Set the
	 * timer to retrieve another FlightModel later. A flight is a Flight object
	 * from the FlightModel, whereas a hop is a Flight object for the database
	 */
	public void getFlightModel() {
		log.info("entering FlightModelClient.getFlightModel()");
		final String uri = "http://localhost:8080/FinalInstructorWebService/getFlightModel";

		RestTemplate restTemplate = new RestTemplate();

		FlightModel flightModel = restTemplate.getForObject(uri,
				FlightModel.class);

		/*
		 * Set the timer to retrieve another flightModel later.
		 */
		log.warn(
				"FlightModelClient expecting next FlightModel in : {} milliseconds",
				flightModel.getSecondsTillNextDay());
		tommorrow = flightModel.getSecondsTillNextDay();
		timer.schedule(new Timeslip(), tommorrow);

		List<Flight> flights = flightModel.getFlights();

		List<Integer> hopIds = flightDao.getIds();

		Integer locationId = 0;
		Location location = new Location();
		List<Location> hasCity = locationDao.getLocations();

		/*
		 * If the database contains a location set the locationId to the last id
		 * in the table
		 */
		if (hasCity.size() > 0) {
			locationId = locationDao.getLatestId();
		}
		List<Location> hasState = new ArrayList<>();

		/*
		 * persist the flight and location data to the database
		 */
		for (Flight flight : flights) {
			com.mock.skybus.b2b.models.orm.Flight hop = new com.mock.skybus.b2b.models.orm.Flight();
			int id = flight.getFlightId();

			/*
			 * if the flight already exists update it if the travel information
			 * has changed, otherwise it would have a different flight id.
			 */
			if (hopIds.contains(id)) {
				hop = flightDao.getFlight(id);
				if (hop.getEta() != flight.getEta()
						|| hop.getDeparture() != flight.getDeparture()) {

					hop.setEta(flight.getEta());
					hop.setDeparture(flight.getDeparture());
					flightDao.updateFlight(hop);
				}

				/*
				 * persist locations to the database in a way that maintains
				 * their uniqueness.
				 */
			} else {
				location.setCity(flight.getOrigin().getCity());
				location.setState(flight.getOrigin().getState());

				/*
				 * If the database contains any location
				 */
				if (hasCity.size() > 0) {

					/*
					 * if this city exists in the database add it to hasState?
					 */
					for (Location l : hasCity) {
						if (location.getCity().equals(l.getCity())) {
							hasState.add(l);
						}
					}

					/*
					 * if this city exists in the database and this state exists
					 * in the database set the origin of the hop to that
					 * location
					 */
					if (hasState.size() > 0) {
						for (Location l : hasState) {
							if (location.getState().equals(l.getState())) {
								hop.setLocationByOrigin(l);
							}
						}
						hasState.clear();
					}

					/*
					 * if a location was not found in the database the origin of
					 * the hop will still be null. In that case give the
					 * location an id, save it to the database, and set it to
					 * the hop origin, and reload this list of locations
					 * (hasCity) with current database information.
					 */
					if (hop.getLocationByOrigin() == null) {
						locationId++;
						location.setId(locationId);
						locationDao.saveLocation(location);
						hop.setLocationByOrigin(location);

						hasCity = locationDao.getLocations();
					}

					/*
					 * do the same for the destination
					 */
					location.setCity(flight.getDestination().getCity());
					location.setState(flight.getDestination().getState());

					for (Location l : hasCity) {
						if (location.getCity().equals(l.getCity())) {
							hasState.add(l);
						}
					}
					if (hasState.size() > 0) {
						for (Location l : hasState) {
							if (location.getState().equals(l.getState())) {
								hop.setLocationByDestination(l);
							}
						}
						hasState.clear();
					}
					if (hop.getLocationByDestination() == null) {
						locationId++;
						location.setId(locationId);
						locationDao.saveLocation(location);
						hop.setLocationByDestination(location);

						hasCity = locationDao.getLocations();
					}

					/*
					 * If the database does not contain any location at all
					 */
				} else {
					locationId++;
					location.setId(locationId);
					locationDao.saveLocation(location);
					hop.setLocationByOrigin(location);

					location.setCity(flight.getDestination().getCity());
					location.setState(flight.getDestination().getState());
					locationId++;
					location.setId(locationId);
					locationDao.saveLocation(location);
					hop.setLocationByDestination(location);

					hasCity = locationDao.getLocations();
				}

				/*
				 * after the location is finally set, set the rest of the fields
				 * for the hop and save it to the database
				 */
				hop.setId(flight.getFlightId());
				hop.setEta(flight.getEta());
				hop.setDeparture(flight.getDeparture());
				flightDao.saveFlight(hop);
			}
		}
		/*
		 * An unnecessary header used to print out the retrieved FlightModel
		 */
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
		HttpEntity<String> entity = new HttpEntity<String>("parameters",
				headers);

		ResponseEntity<String> result = restTemplate.exchange(uri,
				HttpMethod.GET, entity, String.class);
		log.warn("FlightModel recieved {}", result);
		log.info("leaving FlightModelClient.getFlightModel()");
	}
}
