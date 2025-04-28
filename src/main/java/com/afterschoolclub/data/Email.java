package com.afterschoolclub.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class Email {
	/**
	 * Recipient of email 
	 */
	private String recipient;
	/**
	 * Send of email
	 */
	private String from;
	/**
	 * Subject of email 
	 */
	private String subject;
	/**
	 * HTML content for email
	 */
	private String htmlMessage;
	
	/**
	 * Constructor for Email
	 * @param recipient
	 * @param from
	 * @param subject
	 * @param htmlMessage
	 * 
	 */
	public Email(String recipient, String from, String subject, String htmlMessage) {
		super();
		this.recipient = recipient;
		this.from = from;
		this.subject = subject;
		this.htmlMessage = htmlMessage;
	}
	
	
		

}




