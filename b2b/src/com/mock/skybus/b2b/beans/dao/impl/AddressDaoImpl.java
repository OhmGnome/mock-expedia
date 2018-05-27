package com.mock.skybus.b2b.beans.dao.impl;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mock.skybus.b2b.beans.clients.FlightModelClient;
import com.mock.skybus.b2b.beans.dao.AddressDao;
import com.mock.skybus.b2b.models.orm.Address;
import com.mock.skybus.b2b.models.orm.User;

/**
 * Implements the methods determined by the AddressDao interface. Accesses the
 * database for the address model. Spring annotation helps spring manifest the
 * the same session across data access objects unitarily.
 * 
 * @author ZGT43
 *
 */
@Repository
@Transactional
public class AddressDaoImpl implements AddressDao {
	
	private Logger log = LoggerFactory.getLogger(FlightModelClient.class);

	@Autowired
	SessionFactory factory;

	/**
	 * This method can save a new address to the database, or, update the
	 * address information if it already exists in the database. If the address
	 * id is not null, the address does exist in the database and its
	 * information will be updated if new information has been provided for the
	 * fields (if a field is not null).
	 */
	@Override
	public void saveAddress(Address address) {
		log.info("entering AddressDaoImpl.saveAddress(Address address)");
		
		Session session = factory.getCurrentSession();
		if (address.getId() == null) {
			log.warn("saving a new address");
			session.save(address);
			
		} else {
			log.warn("updating an existing address");
			Address exists = address;
			if (address.getCity() != null)
				exists.setCity(address.getCity());
			if (address.getState() != null)
				exists.setState(address.getState());
			if (address.getStreet() != null)
				exists.setStreet(address.getStreet());
			if (address.getZip() != null)
				exists.setZip(address.getZip());
			session.update(exists);
		}
		log.info("leaving AddressDaoImpl.saveAddress(Address address)");
	}

	/**
	 * This method gets an address from the database given a user.
	 */
	@Override
	public Address getAddress(User user) {
		log.info("entering and leaving AddressDaoImpl.getAddress({})", user.getName());
		Session session = factory.getCurrentSession();
		return (Address) session
				.createQuery(
						"Select a FROM Address a, User u WHERE u = :user AND a.user = u")
				.setEntity("user", user).uniqueResult();
	}

}
