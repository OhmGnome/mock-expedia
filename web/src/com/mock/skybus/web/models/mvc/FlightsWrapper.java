package com.mock.skybus.web.models.mvc;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.mock.skybus.web.models.orm.Flight;

/**
 * A wrapper object that helps to marshal and unmarshal a list. used to send xml
 * information through HTTPrequest (REST). When a list of flights is retrieved
 * to find a path of travel, integer distance is extra information that
 * distinguishes a list of flights by the flights combined distances. When a
 * list of flights is retrieved from the database to as a representation of a
 * user's fair, distance represents the trip id of that fair.
 * 
 * @author ZGT43
 *
 */
@XmlRootElement
public class FlightsWrapper {
	Integer distance = 0;
	List<Flight> list;

	@XmlElement
	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	@XmlElement(name = "Item")
	public List<Flight> getList() {
		return list;
	}

	public void setList(List<Flight> list) {
		this.list = list;
	}
}