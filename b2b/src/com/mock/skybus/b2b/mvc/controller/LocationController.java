package com.mock.skybus.b2b.mvc.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mock.skybus.b2b.beans.dao.LocationDao;
import com.mock.skybus.b2b.models.mvc.LocationsWrapper;
import com.mock.skybus.b2b.models.orm.Location;

/**
 * FlightController is a spring implementation of REST HTTP. It can get or send
 * an object across RESTful web services.
 * 
 * @author ZGT43
 *
 */
@Controller
public class LocationController {

	private Logger log = LoggerFactory.getLogger(LocationController.class);

	@Autowired
	private LocationDao locationDao;

	/**
	 * Gets a list of locations from the database via
	 * locationDao.getLocations(). The list is assigned to a LocationsWrapper
	 * object and sent across HTTP.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/locations", method = RequestMethod.GET)
	public @ResponseBody LocationsWrapper getLocation() {
		log.info("/locations/ entered");

		List<Location> locations = locationDao.getLocations();
		LocationsWrapper locationsWrapper = new LocationsWrapper();
		locationsWrapper.setList(locations);

		log.info("/locations/ processed");
		return locationsWrapper;
	}
}
