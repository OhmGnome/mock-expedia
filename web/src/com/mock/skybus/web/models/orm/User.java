package com.mock.skybus.web.models.orm;

// Generated Jun 4, 2015 4:17:55 PM by Hibernate Tools 4.3.1

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * User generated by hbm2java
 */
@XmlRootElement
public class User implements java.io.Serializable {

	private Integer id;
	private String name;
	private String password;
	private Set persons = new HashSet(0);
	private Set books = new HashSet(0);
	private Set addresses = new HashSet(0);

	public User() {
	}

	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public User(String name, String password, Set persons, Set books,
			Set addresses) {
		this.name = name;
		this.password = password;
		this.persons = persons;
		this.books = books;
		this.addresses = addresses;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@XmlTransient
	public Set getPersons() {
		return this.persons;
	}

	public void setPersons(Set persons) {
		this.persons = persons;
	}

	@XmlTransient
	public Set getBooks() {
		return this.books;
	}

	public void setBooks(Set books) {
		this.books = books;
	}

	@XmlTransient
	public Set getAddresses() {
		return this.addresses;
	}

	public void setAddresses(Set addresses) {
		this.addresses = addresses;
	}

}
