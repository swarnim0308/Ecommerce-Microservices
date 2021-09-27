package onlineretailstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFound extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public ProductNotFound(String message) {
		super(message);
	}

}
