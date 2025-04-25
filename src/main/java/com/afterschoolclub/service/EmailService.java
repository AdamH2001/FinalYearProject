package com.afterschoolclub.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.afterschoolclub.data.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


/**
 * Service to be able to send emails
 */
@Service
public class EmailService {
 
    /**
     *  java mail sender used to send the email
     */
	
    private final JavaMailSender mailSender;
    
    /**
     * template engine used to construct the message
     */
    private final TemplateEngine templateEngine;
    
    
	/**
	 * logger to can out put debug messages
	 */
	static Logger logger = LoggerFactory.getLogger(EmailService.class);
    
 
    /**
     *  Constructor for service 
     *  
     * @param emailSender - java mail sender
     * @param templateEngine - template engine
     */
    public EmailService(JavaMailSender emailSender, TemplateEngine templateEngine){
      this.mailSender = emailSender;
      this.templateEngine = templateEngine;
      }
    

 
 /**
  * Method to send an HTML email
 * @param email - email to actually send 
 * @throws MessagingException
 */
@Async
public void  sendHTMLEmail(Email email) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    message.setFrom(new InternetAddress(email.getFrom()));
    message.setRecipients(MimeMessage.RecipientType.TO, email.getRecipient());
    message.setSubject(email.getSubject()); 
    message.setContent(email.getHtmlMessage(), "text/html; charset=utf-8");
    mailSender.send(message);
}
 
 /**
  * Method to send alist of emails 
 * @param allEmails - list of email objects to send
 * @throws MessagingException
 */
@Async
public void  sendAllHTMLEmails(List<Email> allEmails) throws MessagingException {
	 for (Email email : allEmails) {
		 sendHTMLEmail(email);
	 }
} 

 
 /**
  * Return the html content for the email given the template and a context object
  * 
 * @param template - template for content
 * @param context - contaxt of email 
 * @return the html message
 */

public String getHTML(String template, Context context) {
	 return templateEngine.process(template, context);
 }
 
 
}
