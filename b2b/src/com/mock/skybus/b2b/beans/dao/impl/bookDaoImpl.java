package com.mock.skybus.b2b.beans.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mock.skybus.b2b.beans.dao.BookDao;
import com.mock.skybus.b2b.models.orm.Book;
import com.mock.skybus.b2b.models.orm.Flight;

/**
 * Implements the methods determined by the BookDao interface. Accesses the
 * database for the address model. Spring annotation helps spring manifest the
 * the same session across data access objects unitarily.
 * 
 * @author ZGT43
 *
 */
@Repository
@Transactional
public class bookDaoImpl implements BookDao {

	private Logger log = LoggerFactory.getLogger(bookDaoImpl.class);

	@Autowired
	SessionFactory factory;

	/**
	 * Cancel every booking associated with the trip that the booking that
	 * cancel takes as a parameter has. It accomplishes this by getting a list
	 * of bookings from the database that matches the flight and user and trip
	 * information of the parameter book. Iterating through that list of books,
	 * each bookings cancel flag is set to true, and each flight's capacity
	 * (seats) associated with that booking is updated. The database is queried
	 * for the capacity before updating because the the book parameter pushed
	 * from the front-end will not contain up to date information if the same
	 * flight happened to be booked on that same page.
	 */
	@Override
	public void cancel(Book book) {
		log.info("entering BookDaoImpl.cancel(Book)");
		Session session = factory.getCurrentSession();
		List<Book> books = session
				.createQuery(
						"FROM Book b WHERE flight = :flight AND user = :user AND trip = :trip")
				.setEntity("flight", book.getFlight())
				.setEntity("user", book.getUser())
				.setInteger("trip", book.getTrip()).list();

		log.warn("Queried a list of books of size {} ", books.size());
		log.warn("setting cancel on bookings for trip {}", books.get(0)
				.getTrip());
		for (Book fair : books) {
			fair.setCanceled(true);
			session.update(fair);

			Integer seatsOccupied = (Integer) session
					.createQuery(
							"SELECT f.seats FROM Flight f WHERE f = :flight")
					.setEntity("flight", fair.getFlight()).uniqueResult();
			seatsOccupied--;
			Flight flight = fair.getFlight();
			flight.setSeats(seatsOccupied);
			session.update(flight);
			log.warn("Updated the number of passengers on flight {} to {}",
					flight.getId(), seatsOccupied);
		}
		log.info("leaving BookDaoImpl.cancel(Book)");
	}

	/**
	 * Book the first booking in order to get the unique id from the primary key
	 * of the table. Use that number to set the trip id of the subsequent books
	 * in the list of books past to the method as a parameter. A booking is
	 * considered booked when the booked boolean field is set to true. Each
	 * flight's capacity (seats) associated with the bookings is updated. The
	 * database is queried for the capacity before updating because the the book
	 * parameter pushed from the front-end will not contain up to date
	 * information if the same flight happened to be booked on that same page.
	 */
	@Override
	public void book(List<Book> books) {
		log.info("entering BookDaoImpl.book(List<Book)");
		log.info("canceling a list of books of size {} ", books.size());
		Session session = factory.getCurrentSession();
		Book book = books.get(0);
		book.setBooked(true);
		session.save(book);
		int trip = book.getId();
		book.setTrip(trip);
		session.update(book);

		log.warn("booking flights for trip {}", trip);

		Integer seatsOccupied = (Integer) session
				.createQuery(
						"SELECT f.seats FROM Flight f, Book b WHERE b = :book AND f = b.flight")
				.setEntity("book", book).uniqueResult();
		seatsOccupied++;
		Flight flight = book.getFlight();
		flight.setSeats(seatsOccupied);
		session.update(flight);
		log.warn("Updated the number of passengers on flight {} to {}",
				flight.getId(), seatsOccupied);

		/**
		 * Do the same thing for the rest of the bookings, now that we have a
		 * trip id for them and don't rebook the booking prior to this loop
		 */
		for (Book fair : books) {
			if (!fair.isBooked()) {
				fair.setBooked(true);
				fair.setTrip(trip);
				session.save(fair);

				seatsOccupied = (Integer) session
						.createQuery(
								"SELECT f.seats FROM Flight f, Book b WHERE b = :book AND f = b.flight")
						.setEntity("book", book).uniqueResult();
				seatsOccupied++;
				flight = book.getFlight();
				flight.setSeats(seatsOccupied);
				session.update(flight);
				log.info("Updated the number of passengers on flight {} to {}",
						flight.getId(), seatsOccupied);
			}
		}
		log.info("leaving BookDaoImpl.book(List<Book)");
	}
}
