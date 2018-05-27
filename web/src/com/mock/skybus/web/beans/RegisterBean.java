package com.mock.skybus.web.beans;

import javax.annotation.PostConstruct;

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
 * The register bean is the underlying functionality of the register page. The
 * user can create a new account here, and set their information here if they
 * want to.
 * 
 * @author ZGT43
 *
 */
@Component
@Scope("session")
public class RegisterBean {

	Logger log = LoggerFactory.getLogger(LoginBean.class);
	
	@Autowired
	LoginBean loginBean;

	private PersonClient personClient = new PersonClient();
	private AddressClient addressClient = new AddressClient();
	private UserClient userClient = new UserClient();

	private Encrypt encrypt = new Encrypt();
	private Person person;
	private Address address;
	private User user;

	/**
	 * Navigate the user to the register page. Clear any prior data by
	 * instantiating a new object that represents that data.
	 * 
	 * @return
	 */
	public String registrar() {
		log.info("entering RegisterBean.registrar()");
		user = new User();
		person = new Person();
		address = new Address();
		log.info("leaving RegisterBean.registrar()");
		return "registrar";
	}

	/**
	 *Sends the information entered by the user to the back end to be saved to the database. Sets the user object back to null after they have registered for security.
	 * @return
	 */
	public String register() {
		if (user != null) {
			log.info("entering RegisterBean.register()");
			user.setPassword(encrypt.encryptor(user.getPassword()));
			userClient.saveUser(user);

			User dbUser = userClient.getUser(user.getName());
			loginBean.setUser(dbUser);
			String loginNav = loginBean.login();

			person.setUser(dbUser);
			personClient.savePerson(person);

			address.setUser(dbUser);
			addressClient.saveAddress(address);

			log.warn("Registering a new user: {}", user.getName());
			user = new User();
			log.info("leaving RegisterBean.register()");
			return loginNav;
		} else {
			log.warn("Registering a new user: null failed");
			return "registrar";
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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