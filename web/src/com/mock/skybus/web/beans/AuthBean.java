package com.mock.skybus.web.beans;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mock.skybus.web.clients.UserClient;
import com.mock.skybus.web.models.orm.User;

/**
 * Stores the current user All methods that need the signed in user should
 * access it through authBean.getName()
 * 
 * @author ZGT43
 *
 */
@Component
@Scope("session")
public class AuthBean {
	
	private Logger log = LoggerFactory.getLogger(AuthBean.class);

	private UserClient userClient = new UserClient();
	private Encrypt encrypt = new Encrypt();

	private User user;

	/**
	 * Logs the user in by assigning the parameter user to this user if user and
	 * password is not null or empty and the password matches the password of
	 * the user in the database. Calls userClient.getUser(user name) to get said
	 * password to compare to.
	 * 
	 * @param user
	 * @return
	 */
	public boolean login(User user) {
		log.info("entering AuthBean.login(User) ");

		if (user == null){
			log.error("user is null");
			return false;
		}
		if (user.getName() == null || user.getPassword() == null){
			log.error("user name or password was null");
			return false;
		}
		
		String username = user.getName().trim();
		String password = user.getPassword().trim();

		log.warn("attempting to login user {}", username);
		
		if (username.isEmpty() || password.isEmpty()){
			log.error("user name or password was empty");
			return false;
		}

		User dbUser = userClient.getUser(username);

		if (dbUser == null){
			log.error("the user retrieved from the database was null");
			return false;
		}

		if (encrypt.decryptor(password, dbUser.getPassword())) {
			this.user = dbUser;
			log.info("Login success, password match");
			return true;
		} else {
			log.error("Login Falied passwords do not match");
			return false;
		}
	}

	/**
	 * If a user is logged in, this method returns true, otherwise it returns
	 * false.
	 * 
	 * @return
	 */
	public boolean isLoggedIn() {
		return user != null;
	}

	/**
	 * Invalidates the current session via java server faces upon logout.
	 * returning home.xhtml?faces-redirect=true tells faces.config to tell faces
	 * to forward the user to the home page and forces a redirect.
	 * 
	 * @return
	 */
	public String logout() {
		user = null;
		FacesContext.getCurrentInstance().getExternalContext()
				.invalidateSession();
		return "/home.xhtml?faces-redirect=true";
	}

	/**
	 * Returns the name of this user
	 * 
	 * @return
	 */
	public String getUsername() {
		if (user != null) {
			return user.getName();
		} else {
			return null;
		}
	}
}
