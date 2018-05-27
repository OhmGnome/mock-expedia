package com.mock.skybus.web.beans;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Hashes the password for security purposes
 * @author ZGT43
 *
 */
public class Encrypt {
	public String encryptor(String password) {
		int workload = 12;
		
			String salt = BCrypt.gensalt(workload);
			String hashed_password = BCrypt.hashpw(password, salt);
	 
			return(hashed_password);
		}
	
	public Boolean decryptor(String password, String hash) {
			return BCrypt.checkpw(password, hash);
		}
	}
