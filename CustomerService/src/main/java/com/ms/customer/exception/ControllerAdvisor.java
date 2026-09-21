package com.ms.customer.exception;

import java.util.Date;

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

	@ExceptionHandler(Exception.class)
	public ResponseEntity<httpResponse> genericHandler(Exception e) {
		return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
	}

	private ResponseEntity<httpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
		return new ResponseEntity<>(new httpResponse(new Date(), httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase(), message), httpStatus);
	}
}
