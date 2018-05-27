package com.mock.skybus.web.beans;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mock.skybus.web.clients.FlightClient;
import com.mock.skybus.web.models.orm.Flight;

/*
 * bean for the home page. classes that should be reconstructed everytime the page loads should be @request scope, 
 * any variables that need to be passed from bean to bean should be @session scope
 */
@Component
@Scope("session")
public class HomeBean {

	Logger log = LoggerFactory.getLogger(HomeBean.class);

	private FlightClient flightClient = new FlightClient();
	private List<Flight> flights;

	/**
	 * HomeBean.init() is called when the Bean is created. It populates the page
	 * with a list of all the flights in the database.
	 */
	@PostConstruct
	public void init() {
		log.info("entering flightClient.init()");
		flights = flightClient.getFlights();
		log.info("leaving flightClient.init()");
	}

	/**
	 * Refresh the list of flights displayed on the home page every time the
	 * user navigates to the home page.
	 * 
	 * @return
	 */
	public String home() {
		log.info("entering flightClient.getFlights()");
		flights = flightClient.getFlights();
		log.info("leaving flightClient.getFlights()");
		return "home";
	}

	public List<Flight> getFlights() {
		return flights;
	}

	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}
}