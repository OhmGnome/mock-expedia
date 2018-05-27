package com.mock.skybus.web.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mock.skybus.web.clients.AddressClient;
import com.mock.skybus.web.clients.PersonClient;
import com.mock.skybus.web.clients.UserClient;
import com.mock.skybus.web.models.orm.Address;
import com.mock.skybus.web.models.orm.Person;
import com.mock.skybus.web.models.orm.User;

/**
 * The profile bean is the underlying functionality of the login page. The user
 * can change their customer information here.
 * 
 * @author ZGT43
 *
 */
@Component
@Scope("session")
public class ProfileBean {

	Logger log = LoggerFactory.getLogger(LoginBean.class);
	
	@Autowired
	AuthBean authBean;

	private PersonClient personClient = new PersonClient();
	private AddressClient addressClient = new AddressClient();
	private UserClient userClient = new UserClient();

	private Person person;
	private Address address;

	/**
	 * Direct the user to the profile page and load the variables with the users.
	 * @return
	 */
	public String profile(){
		log.info("entering profileBean.profile()");
		String username = new String();
		if ((username = authBean.getUsername()) != null) {
			person = personClient.getPerson(username);
			address = addressClient.getAddress(username);
		}
		log.info("leaving profileBean.profile()");
		return "profile";
	}

	/**
	 * Sends the information entered by the user to the back end to be saved to the database.
	 * @return
	 */
	public String save() {
		log.info("entering ProfileBean.save()");
		User user = userClient.getUser(authBean.getUsername());
		userClient.saveUser(user);

		person.setUser(user);
		personClient.savePerson(person);

		address.setUser(user);
		addressClient.saveAddress(address);

		log.info("leaving ProfileBean.save()");
		return "profile";
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}