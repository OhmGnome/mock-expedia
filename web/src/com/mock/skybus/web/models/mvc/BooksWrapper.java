package com.mock.skybus.web.models.mvc;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.mock.skybus.web.models.orm.Book;

/**
 * A wrapper object that helps to marshal and unmarshal a list. used to send xml
 * information through HTTPrequest (REST).
 * 
 * @author ZGT43
 *
 */
@XmlRootElement
public class BooksWrapper {
	List<Book> list;

	@XmlElement(name = "Item")
	public List<Book> getList() {
		return list;
	}

	public void setList(List<Book> list) {
		this.list = list;
	}
}