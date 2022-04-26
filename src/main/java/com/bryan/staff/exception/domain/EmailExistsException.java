package com.bryan.staff.exception.domain;

public class EmailExistsException extends Exception{
	public EmailExistsException(String message) {
		super(message.toLowerCase());
	}

}
