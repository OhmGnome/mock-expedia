package com.mock.skybus.b2b.beans.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mock.skybus.b2b.models.mvc.FlightsWrapper;
import com.mock.skybus.b2b.models.mvc.PathsWrapper;
import com.mock.skybus.b2b.models.orm.Book;
import com.mock.skybus.b2b.models.orm.Flight;
import com.mock.skybus.b2b.models.orm.Location;
import com.mock.skybus.b2b.mvc.controller.FlightController;

/**
 * Implements the methods determined by the AddressDao interface. Accesses the
 * database for the address model. Spring annotation helps spring manifest the
 * the same session across data access objects unitarily.
 * 
 * 
 *
 */

public class main1{

    
	private Logger log = LoggerFactory.getLogger(FlightController.class);

	@Autowired
    SessionFactory factory;
    
    
    public static void main(String[] args){

    }

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
                Worker worker = new Worker();
                worker.destination = destination;
                worker.flight1 = flight1;
                worker.flightsToDestination = flightsToDestination;
                worker.paths = paths;

                int size = (int) Math.floor(flights.size()/4);
                for (int i = 0; i < 4; i++){
                    int start = size * i;
                    int end;
                    if (i != 3) end = size * i + 1;
                    else end = flights.size() - 1;

                    worker.flights = flights.subList(start, end);
                    
                    try {
                        Worker clone = (Worker) worker.clone();
                        clone.paths = paths;
                        Thread t = new Thread(clone);
                        t.start();
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
                }
			}
		}
		log.info("leaving FlightDaoImpl.mapPaths() with {} paths", paths.size());
		return paths;
    }
    

    private class Worker implements Runnable, Cloneable{
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
}
