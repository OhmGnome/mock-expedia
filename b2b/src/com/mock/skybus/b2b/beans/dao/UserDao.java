package com.mock.skybus.b2b.beans.dao;

import com.mock.skybus.b2b.models.orm.User;

/**
 * The Person data access object interface determines what methods the DAO
 * should contain and what they return in order to provide a platform for
 * extensibility across developer groups. It can save a user name and password
 * provided by the front end user. It can get a user from a username
 * 
 * @author ZGT43
 *
 */
public interface UserDao {
	public void saveUser(User user);

	public User getUserByUsername(String username);
}
