package com.mock.skybus.web.clients;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.mock.skybus.web.models.mvc.LocationsWrapper;
import com.mock.skybus.web.models.orm.Location;

public class LocationClient {

	Logger log = LoggerFactory.getLogger(LocationClient.class);
	
	public List<Location> getLocations() {
		log.info("entering LocationClient.getLocations()");
		
		final String uri = "http://localhost:8080/flight-planner-backend/locations/";
		RestTemplate restTemplate = new RestTemplate();
		LocationsWrapper locationsWrapper = restTemplate.getForObject(uri, LocationsWrapper.class);
		List<Location> locations = locationsWrapper.getList();
		
		log.info("leaving LocationClient.getLocations()");
		return locations;
	}
}