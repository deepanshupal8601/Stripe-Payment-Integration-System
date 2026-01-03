package com.hulkhire.payments.service.impl;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhire.payments.constant.ErrorCodeEnum;
import com.hulkhire.payments.exception.StripeProviderException;
import com.hulkhire.payments.http.HttpRequest;
import com.hulkhire.payments.http.HttpServiceEngine;
import com.hulkhire.payments.pojo.CreatePaymentRequest;
import com.hulkhire.payments.pojo.PaymentResponse;
import com.hulkhire.payments.service.helper.CreatePaymengtHelper;
import com.hulkhire.payments.service.interfaces.PaymentService;
import com.hulkhire.payments.util.JsonUtil;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	private final HttpServiceEngine httpServiceEngine;
	 
	
	
	private final JsonUtil jsonUtil;
	
	private final CreatePaymengtHelper createPaymentHelper;
	
	private final ChatClient chatClient;

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) {

        log.info("Processing payment creation ||"
        		+"createPaymentRequest: {}", createPaymentRequest);
        
//        String name = null;
//        name.length();
         
        
        
        // if createPaymentRequest 1st line item quantity is 0 or less throw exception
        
        if(createPaymentRequest.getLineItems().isEmpty()
        		|| createPaymentRequest.getLineItems().get(0).getQuantity()<=0) {
        	
        	   
        	throw new StripeProviderException(
        			ErrorCodeEnum.INVALID_QUANTITY.getErrorCode(),
        			ErrorCodeEnum.INVALID_QUANTITY.getErrorMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
        
        HttpRequest httpRequest = createPaymentHelper.prepareHttpRequest(createPaymentRequest);
     		
     	log.info("Prepared HttpRequest:{}",httpRequest);
     	// Make the HTTP call
        ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
        log.info("HTTP service response: {}", httpResponse);
        
       PaymentResponse response = processResponse(httpResponse);
        log.info("Final PaymentResponse to be returned: {}",response);

        return response;
    }
    
    
    private PaymentResponse processResponse(ResponseEntity<String>httpResponse) {
    	log.info("Processing HTTP response | httpResponse:{}",httpResponse);
    	
    	if(httpResponse.getStatusCode().is2xxSuccessful()) {
    		log.info("HTTP response is successful");
    		  PaymentResponse response=jsonUtil.convertJsonToObject(
    	        		httpResponse.getBody(),PaymentResponse.class)  ;
    	        
    		  log.info("Converted PaymentResponse:{}",response);
    		
    		  if(response != null 
    				  && response.getId()!=null
    				  && response.getUrl()!=null) {
    			  log.info("PaymentResponse is valid and contains necessary fields");
    			  return response;
    		  }
    	}
    	// if we reach this code , means it error & we shlod throw an exception
    	
    	if(httpResponse.getStatusCode().is4xxClientError()
    			|| httpResponse.getStatusCode().is5xxServerError()) {
    		log.error("HTTP response indicates client or server error: {}", httpResponse);
    		
    		
    		//what to pass in errorCode & errorMessage
    		
    		String errorMessage=prepareErrorSummaryMessage(httpResponse);
    		log.error("Prepared error message from AI model: {}",errorMessage);
    		
    		throw new StripeProviderException(
    				ErrorCodeEnum.STRIPE_ERROR.getErrorCode(),
    				errorMessage,
    				HttpStatus.valueOf(httpResponse.getStatusCode().value())
    				);
    	}
    	
    	
    	
    	
    	throw new StripeProviderException(
    			ErrorCodeEnum.PAYMENT_CREATION_FAILED.getErrorCode(),
    			ErrorCodeEnum.PAYMENT_CREATION_FAILED.getErrorMessage(),
    			HttpStatus.INTERNAL_SERVER_ERROR
    			);
    }
    


    
    private String prepareErrorSummaryMessage(ResponseEntity<String> httpResponse) {
		// TODO Auto-generated method stub
    	
    	if(true) {
    		return httpResponse.getBody();
    		}
    	
    	String promptTemplate = """
				Given the following json message from a third-party API, read the entire JSON, and summarize in 1 line:
				Instructions:
				1. Put a short, simple summary. Which exactly represents what error happened.
				2. Max length of summary less than 200 characters.
				3. Keep the output clear and concise.
				4. Summarize as message that we can send in API response to the client.
				5. Dont point any info to read external documentation or link.
				{error_json}
				""";
		
		String errorJson = httpResponse.getBody();
		
		String response = chatClient.prompt()
				.system("You are an technical analyst. which just retunrs 1 line summary of the json error")
				.user(promptUserSpec -> promptUserSpec
						.text(promptTemplate)
						.param("error_json", errorJson))
				.call()
				.content();
 
		log.info("=>AI Model response: {}",response);
    	
		return response;
	}





	
	
}
