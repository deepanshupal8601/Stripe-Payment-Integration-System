package com.hulkhiretech.payments.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import com.hulkhiretech.payments.stripe.StripeResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(Constant.PAYMENTS)
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
	
	private final PaymentService paymentService;
	
	@PostMapping
	public PaymentResponse createPayment(
			@RequestBody CreatePaymentRequest createPaymentRequest) {
		log.info("Creating a new payment| "
				+ "createPaymentRequest:{}", createPaymentRequest);
		
		PaymentResponse response = paymentService.createPayment(createPaymentRequest);
		log.info("Payment creation response: {}", response);
		
		return response;
	}
	
	@GetMapping("/{Id}")
	public PaymentResponse getPayment(@PathVariable String Id) {
		log.info("Get Payment called id: {}", Id);
		
		PaymentResponse response=paymentService.getPayment(Id);
		log.info("Get Payment response: {}", response);
		
        return response;
	}
	
}
