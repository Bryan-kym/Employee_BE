package com.bryan.staff.exception.domain;

public class EmailNotFoundException extends Exception{
	public EmailNotFoundException(String message) {
		super(message.toLowerCase());
	}

}
