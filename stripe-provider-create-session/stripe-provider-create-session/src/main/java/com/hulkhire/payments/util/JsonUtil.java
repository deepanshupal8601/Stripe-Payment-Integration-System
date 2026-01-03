package com.hulkhire.payments.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonUtil {
	private final ObjectMapper objectMapper;
	
	public <T> T convertJsonToObject(String jsonString,Class<T>valueType) {
		try {
			return objectMapper.readValue(jsonString, valueType);
			}
		catch(Exception e) {
			log.error("Error converting Json to Object: {}",e.getMessage());
			
			
			//TODO replace with custom exception , when error handling is implemented
			return null;
		}
	}

}
