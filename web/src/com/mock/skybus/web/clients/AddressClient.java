package com.mock.skybus.web.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.mock.skybus.web.models.orm.Address;
import com.mock.skybus.web.models.orm.User;

public class AddressClient {

	Logger log = LoggerFactory.getLogger(AddressClient.class);



	public Address getAddress(String username) {
		log.info("entering AddressClient.getAddress(username)");
		final String uri = "http://localhost:8080/flight-planner-backend/address/"
				+ username;

		RestTemplate restTemplate = new RestTemplate();

		Address address = restTemplate.getForObject(uri, Address.class);
		log.info("leaving AddressClient.getAddress(username)");
		return address;
	}

	public void saveAddress(Address address) {
		log.info("entering AddressClient.saveAddress(address)");
		final String uri = "http://localhost:8080/flight-planner-backend/address/POST";

		RestTemplate restTemplate = new RestTemplate();
		Address response = restTemplate.postForObject(uri, address,
				Address.class);

		log.info("leaving AddressClient.saveAddress(address)");
	}

	public void saveUser(User user) {
		System.out.println("entering UserClient.saveUser(user)");
		final String uri = "http://localhost:8080/flight-planner-backend/user/POST";

		RestTemplate restTemplate = new RestTemplate();
		User response = restTemplate.postForObject(uri, user,
				User.class);

		System.out.println("leaving UserClient.saveUser(user)");
		if (response != null) {
			System.out.println("http://localhost:8080/flight-planner-backend/user/POST \n"
					+ "posted success");
		} else {
			System.out.println("http://localhost:8080/flight-planner-backend/user/POST \n"
					+ "post failure");
		}
	}
	
	
}
