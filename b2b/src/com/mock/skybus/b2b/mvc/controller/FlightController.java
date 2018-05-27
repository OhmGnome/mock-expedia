package com.mock.skybus.b2b.mvc.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mock.skybus.b2b.beans.clients.FlightModelClient;
import com.mock.skybus.b2b.beans.dao.FlightDao;
import com.mock.skybus.b2b.beans.dao.LocationDao;
import com.mock.skybus.b2b.beans.dao.UserDao;
import com.mock.skybus.b2b.models.mvc.FlightsWrapper;
import com.mock.skybus.b2b.models.mvc.PathsWrapper;
import com.mock.skybus.b2b.models.orm.Flight;
import com.mock.skybus.b2b.models.orm.Location;

/**
 * FlightController is a spring implementation of REST HTTP. It can get or send
 * an object across RESTful web services.
 * 
 * @author ZGT43
 *
 */
@Controller
public class FlightController {

	private Logger log = LoggerFactory.getLogger(FlightController.class);

	@Autowired
	private FlightModelClient flightModelClient;
	@Autowired
	private FlightDao flightDao;
	@Autowired
	private LocationDao locationDao;
	@Autowired
	private UserDao userDao;

	/**
	 * Gets a list of flights from the database via flightDao.getFlights(). The
	 * list is assigned to a FlightsWrapper object and sent across HTTP.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/flights", method = RequestMethod.GET)
	public @ResponseBody FlightsWrapper getFlights() {
		log.info("/flights entered");

		List<Flight> flights = flightDao.getFlights();
		FlightsWrapper flightsWrapper = new FlightsWrapper();
		flightsWrapper.setList(flights);

		log.info("/flights processed");
		return flightsWrapper;
	}

	/**
	 * Gets a list of a list of flights from the database via
	 * flightDao.getFlights(user name). The list is assigned to a PathsWrapper
	 * object and sent across HTTP.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/paths/user/{username}", method = RequestMethod.GET)
	public @ResponseBody PathsWrapper getFlightsByUser(
			@PathVariable String username) {
		log.info("/paths/User/{username} entered");
		log.warn("/paths/User/{}", username);
		PathsWrapper pathsWrapper = flightDao.getFlights(username);
		log.info("/paths/User/{username} processed");
		return pathsWrapper;
	}

	/**
	 * Gets a list of a list of flights from the database via
	 * flightDao.getFlights(origin destination). The list is assigned to a PathsWrapper
	 * object and sent across HTTP.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/paths/{originId}/{destinationId}", method = RequestMethod.GET)
	public @ResponseBody PathsWrapper getFlightsByPaths(
			@PathVariable Integer originId, @PathVariable Integer destinationId) {
		log.info("/paths/{originId}/{destinationId} entered");
		log.warn("/paths/{}/{}", originId, destinationId);
		
		Location origin = locationDao.getLocation(originId);
		Location destination = locationDao.getLocation(destinationId);
		List<FlightsWrapper> paths = flightDao.getFlights(origin, destination);
		PathsWrapper pathsWrapper = new PathsWrapper();
		pathsWrapper.setPaths(paths);

		log.info("/paths/{originId}/{destinationId} processed");
		return pathsWrapper;
	}

	/**
	 * Gets a list of a list of flights from the database via
	 * flightDao.getFlights(origin, destination, home). The list is assigned to a PathsWrapper
	 * object and sent across HTTP.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/reroute/{originId}/{destinationId}/{homeId}", method = RequestMethod.GET)
	public @ResponseBody PathsWrapper reroute(@PathVariable Integer originId,
			@PathVariable Integer destinationId, @PathVariable Integer homeId) {
		log.info("/reroute/{originId}/{destinationId}/{homeId} entered");
		log.warn("/reroute/{}/{}/{} : ", originId, destinationId, homeId);
		
		Location origin = locationDao.getLocation(originId);
		Location destination = locationDao.getLocation(destinationId);
		Location home = locationDao.getLocation(homeId);
		List<FlightsWrapper> paths = flightDao.reroute(origin, destination,
				home);
		PathsWrapper pathsWrapper = new PathsWrapper();
		pathsWrapper.setPaths(paths);

		log.info("/reroute/{originId}/{destinationId}/{homeId} processed");
		return pathsWrapper;
	}
}
