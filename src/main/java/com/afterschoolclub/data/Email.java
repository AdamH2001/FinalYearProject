package com.afterschoolclub.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString

public class Email {

	

	
	@Id
	private String recipient;
	private String from;
	private String subject;
	private String htmlMessage;
	
	public Email(String recipient, String from, String subject, String htmlMessage) {
		super();
		this.recipient = recipient;
		this.from = from;
		this.subject = subject;
		this.htmlMessage = htmlMessage;
	}
	
	
		

}




