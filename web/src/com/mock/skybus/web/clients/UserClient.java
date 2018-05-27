package com.mock.skybus.web.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.mock.skybus.web.models.orm.User;
import com.mock.skybus.web.models.orm.User;

public class UserClient {

	Logger log = LoggerFactory.getLogger(UserClient.class);
	
	public User getUser(String username) {
		log.info("entering UserClient.getUser(username)");
		
		final String uri = "http://localhost:8080/flight-planner-backend/user/"
				+ username;
		RestTemplate restTemplate = new RestTemplate();
		User user = restTemplate.getForObject(uri, User.class);
		
		log.info("leaving UserClient.getUser(username)");
		return user;
	}

	public void saveUser(User user) {
		log.info("entering UserClient.saveUser(user)");
		
		final String uri = "http://localhost:8080/flight-planner-backend/user/POST";
		RestTemplate restTemplate = new RestTemplate();
		User response = restTemplate.postForObject(uri, user,
				User.class);

		log.info("leaving UserClient.saveUser(user)");
	}
}