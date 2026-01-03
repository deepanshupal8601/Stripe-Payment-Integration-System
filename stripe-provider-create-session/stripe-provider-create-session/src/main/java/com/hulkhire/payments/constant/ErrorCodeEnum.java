package com.hulkhire.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
	
	 GENERIC_ERROR("40000", "Unable to process the request at this time. Please try again later."),
    INVALID_QUANTITY("40001", "Invalid quantity. Provide 1 or above."), 
    UNABLE_TO_CONNECT_TO_STRIPE("40002","Unable to connect to stripe"),
    PAYMENT_CREATION_FAILED("40003","Payment creation failed"),
    STRIPE_ERROR("40004","<dynamically prepare from stripe error response>");
	
    
	private final String errorCode;
    private final String errorMessage;

    ErrorCodeEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }


}
