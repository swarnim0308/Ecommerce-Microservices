package com.ms.customer.exception;

import java.util.Date;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class httpResponse {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
	private Date timeStamp;
	private int statusCode;
	private HttpStatus httpStatus;
	private String reason;
	private String message;

}
