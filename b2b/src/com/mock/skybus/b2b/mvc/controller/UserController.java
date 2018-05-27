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

import com.mock.skybus.b2b.beans.dao.UserDao;
import com.mock.skybus.b2b.models.orm.User;

/**
 * UserController is a spring implementation of REST HTTP. It can get or send
 * an object across RESTful web services.
 * 
 * @author ZGT43
 *
 */
@Controller
public class UserController {

	private Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserDao userDao;
	
	/**
	 * calls userDao.getPerson(user) via userDao.getUserByUsername(user name)
	 * to get a user from the database given a user name. The PathVariable
	 * determines the value of the user name. PathVariable itself is also a
	 * variable that is determined by another machine and transported to this
	 * machine via HTTP.
	 * 
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
	public @ResponseBody User getUserByUsername(@PathVariable String username) {
		log.info("/user/{username} entered");
		log.warn("/user/{}", username);
		
		User user = userDao.getUserByUsername(username);
		
		log.warn("sending user number {}", user.getId());
		log.info("/user/{username} processed");
		return user;
	}

	@RequestMapping(value = "/user/POST", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody void saveUser(@RequestBody User user) {
		log.info("/user/POST entered");
		log.warn("userId: {}", user);
		
		try {
			userDao.saveUser(user);
			log.info("/user/POST processed");
			
		} catch (Exception e) {
			log.error("/user/POST failed");
			e.printStackTrace();
		}
	}
}
