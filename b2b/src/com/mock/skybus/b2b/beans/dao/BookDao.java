package com.mock.skybus.b2b.beans.dao;

import java.util.List;

import com.mock.skybus.b2b.models.orm.Book;

/**
 * The Flight data access object interface determines what methods the DAO
 * should contain and what they return in order to provide a platform for
 * extensibility across developer groups. It can book a group of flights, and
 * cancel a flight or group of flights utilizing the trip id
 * 
 * @author ZGT43
 *
 */
public interface BookDao {
	public void book(List<Book> books);

	public void cancel(Book book);
}
