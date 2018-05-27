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

import com.mock.skybus.b2b.beans.dao.AddressDao;
import com.mock.skybus.b2b.beans.dao.UserDao;
import com.mock.skybus.b2b.models.orm.Address;

/**
 * AddressController is a spring implementation of REST HTTP. It can get or send
 * an object across RESTful web services.
 * 
 * @author ZGT43
 *
 */
@Controller
public class AddressController {

	private Logger log = LoggerFactory.getLogger(AddressController.class);

	@Autowired
	private AddressDao addressDao;
	@Autowired
	private UserDao userDao;

	/**
	 * calls addressDao.getAddress(user name) to get an address from the
	 * database given a user name. The PathVariable determines the value of the
	 * user name. PathVariable itself is also a variable that is determined by
	 * another machine and transported to this machine via HTTP.
	 * 
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/address/{username}", method = RequestMethod.GET)
	public @ResponseBody Address getAddress(@PathVariable String username) {
		log.info("/address/{username} entered");
		log.warn("/address/{}", username);
		Address address = addressDao.getAddress(userDao
				.getUserByUsername(username));
		log.warn("sending address number {}", address.getId());
		log.info("/address/{username} processed");
		return address;
	}

	/**
	 * Saves an address transported to this machine via HTTP. The object is
	 * transported as XML and unmarshaled back into an object when it arrives
	 * 
	 * @param address
	 */
	@RequestMapping(value = "/address/POST", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody void saveAddress(@RequestBody Address address) {
		log.info("/address/POST entered : {}");
		try {
			addressDao.saveAddress(address);
			log.info("/address/POST processed");
		} catch (Exception e) {
			log.error("/address/POST failure");
			e.printStackTrace();
		}
	}

}
