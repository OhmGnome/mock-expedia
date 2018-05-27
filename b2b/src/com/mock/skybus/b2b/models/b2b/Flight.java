package com.mock.skybus.b2b.models.b2b;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Flight implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final AtomicInteger NEXTID = new AtomicInteger(1);
	
	@XmlElement
	private Integer flightId;
	
	@XmlElement
	private Location origin;
	@XmlElement
	private Location destination;
	
	@XmlElement
	private Integer departure;
	@XmlElement
	private Integer eta;
	
	public static Integer getNextFlightID(){
		return NEXTID.incrementAndGet();
	}
	
	public Integer getFlightId() {
		return flightId;
	}
	public void setFlightId(Integer flightId) {
		this.flightId = flightId;
	}
	public Location getOrigin() {
		return origin;
	}
	public void setOrigin(Location origin) {
		this.origin = origin;
	}
	public Location getDestination() {
		return destination;
	}
	public void setDestination(Location destination) {
		this.destination = destination;
	}
	public Integer getDeparture() {
		return departure;
	}
	public void setDeparture(Integer departure) {
		this.departure = departure;
	}
	public Integer getEta() {
		return eta;
	}
	public void setEta(Integer eta) {
		this.eta = eta;
	}
}
