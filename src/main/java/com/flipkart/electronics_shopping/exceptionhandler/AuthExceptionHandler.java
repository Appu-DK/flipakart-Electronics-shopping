package com.flipkart.electronics_shopping.exceptionhandler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.flipkart.electronics_shopping.exception.InvalidUserRoleException;
import com.flipkart.electronics_shopping.exception.UserAlreadyRegisteredException;

@RestControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler{

	public ResponseEntity<Object> structure(HttpStatus status, String message, Object rootCause) {
		return new ResponseEntity<Object>(Map.of("status", status.value(), "message", message, "root cause", rootCause),
				status);
	}

	@ExceptionHandler(UserAlreadyRegisteredException.class)
	public ResponseEntity<Object> handlerUserAlreadyExistException(UserAlreadyRegisteredException exception) {
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "user alreday existing ");
	}
	@ExceptionHandler(InvalidUserRoleException.class)
	public ResponseEntity<Object> handlerInvalidUserRoleException(InvalidUserRoleException exception) {
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "user role is invalid ");
	}

	//	@ExceptionHandler(OtpExpiredException.class)
	//	public ResponseEntity<Object> otpExpiredException(OtpExpiredException ex){
	//		return structure(HttpStatus.REQUEST_TIMEOUT,ex.getMessage(), "entered otp is already expired");
	//	}
	//
	//	@ExceptionHandler(InvalidOtpException.class)
	//	public ResponseEntity<Object> invalidOtpException(InvalidOtpException ex){
	//		return structure(HttpStatus.UNAUTHORIZED,ex.getMessage(), "entered otp is invalid");	
	//	}
	//
	//	@ExceptionHandler(SessionTimeExpiredException.class)
	//	public ResponseEntity<Object> sessionTimeOutException(SessionTimeExpiredException ex){
	//		return structure(HttpStatus.REQUEST_TIMEOUT, ex.getMessage(), "cache session has been expired");
	//	}


}