package com.company;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalErrorHandler {
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> HandleError(HttpServletRequest req, Exception ex){
		CustomError error = new CustomError(HttpStatus.BAD_REQUEST, ex.getMessage());
		return new ResponseEntity<Object>( error, new HttpHeaders(), error.getStatus());
		
	}

}
