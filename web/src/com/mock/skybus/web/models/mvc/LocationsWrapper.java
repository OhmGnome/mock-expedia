package com.mock.skybus.web.models.mvc;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.mock.skybus.web.models.orm.Location;

/**
 * A wrapper object that helps to marshal and unmarshal a list. used to send xml
 * information through HTTPrequest (REST).
 * 
 * @author ZGT43
 *
 */
@XmlRootElement
public class LocationsWrapper{
	List<Location> list;

  @XmlElement(name="Item")
	public List<Location> getList() {
		return list;
	}

	public void setList(List<Location> list) {
		this.list = list;
	}
}