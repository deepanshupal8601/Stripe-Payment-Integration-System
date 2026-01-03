package com.hulkhiretech.payments.service.interfaces;

import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.stripe.StripeResponse;

public interface PaymentService {
	
	public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest);

	public PaymentResponse getPayment(String id);
}
