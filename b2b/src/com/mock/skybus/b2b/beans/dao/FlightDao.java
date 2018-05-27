package com.mock.skybus.b2b.beans.dao;

import java.util.List;

import com.mock.skybus.b2b.models.mvc.FlightsWrapper;
import com.mock.skybus.b2b.models.mvc.PathsWrapper;
import com.mock.skybus.b2b.models.orm.Flight;
import com.mock.skybus.b2b.models.orm.Location;

/**
 * The Flight data access object interface determines what methods the DAO
 * should contain and what they return in order to provide a platform for
 * extensibility across developer groups. It can save a flight, update a flight, get all the
 * flight id's, and get a flight with an id. These methods are for the back-end. The methods that
 * get all the flights, get all the flights booked by the user, get a list of
 * flights from one location to another as a path, and reroute a path are for the front-end.
 * @author ZGT43
 *
 */
public interface FlightDao {
	public void saveFlight(Flight flight);

	public void updateFlight(Flight flight);

	public List<Flight> getFlights();

	public PathsWrapper getFlights(String username);

	public List<FlightsWrapper> getFlights(Location origin, Location destination);

	public List<FlightsWrapper> reroute(Location origin, Location destination,
			Location home);

	public List<Integer> getIds();

	public Flight getFlight(Integer id);
}
