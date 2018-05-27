package com.mock.skybus.b2b.models.mvc;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper object that helps to marshal and unmarshal a list. used to send xml
 * information through HTTPrequest (REST). Boolean delays is a piece of extra
 * information used in the front-end to warn the user that one of their flights
 * have been delayed.
 * 
 * @author ZGT43
 *
 */
@XmlRootElement
public class PathsWrapper {
	List<FlightsWrapper> paths;
	Boolean delays = false;

	@XmlElement
	public Boolean getDelays() {
		return delays;
	}

	public void setDelays(Boolean delays) {
		this.delays = delays;
	}

	@XmlElement(name = "Item")
	public List<FlightsWrapper> getPaths() {
		return paths;
	}

	public void setPaths(List<FlightsWrapper> paths) {
		this.paths = paths;
	}
}