package com.ms.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {

	@ExceptionHandler(IdNotFoundException.class)
	public ResponseEntity<httpResponse> IdNotFoundExceptionHandler() {
		return createHttpResponse(HttpStatus.NOT_FOUND, "ID Not Found.");
	}

	private ResponseEntity<httpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
		return new ResponseEntity<>(new httpResponse(), httpStatus);
	}
}
