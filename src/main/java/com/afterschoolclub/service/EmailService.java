package com.afterschoolclub.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.afterschoolclub.data.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {
 
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
	static Logger logger = LoggerFactory.getLogger(EmailService.class);
    
 
    public EmailService(JavaMailSender emailSender, TemplateEngine templateEngine){
      this.mailSender = emailSender;
      this.templateEngine = templateEngine;
      }
    
    public void sendEmail(String to, String from, String subject, String body)  throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(body);
 
        mailSender.send(message);
    }
 
 @Async
public void  sendHTMLEmail(Email email) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    message.setFrom(new InternetAddress(email.getFrom()));
    message.setRecipients(MimeMessage.RecipientType.TO, email.getRecipient());
    message.setSubject(email.getSubject()); 
    message.setContent(email.getHtmlMessage(), "text/html; charset=utf-8");
    mailSender.send(message);
}
 
 @Async
public void  sendAllHTMLEmails(List<Email> allEmails) throws MessagingException {
	 for (Email email : allEmails) {
		 sendHTMLEmail(email);
	 }
} 

 @Async
 public void test() throws InterruptedException {
	 for (int i = 0 ; i < 100; i++) {
		 logger.info(String.format("Hello %d", i));
		 Thread.sleep(1000);
	 }
 }
 
 public String getHTML(String template, Context context) {
	 return templateEngine.process(template, context);
 }
 
 
}
