package com.mock.skybus.web.clients;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.mock.skybus.web.models.mvc.BooksWrapper;
import com.mock.skybus.web.models.orm.Book;

public class BookClient {

	Logger log = LoggerFactory.getLogger(BookClient.class);
	
	
	
	public void cancel(Book book) {
		log.info("entering BookClient.cancel(book)");
		
		final String uri = "http://localhost:8080/flight-planner-backend/cancel/POST";
		RestTemplate restTemplate = new RestTemplate();
		Book response = restTemplate.postForObject(uri, book, Book.class);
		
		log.info("leaving BookClient.cancel(username, flight)");
	}
	
	public void saveBook(List<Book> books) {
		log.info("entering BookClient.saveBook(books)");
		
		BooksWrapper booksWrapper = new BooksWrapper();
		booksWrapper.setList(books);
		final String uri = "http://localhost:8080/flight-planner-backend/books/POST";
		RestTemplate restTemplate = new RestTemplate();
		BooksWrapper response = restTemplate.postForObject(uri, booksWrapper, BooksWrapper.class);
		
		log.info("leaving BookClient.saveBook(books)");
	}
}