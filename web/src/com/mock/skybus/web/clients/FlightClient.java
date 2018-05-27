package com.mock.skybus.web.clients;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.mock.skybus.web.models.mvc.FlightsWrapper;
import com.mock.skybus.web.models.mvc.PathsWrapper;
import com.mock.skybus.web.models.orm.Flight;
import com.mock.skybus.web.models.orm.Location;

public class FlightClient {

	Logger log = LoggerFactory.getLogger(FlightClient.class);

	public static void main(String[] args) {
		FlightClient c = new FlightClient();
//		Location o = new Location();
//		o.setCity("Atlanta");
//		o.setState("Georgia");
//		o.setId(8);
//
//		Location d = new Location();
//		d.setCity("Memphis");
//		d.setState("Tennessee");
//		d.setId(21);
//
//		Map<Integer, FlightsWrapper> paths = c.getFlightsByPaths(o, d);
//		Set keys = paths.keySet();
//		for (Object key : keys) {
//			System.out.println(key.toString());
//			FlightsWrapper flightsWrapper = paths.get(key);
//			List<Flight> flights = flightsWrapper.getList();
//			for (Flight flight : flights) {
//				System.out.println(flight.getId());
//			}
//		}
		
		
		List<Flight> flights = c.getFlights();
		for(Flight f : flights){
			System.out.println(f);
		}
		System.out.println("test completed");
	}

	public List<Flight> getFlights() {
		log.info("entering FlightClient.getFlights()");

		final String uri = "http://localhost:8080/flight-planner-backend/flights";
		RestTemplate restTemplate = new RestTemplate();
		FlightsWrapper flightsWrapper = restTemplate.getForObject(uri,
				FlightsWrapper.class);
		List<Flight> flights = flightsWrapper.getList();

		log.info("leaving FlightClient.getFlights()");
		return flights;
	}

//	public List<Flight> getFlightsByUser(String username) {
//		log.info("entering FlightClient.getFlightsByUser(String username)");
//
//		final String uri = "http://localhost:8080/flight-planner-backend/flights/User/"
//				+ username;
//		RestTemplate restTemplate = new RestTemplate();
//		FlightsWrapper flightsWrapper = restTemplate.getForObject(uri,
//				FlightsWrapper.class);
//		List<Flight> flights = flightsWrapper.getList();
//		
//		log.info("leaving FlightClient.getFlightsByUser(username)");
//		return flights;
//	}
	
	public PathsWrapper getFlights(String username) {
		log.info("entering FlightClient.getFlightsByUser(String username)");

		final String uri = "http://localhost:8080/flight-planner-backend/paths/user/"
				+ username;
		RestTemplate restTemplate = new RestTemplate();
		PathsWrapper pathsWrapper = restTemplate.getForObject(uri,
				PathsWrapper.class);
		
		log.info("leaving FlightClient.getFlightsByUser(username)");
		return pathsWrapper;
	}

	public List<FlightsWrapper> getFlights(Location origin,
			Location destination) {
		log.info("entering FlightClient.getFlightsByPaths(Location origin, Location destination)");

		Integer originId = origin.getId();
		Integer destinationId = destination.getId();

		final String uri = "http://localhost:8080/flight-planner-backend/paths/"
				+ originId + "/" + destinationId;
		RestTemplate restTemplate = new RestTemplate();
		PathsWrapper pathsWrapper = restTemplate.getForObject(uri,
				PathsWrapper.class);

		List<FlightsWrapper> paths = pathsWrapper.getPaths();
		
		log.info("leaving FlightClient.getFlightsByPaths(Location origin, Location destination)");
		return paths;
	}
	
	public List<FlightsWrapper> reroute(Location origin,
			Location destination, Location home) {
		log.info("entering FlightClient.reroute(Location origin, Location destination, Location home)");

		Integer originId = origin.getId();
		Integer destinationId = destination.getId();
		Integer homeId = home.getId();

		final String uri = "http://localhost:8080/flight-planner-backend/reroute/"
				+ originId + "/" + destinationId + "/" +homeId;
		RestTemplate restTemplate = new RestTemplate();
		PathsWrapper pathsWrapper = restTemplate.getForObject(uri,
				PathsWrapper.class);

		List<FlightsWrapper> paths = pathsWrapper.getPaths();
		
		log.info("leaving FlightClient.reroute(Location origin, Location destination, Location home)");
		return paths;
	}

}