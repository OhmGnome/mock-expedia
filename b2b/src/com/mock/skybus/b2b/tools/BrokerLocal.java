package com.mock.skybus.b2b.tools;

import org.apache.activemq.broker.BrokerService;

/**
 * An activeMQ broker that starts the broker in a new processes that handles
 * messages.
 * 
 * @author ZGT43
 *
 */
public class BrokerLocal {

	public static void main(String[] args) {
		BrokerService broker = new BrokerService();

		try {
			broker.addConnector("tcp://localhost:61616");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			broker.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
