package com.mock.skybus.b2b.beans.dao.impl;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mock.skybus.b2b.beans.clients.FlightModelClient;
import com.mock.skybus.b2b.beans.dao.UserDao;
import com.mock.skybus.b2b.models.orm.User;

/**
 * Implements the methods determined by the UserDao interface. Accesses the
 * database for the address model. Spring annotation helps spring manifest the
 * the same session across data access objects unitarily.
 * 
 * @author ZGT43
 *
 */
@Repository
@Transactional
public class userDaoImpl implements UserDao {

	private Logger log = LoggerFactory.getLogger(FlightModelClient.class);

	@Autowired
	SessionFactory factory;

	/**
	 * Save a user to the database, unless in already exists, in which case
	 * update the existing user with the new information in the user object.
	 */
	@Override
	public void saveUser(User user) {
		log.info("entering userDaoImpl.saveUser({})", user.getName());
		Session session = factory.getCurrentSession();
		if (user.getId() == null) {
			session.save(user);
			log.warn("saved a new user to the database");
		} else {
			session.update(user);
			log.warn("updated a user fields within the database with id {}",
					user.getId());
		}

	}

	/**
	 * Retrieve a user from the database with a given user name.
	 */
	@Override
	public User getUserByUsername(String username) {
		log.info("entering and leaving userDaoImpl.getUserByUsername({})",
				username);
		Session session = factory.getCurrentSession();
		return (User) session
				.createQuery("FROM User u WHERE u.name = :username")
				.setString("username", username).uniqueResult();
	}
}
