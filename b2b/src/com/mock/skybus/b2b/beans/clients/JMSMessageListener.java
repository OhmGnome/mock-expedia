package com.mock.skybus.b2b.beans.clients;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mock.skybus.b2b.models.b2b.Flight;
import com.mock.skybus.b2b.beans.dao.FlightDao;

/**
 * JMSMessageListener is a bean that is configured in the application context.
 * There, the topic is set, a broker is set, the connection factory is made and
 * a connection is connected. However, a broker is configured in the front end
 * for convenience, otherwise it would in a real use case scenario the broker
 * would reside in a different project, but more likely on a different machine.
 * JMSMessageListener listens for a message.
 * 
 * @author ZGT43
 *
 */
@Component("JMSMessageListener")
public class JMSMessageListener implements MessageListener {

	private Logger log = LoggerFactory.getLogger(JMSMessageListener.class);

	@Autowired
	FlightDao flightDao;

	com.mock.skybus.b2b.models.orm.Flight hop = null;

	/**
	 * When a message is recieved, if the message FlightStatus = Flight Arrived,
	 * the message object is expected to be a single flight. set that flights
	 * arrived boolean to true and update it in the database. If the
	 * FlightStatus = Flights Delayed, for every flight delayed, increment that
	 * flights eta, set its delayed boolean to true, and update that flight in
	 * the database.
	 */
	public void onMessage(Message message) {

		log.info("JMSMessageListener.OnMessage(Message) entered");
		if (message instanceof ObjectMessage) {

			ObjectMessage msg = (ObjectMessage) message;

			try {
				if (msg.getStringProperty("FlightStatus").equals(
						"Flight Arrived")) {
					Flight flight = (Flight) msg.getObject();
					hop = flightDao.getFlight(flight.getFlightId());
					hop.setArrived(true);
					flightDao.updateFlight(hop);
					log.warn("Flight{} has arrived", flight.getFlightId());

				} else if (msg.getStringProperty("FlightStatus").equals(
						"Flights Delayed")) {
					List<Flight> flights = (List<Flight>) msg.getObject();
					
					if (flights.size() > 0) {
						log.warn("Several flights have been delayed : {}",
								flights.size());
						
						for (Flight flight : flights) {
							hop = flightDao.getFlight(flight.getFlightId());
							hop.setPostponed(true);
							flightDao.updateFlight(hop);
							log.warn("Flight{} has been delayed", flight.getFlightId());
						}
					}
					log.warn("No flights have been delayed : {}",
							flights.size());
				}
			} catch (JMSException e) {
				log.error("JMSMessageListener.onMessage(message) could not retrieve FlightStatus");
				e.printStackTrace();
			}
		}
	}
}