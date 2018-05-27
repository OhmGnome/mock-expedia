package com.mock.skybus.b2b.beans.dao;

import java.util.List;

import com.mock.skybus.b2b.models.orm.Location;

/**
 * The Location data access object interface determines what methods the DAO
 * should contain and what they return in order to provide a platform for
 * extensibility across developer groups. The methods that save a location, get
 * a location, and get the ids of all the locations are for the backed. the
 * method that gets all the locations is for the front end.
 * 
 * @author ZGT43
 *
 */
public interface LocationDao {
	public void saveLocation(Location location);

	public Location getLocation(Integer Id);

	public List<Location> getLocations();

	public Integer getLatestId();
}
