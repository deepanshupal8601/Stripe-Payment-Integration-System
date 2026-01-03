package com.hulkhire.payments.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.hulkhire.payments.constant.Constant;
import com.hulkhire.payments.http.HttpRequest;
import com.hulkhire.payments.pojo.CreatePaymentRequest;
import com.hulkhire.payments.pojo.LineItem;

import lombok.extern.slf4j.Slf4j;


@Service 
@Slf4j
public class CreatePaymengtHelper {
	
	@Value("${stripe.create-session.url}")
	private String createSessionUrl ;
	
	@Value("${stripe.api.key}")
	private String stripeApiKey;
	
	public  HttpRequest prepareHttpRequest(CreatePaymentRequest createPaymentRequest) {
		log.info("Preparing HttpRequest for CreatePaymentRequest: {}",
				createPaymentRequest);
		
	//Prepare headers
		HttpHeaders headers= new HttpHeaders();
		
		headers.setBasicAuth(stripeApiKey,Constant.EMPTY_STRING);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
        
     // Prepare from data
         
     		MultiValueMap<String ,String> requestBody = new LinkedMultiValueMap<>();
     		requestBody.add(Constant.MODE, Constant.MODE_PAYMENT); // by stripe-provider -service itself
     		requestBody.add(Constant.SUCCESS_URL, createPaymentRequest.getSuccessUrl());
     		requestBody.add(Constant.CANCEL_URL, createPaymentRequest.getCancelUrl());
     		
     		
     		
     		// Add line items
     		
     		for(int i=0; i<createPaymentRequest.getLineItems().size();i++) {
     			
     			LineItem lineItem = createPaymentRequest.getLineItems().get(i);
				requestBody.add("line_items["+i+"][price_data][currency]",
     					lineItem.getCurrency());
     			requestBody.add("line_items["+i+"][quantity]",
     					String.valueOf(lineItem.getQuantity()));
     			requestBody.add("line_items["+i+"][price_data][product_data][name]",
     					lineItem.getProductName());
     			requestBody.add("line_items["+i+"][price_data][unit_amount]",
     					String.valueOf(lineItem.getUnitAmount()));
     		}
     		
     		 

     		
     		HttpRequest httpRequest = new HttpRequest();
     		httpRequest.setHttpMethod(HttpMethod.POST);
     		
			httpRequest.setUrl(createSessionUrl);
     		httpRequest.setHttpHeaders(headers);
     		httpRequest.setRequestBody(requestBody);
     		
     		log.info("Prepared HttpRequest: {}",httpRequest);
		return httpRequest;
	}

}
