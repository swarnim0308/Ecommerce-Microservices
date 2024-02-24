package onlineretailstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class ExceptionHandlerAdvice {
	@ExceptionHandler(ProductNotFound.class)
	public ResponseEntity<HttpResponse> productNotFoundExceptionHandler() {
	return createHttpResponse(HttpStatus.NOT_FOUND, "Product Not Found.");
	}
	private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
		return new ResponseEntity<>(
		new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message),
		httpStatus);
		}
}
