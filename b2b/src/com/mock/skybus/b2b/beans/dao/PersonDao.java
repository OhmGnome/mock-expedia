package com.mock.skybus.b2b.beans.dao;

import com.mock.skybus.b2b.models.orm.Person;
import com.mock.skybus.b2b.models.orm.User;

/**
 * The Person data access object interface determines what methods the DAO
 * should contain and what they return in order to provide a platform for
 * extensibility across developer groups. It can save or get a first and last
 * name provided by the front end user.
 * 
 * @author ZGT43
 *
 */
public interface PersonDao {
	public void savePerson(Person person);

	public Person getPerson(User user);
}
