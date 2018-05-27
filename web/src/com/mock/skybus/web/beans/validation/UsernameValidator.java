package com.mock.skybus.web.beans.validation;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mock.skybus.web.clients.UserClient;

/*
 * checks if the submitted user name already exists.
 */

@Component
@Scope("request")
public class UsernameValidator implements Validator {

	
	private UserClient userClient;

	@PostConstruct
	public void init(){
		userClient = new UserClient();
	}
	
	@Override
	public void validate(FacesContext ctx, UIComponent component, Object value)
			throws ValidatorException {
		if (userClient.getUser((String) value) != null) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Error",
					"Entered username already exists"));
		}
	}
}
