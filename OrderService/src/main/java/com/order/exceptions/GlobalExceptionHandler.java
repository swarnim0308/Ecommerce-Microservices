package com.order.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<HttpResponse> OrderNotFoundExceptionExceptionHandler() {
		return createHttpResponse(HttpStatus.NOT_FOUND, "Order Not Found.");
	}

	private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
		return new ResponseEntity<HttpResponse>(
				new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message),
				httpStatus);
	}
}