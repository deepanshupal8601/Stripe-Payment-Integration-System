package com.hulkhiretech.payments.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.service.helper.StripeWebhookHelper;
import com.hulkhiretech.payments.stripe.StripeResponse;
import com.hulkhiretech.payments.util.JsonUtil;
import com.stripe.model.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessStripeEventAsync {
	
	private final JsonUtil jsonUtil;
	
	private final StripeWebhookHelper stripeWebhookHelper;

	private final HttpServiceEngine httpServiceEngine;
	
	private List<String> successEvents = List.of(
		    "checkout.session.completed",
		    "checkout.session.async_payment_succeeded"
		);
	
	private List<String> failedEvents = List.of(
		    "checkout.session.async_payment_failed"
		);
	
	@Async
	public void processStripeEvent(Event event) {
		//read Incomming Data
		//data.Object
		//Understand which event
		log.info("Recieved Stripe Event:{]",event.getType());
		
		if (!successEvents.contains(event.getType())
				&& !failedEvents.contains(event.getType())) {
			log.info("Ignoring Event Type: {}",event.getType());
			return; 
		}
		
		log.info("Processing Incomong Event: {}",event.getType());
			
		String eventAsJson =event.getDataObjectDeserializer().getRawJson();
	 	log.info("eventAsJson: {}",eventAsJson);
	 	
	 	StripeResponse response = jsonUtil.convertJsonToObject(
	 			eventAsJson, StripeResponse.class);
	 	log.info("Mapped StripeResponse: {}",response);
	 	
	 	
	 	if(successEvents.contains(event.getType())) {
	 		log.info("Payment Success Event Type: {}",event.getType());
     		//call internal processing  api to update status as SUCCESS
	 		if(response.getPaymentStatus().equals("paid")) {
	 			//make api call to processing service)
	 			triggerSuccessNotification(response);
	 			
	 		}
	 		else {
	 			log.warn("Payment not completed yet for eventType: {}",event.getType());
	 		}
     		log.info("Payment Success for Event: {}",event.getId());
     		return ;
	 	}
	 	
	 	if(failedEvents.contains(event.getType())) {
	 		//call internal processing  api to update status as FAILRD
	 		log.info("Payment Failure for Event: {}",event.getId());
	 		//TODO:make api call with field case.
	 		triggerFailedNotification(response);
	     		
	     	}
	 	
		//success/failure
		//Make Api call to processing Service 
		//response back
		log.info("Completed Processing Stripe Event: {}",event.getId());
	}
	private void triggerFailedNotification(StripeResponse response) {
		HttpRequest httpRequest = stripeWebhookHelper
				.prepareFailedNotificationRequest(response);
		log.info("Prepared FAILED notification request: {}", httpRequest);

		ResponseEntity<String> notificationResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("Response from processing-service for FAILED notification: {}", 
				notificationResponse);

		if(notificationResponse.getStatusCode().is2xxSuccessful()) {
			log.info("Successfully sent FAILED notification to processing-service");
		} else {
			log.error("Failed to send FAILED notification to processing-service. " + "Response: {}",
					notificationResponse);
		}
	}
	
	private void triggerSuccessNotification(StripeResponse response) {
		HttpRequest httpRequest = stripeWebhookHelper
				.prepareSuccessNotificationRequest(response);
		log.info("Prepared SUCCESS notification request: {}", httpRequest);

		ResponseEntity<String> notificationResponse = httpServiceEngine
				.makeHttpCall(httpRequest);
		log.info("Response from processing-service for FAILED notification: {}", 
				notificationResponse);

		if(notificationResponse.getStatusCode().is2xxSuccessful()) {
			log.info("Successfully sent FAILED notification to processing-service");
		} else {
			log.error("Failed to send FAILED notification to processing-service. " + "Response: {}",
					notificationResponse);
		}
	}
	
	
	

}
