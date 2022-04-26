package com.bryan.staff.exception.domain;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import static org.springframework.http.HttpStatus.*;

import java.io.IOException;
import java.util.Objects;

import javax.persistence.NoResultException;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.bryan.staff.domain.HttpResponse;

@RestControllerAdvice
public class ExceptionHandling implements ErrorController {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact Admin";
	private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint";
	private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
	private static final String INCORRECT_CREDENTIALS = "Username/password incorrect, please try again";
	private static final String ACCOUNT_DISABLED = "Your account has been disabled, please contact admin";
	private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
	private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
	public static final String ERROR_PATH = "/error";
	
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<HttpResponse> accountDisabledException(){
		return createHttpResponse(BAD_REQUEST,ACCOUNT_DISABLED);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<HttpResponse> accessDeniedException(){
		return createHttpResponse(FORBIDDEN,NOT_ENOUGH_PERMISSION);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<HttpResponse> badCredentialsException(){
		return createHttpResponse(BAD_REQUEST,INCORRECT_CREDENTIALS);
	}
	
	@ExceptionHandler(LockedException.class)
	public ResponseEntity<HttpResponse> lockedException(){
		return createHttpResponse(UNAUTHORIZED,ACCOUNT_LOCKED);
	}
	
	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception){
		return createHttpResponse(UNAUTHORIZED,exception.getMessage());
	}
	
	@ExceptionHandler(EmailExistsException.class)
	public ResponseEntity<HttpResponse> emailExistsException(EmailExistsException exception){
		return createHttpResponse(BAD_REQUEST,exception.getMessage());
	}
	
	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception){
		return createHttpResponse(BAD_REQUEST,exception.getMessage());
	}
	
	@ExceptionHandler(UsernameExistsException.class)
	public ResponseEntity<HttpResponse> usernameExistsException(UsernameExistsException exception){
		return createHttpResponse(BAD_REQUEST,exception.getMessage());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception){
		return createHttpResponse(BAD_REQUEST,exception.getMessage());
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception){
		HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods().iterator().next());
		return createHttpResponse(METHOD_NOT_ALLOWED,String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
	}
	
	/*@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException exception){
		return createHttpResponse(BAD_REQUEST,"This page is not found");
	}*/
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception){
		LOGGER.error(exception.getMessage());
		return createHttpResponse(INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR_MSG);
	}
	
	@ExceptionHandler(IOException.class)
	public ResponseEntity<HttpResponse> iOException(IOException exception){
		LOGGER.error(exception.getMessage());
		return createHttpResponse(INTERNAL_SERVER_ERROR,ERROR_PROCESSING_FILE);
	}
	
	@ExceptionHandler(NoResultException.class)
	public ResponseEntity<HttpResponse> notFoundException(NoResultException exception){
		LOGGER.error(exception.getMessage());
		return createHttpResponse(NOT_FOUND,exception.getMessage());
	}
	
	@ExceptionHandler(NotAnImageFileException.class)
	public ResponseEntity<HttpResponse> notAnImageFileException(NotAnImageFileException exception){
		LOGGER.error(exception.getMessage());
		return createHttpResponse(BAD_REQUEST,exception.getMessage());
	}
	
	private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
		
		HttpResponse httpResponse = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(),message.toUpperCase());
		
		return new ResponseEntity<>(httpResponse,httpStatus);
	}
	
	@RequestMapping(ERROR_PATH)
	public ResponseEntity<HttpResponse> notFound( ){
		return createHttpResponse(NOT_FOUND, "Page not found");
	}

	public String getErrorPath() {
		return ERROR_PATH;
	}
}
