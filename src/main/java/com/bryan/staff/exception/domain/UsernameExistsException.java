package com.bryan.staff.exception.domain;

public class UsernameExistsException extends Exception{
	public UsernameExistsException(String message) {
		super(message);
	}
}
