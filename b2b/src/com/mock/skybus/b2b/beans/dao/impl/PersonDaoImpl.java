package com.mock.skybus.b2b.beans.dao.impl;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mock.skybus.b2b.beans.clients.FlightModelClient;
import com.mock.skybus.b2b.beans.dao.PersonDao;
import com.mock.skybus.b2b.models.orm.Person;
import com.mock.skybus.b2b.models.orm.User;

/**
 * Implements the methods determined by the PersonDao interface. Accesses the
 * database for the address model. Spring annotation helps spring manifest the
 * the same session across data access objects unitarily.
 * 
 * @author ZGT43
 *
 */
@Repository
@Transactional
public class PersonDaoImpl implements PersonDao {

	private Logger log = LoggerFactory.getLogger(FlightModelClient.class);

	@Autowired
	SessionFactory factory;

	/**
	 * save a person to the database, unless in already exists, in which case
	 * update the existing person with the new information if that information
	 * also exists.
	 */
	@Override
	public void savePerson(Person person) {
		log.info("entering savePerson(Person person)");

		Session session = factory.getCurrentSession();
		if (person.getId() == null) {
			log.warn("saving a new person");
			session.save(person);

		} else {
			log.warn("updating an existing person");
			Person exists = person;
			if (exists.getFirstName() != null)
				exists.setFirstName(person.getFirstName());
			if (exists.getLastName() != null)
				exists.setLastName(person.getLastName());
			session.update(exists);
		}
		log.info("leaving savePerson(Person person)");
	}

	/**
	 * Retrieve a person from the database that is associated with a given user.
	 */
	@Override
	public Person getPerson(User user) {
		log.info("entering and leaving PersonDaoImpl.getPerson({})",
				user.getName());
		Session session = factory.getCurrentSession();
		return (Person) session
				.createQuery(
						"SELECT p FROM Person p, User u WHERE u = :user AND p.user = u")
				.setEntity("user", user).uniqueResult();
	}
}
