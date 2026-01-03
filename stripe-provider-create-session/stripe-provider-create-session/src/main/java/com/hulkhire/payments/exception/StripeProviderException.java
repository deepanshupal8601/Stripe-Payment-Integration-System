package com.hulkhire.payments.exception;


import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString

public class StripeProviderException extends RuntimeException {

    
	private static final long serialVersionUID = 8299241928708977276L;
	private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    public StripeProviderException(
    		String errorCode,
    		String errorMessage,
    		HttpStatus httpStatus) 
    {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

   
}

