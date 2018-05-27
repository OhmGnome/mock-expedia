package com.mock.skybus.b2b.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mock.skybus.b2b.beans.dao.PersonDao;
import com.mock.skybus.b2b.beans.dao.UserDao;
import com.mock.skybus.b2b.models.orm.Person;

/**
 * PersonController is a spring implementation of REST HTTP. It can get or send
 * an object across RESTful web services.
 * 
 * @author ZGT43
 *
 */
@Controller
public class PersonController {

	private Logger log = LoggerFactory.getLogger(PersonController.class);

	@Autowired
	private PersonDao personDao;
	@Autowired
	private UserDao userDao;

	/**
	 * calls personDao.getPerson(user) via userDao.getUserByUsername(user name)
	 * to get a person from the database given a user name. The PathVariable
	 * determines the value of the user name. PathVariable itself is also a
	 * variable that is determined by another machine and transported to this
	 * machine via HTTP.
	 * 
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/person/{username}", method = RequestMethod.GET)
	public @ResponseBody Person getPerson(@PathVariable String username) {
		log.info("/person/{username} entered");
		log.warn("/person/{}", username);

		Person person = personDao
				.getPerson(userDao.getUserByUsername(username));

		log.warn("sending person number {}", person.getId());
		log.info("/person/{username} processed");
		return person;
	}

	/**
	 * Saves an person transported to this machine via HTTP. The object is
	 * transported as XML and unmarshaled back into an object when it arrives
	 * 
	 * @param person
	 */
	@RequestMapping(value = "/person/POST", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody void savePerson(@RequestBody Person person) {
		log.info("/person/POST entered");
		log.warn("personId: {}", person);

		try {
			personDao.savePerson(person);
			log.info("/person/POST processed");

		} catch (Exception e) {
			log.error("/person/POST failed");
			e.printStackTrace();
		}
	}
}
