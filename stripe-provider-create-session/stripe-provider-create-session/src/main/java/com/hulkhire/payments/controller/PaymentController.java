 package com.hulkhire.payments.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhire.payments.constant.Constant;
import com.hulkhire.payments.pojo.CreatePaymentRequest;
import com.hulkhire.payments.pojo.PaymentResponse;
import com.hulkhire.payments.service.interfaces.PaymentService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@RestController
@RequestMapping(Constant.PAYMENTS)

@Slf4j
@RequiredArgsConstructor
public class PaymentController {
	
	private final PaymentService paymentService;

	@PostMapping
	public PaymentResponse createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
	   log.info("Create new Payments"
			   + "createPaymentRequest:{}",createPaymentRequest);		
	    
	    PaymentResponse response  =   paymentService.createPayment(createPaymentRequest);
	    
	    log.info("Payment creation response:{}",response);
		return response;
	}
	
	
	 // ⭐ Using Constant.SUCCESS
    @GetMapping(Constant.SUCCESS)
    public String paymentSuccess() {
        log.info("✅ Payment completed successfully!");
        return "✅ Payment Successful! Thank you for your purchase. 🎉";
    }
    
    // ⭐ Using Constant.CANCEL
    @GetMapping(Constant.CANCEL)
    public String paymentCancel() {
        log.info("❌ Payment was cancelled by user");
        return "❌ Payment Cancelled. Please try again.";
    }
	
	@PostConstruct
	public void init() {
		log.info("PaymentController initialized  || paymentService:{}",paymentService);
	}

}
