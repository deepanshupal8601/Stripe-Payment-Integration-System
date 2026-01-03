package com.hulkhire.payments.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.hulkhire.payments.constant.ErrorCodeEnum;
import com.hulkhire.payments.exception.StripeProviderException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class HttpServiceEngine {
	 
	
	private RestClient restClient;
	
	public HttpServiceEngine(RestClient.Builder restClientBuilder) {
		this.restClient=restClientBuilder.build();
	}
	
	 
	public ResponseEntity<String>makeHttpCall(HttpRequest httpRequest) {
		log.info("Making on HTTP call");
		
		
		
		
		try {
		ResponseEntity<String>httpResponse=restClient
				.method(httpRequest.getHttpMethod())
				.uri(httpRequest.getUrl())
				.headers( t-> t.addAll(httpRequest.getHttpHeaders()))
				.body(httpRequest.getRequestBody())
				.retrieve()
				.toEntity(String.class);
		
		 
		 
		log.info("HTTP call completed with status code httpResponse:{}",httpResponse);

		
	     return httpResponse;
	}
		catch(HttpClientErrorException | HttpServerErrorException e) {
			//valid error response from stripe
			log.info("HTTP error response from stripe: {}",e.getMessage(),e);
			
			
			//  504 for gateway timeout   &  503 for service unavailable
			
			if(e.getStatusCode()==HttpStatus.GATEWAY_TIMEOUT
					|| e.getStatusCode()==HttpStatus.SERVICE_UNAVAILABLE) {
				
				throw new StripeProviderException(
						ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorCode(),
				        ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorMessage(),
				        HttpStatus.SERVICE_UNAVAILABLE);
			           
			}
			//  ResponseEntity with status code and body from exception and return the object
			return ResponseEntity.status(e.getStatusCode())
					.body(e.getResponseBodyAsString());
			
			
		}
		
		catch(Exception e) {
			log.error("Exception occured while making HTTP call: {}", e.getMessage(),e);
			
			throw new StripeProviderException(
        			ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorCode(),
        			ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorMessage(),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
		}
		
		
	}
	
	@PostConstruct
	public void init() {
		log.info("HttpServiceEngine initialized with RestClient:{}",restClient);
		
	} 
 
}
