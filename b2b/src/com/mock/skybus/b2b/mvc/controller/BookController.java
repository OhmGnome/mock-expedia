package com.mock.skybus.b2b.mvc.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mock.skybus.b2b.beans.dao.BookDao;
import com.mock.skybus.b2b.beans.dao.UserDao;
import com.mock.skybus.b2b.models.mvc.BooksWrapper;
import com.mock.skybus.b2b.models.orm.Book;

/**
 * BookController is a spring implementation of REST HTTP. It can get or send an
 * object across RESTful web services.
 * 
 * @author ZGT43
 *
 */
@Controller
public class BookController {

	private Logger log = LoggerFactory.getLogger(BookController.class);

	@Autowired
	private BookDao bookDao;
	@Autowired
	private UserDao userDao;

	/**
	 * Gets a book object from HTTP. Calls bookDao.cancel(book) to cancel the
	 * bookings with the same trip id as that book.
	 * 
	 * @param book
	 */
	@RequestMapping(value = "/cancel/POST", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody void cancel(@RequestBody Book book) {
		log.info("/cancel/POST entered");
		try {
			bookDao.cancel(book);
			log.info("/cancel/POST processed for book number {} ", book.getId());
		} catch (Exception e) {
			log.error("/cancel/POST failure for book {}", book);
			e.printStackTrace();
		}
	}

	/**
	 * Gets a BooksWrapper object from HTTP and list of bookings from it. Calls
	 * bookDao.book(books) to save those bookings to the user plus flight
	 * relational table (the book table).
	 * 
	 * @param booksWrapper
	 */
	@RequestMapping(value = "/books/POST", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody void bookPath(@RequestBody BooksWrapper booksWrapper) {
		log.info("/books/POST entered");
		try {
			List<Book> books = booksWrapper.getList();
			bookDao.book(books);
			log.info("/books/POST processed");
		} catch (Exception e) {
			log.error("/books/POST failure");
			e.printStackTrace();
		}
	}

}
