package com.bryan.staff.exception.domain;

public class UserNotFoundException extends Exception{
	public UserNotFoundException(String message) {
		super(message.toLowerCase());
	}
}
