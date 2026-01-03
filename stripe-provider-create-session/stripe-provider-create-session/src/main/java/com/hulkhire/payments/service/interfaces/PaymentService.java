package com.hulkhire.payments.service.interfaces;

import com.hulkhire.payments.pojo.CreatePaymentRequest;
import com.hulkhire.payments.pojo.PaymentResponse;

public interface PaymentService {
	
	public PaymentResponse   createPayment(CreatePaymentRequest createPaymentRequest);   

}

