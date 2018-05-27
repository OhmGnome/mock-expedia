package com.mock.skybus.web.beans;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mock.skybus.web.clients.FlightClient;
import com.mock.skybus.web.models.mvc.PathsWrapper;
import com.mock.skybus.web.models.orm.User;

/**
 * The login bean is the underlying functionality of the login page
 * 
 * @author ZGT43
 *
 */
@Component
@Scope("session")
public class LoginBean {
	
	Logger log = LoggerFactory.getLogger(LoginBean.class);

	@Autowired
	AuthBean authBean;
	@Autowired
	TravelBean travelBean;

	FlightClient flightClient = new FlightClient();
	private Encrypt encrypt = new Encrypt();

	User user;
	private Boolean flightDelayed;

	// scrap this
	private String delayMessage = "Attention! Your flight has been delayed!";

	@PostConstruct
	public void init() {
		// user = new User();
		flightDelayed = false;
	}

	/**
	 * Calls the authentication bean to log the user in Gets the fairs for the
	 * user, that has a variable within the packaging that is used to tell if
	 * any flights have been delayed for the user. If a flight has been delayed
	 * the delayMessage will show in the header.
	 * 
	 * @return
	 */
	public String login() {
		log.info("entering loginBean.login()");
		if (authBean.login(user)) {
			PathsWrapper paths = flightClient
					.getFlights(authBean.getUsername());
			if (paths.getDelays()) {
				log.warn("a flight delay was detected");
				flightDelayed = true;
			}
			log.warn("user has been logged in");
			user = null;
			log.info("leaving loginBean.login()");
			return travelBean.travel();
			
		} else {
			log.warn("user was not logged in");
			user = null;
			log.info("leaving loginBean.login()");
			return "login";
		}
	}

	/**
	 * calls authBean.logout() to set the user it holds to null, and return a string to navigate the user to home.
	 * @return
	 */
	public String logout() {
		return authBean.logout();
	}

	/**
	 * Navigates the user to the login page and sets the variable herein to a new user. 
	 * @return
	 */
	public String loginPage() {
		user = new User();
		return "login";
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDelayMessage() {
		return delayMessage;
	}

	public Boolean getFlightDelayed() {
		return flightDelayed;
	}

	public void setFlightDelayed(Boolean flightDelayed) {
		this.flightDelayed = flightDelayed;
	}
}