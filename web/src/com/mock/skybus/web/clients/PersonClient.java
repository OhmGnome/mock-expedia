package com.mock.skybus.web.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.mock.skybus.web.models.orm.Person;
import com.mock.skybus.web.models.orm.User;

public class PersonClient {

	Logger log = LoggerFactory.getLogger(PersonClient.class);
	
	public Person getPerson(String username) {
		log.info("entering PersonClient.getPerson(username)");
		
		final String uri = "http://localhost:8080/flight-planner-backend/person/"
				+ username;
		RestTemplate restTemplate = new RestTemplate();
		Person person = restTemplate.getForObject(uri, Person.class);
		
		log.info("leaving PersonClient.getPerson(username)");
		return person;
	}

	public void savePerson(Person person) {
		log.info("entering PersonClient.savePerson(person)");
		
		final String uri = "http://localhost:8080/flight-planner-backend/person/POST";
		RestTemplate restTemplate = new RestTemplate();
		Person response = restTemplate.postForObject(uri, person,
				Person.class);

		log.info("leaving PersonClient.savePerson(person)");
	}
}