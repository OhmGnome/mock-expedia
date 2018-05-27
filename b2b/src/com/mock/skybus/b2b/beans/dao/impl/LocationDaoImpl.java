package com.mock.skybus.b2b.beans.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mock.skybus.b2b.beans.dao.LocationDao;
import com.mock.skybus.b2b.models.orm.Location;
import com.mock.skybus.b2b.mvc.controller.FlightController;

/**
 * Implements the methods determined by the LocationDao interface. Accesses the
 * database for the address model. Spring annotation helps spring manifest the
 * the same session across data access objects unitarily.
 * 
 * @author ZGT43
 *
 */
@Repository
@Transactional
public class LocationDaoImpl implements LocationDao {

	@Autowired
	SessionFactory factory;

	private Logger log = LoggerFactory.getLogger(FlightController.class);

	/**
	 * Save a location to the database.
	 */
	@Override
	public void saveLocation(Location location) {
		log.info("entering LocationDaoImpl.saveLocation({} {})",
				location.getCity(), location.getState());
		Session session = factory.getCurrentSession();
		session.save(location);
	}

	/**
	 * Get a location from the database using a given id.
	 */
	@Override
	public Location getLocation(Integer id) {
		log.info("entering leaving LocationDaoImpl.saveLocation(Integer {})",
				id);
		Session session = factory.getCurrentSession();
		return (Location) session
				.createQuery("FROM Location l WHERE l.id = :id")
				.setInteger("id", id).uniqueResult();
	}

	/**
	 * Get locations from the database and order them by state and city for
	 * display on the travel page so a user can select an origin and a
	 * destination for their trip
	 */
	@Override
	public List<Location> getLocations() {
		log.info("entering leaving LocationDaoImpl.getLocations()");
		Session session = factory.getCurrentSession();
		return session.createQuery("FROM Location ORDER BY state, city ASC")
				.list();
	}

	/**
	 * Get all the location id in the database to check if a location already
	 * exists in the database and to set FlightModelClient.locationId to the
	 * latest id, in order to preserve the uniqueness of the location ids in the
	 * database
	 */
	@Override
	public Integer getLatestId() {
		log.info("entering leaving LocationDaoImpl.getIds()");
		Session session = factory.getCurrentSession();
		return (Integer) session.createQuery(
				"SELECT l.id FROM Location l ORDER BY l.id DESC").setMaxResults(1).uniqueResult();
	}

}