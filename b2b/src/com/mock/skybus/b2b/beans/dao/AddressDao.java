package com.mock.skybus.b2b.beans.dao;

import com.mock.skybus.b2b.models.orm.Address;
import com.mock.skybus.b2b.models.orm.User;

/**
 * The Address data access object interface determines what methods the DAO
 * should contain and what they return in order to provide a platform for
 * extensibility across developer groups. It can save an address and get an address.
 * 
 * @author ZGT43
 *
 */
public interface AddressDao {
	public void saveAddress(Address address);
	public Address getAddress(User user);
}
