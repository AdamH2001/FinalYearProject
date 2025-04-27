package com.afterschoolclub;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class used to retrieve the configuration for PayPal
 */
@Configuration

public class PaypalConfig {
	
	@Value("${paypal.client-id}")
	private String clientId;
	
	@Value("${paypal.client-secret}")
	private String clientSecret;
	
	@Value("${paypal.mode}")
	private String mode;
	
	/**
	 * Return an api context for calling PayPal 
	 * @return APIContext
	 */
	
	@Bean
	public APIContext apiContext() {
		return new APIContext(clientId, clientSecret, mode);
	}
	
	
	
	
}
