package com.afterschoolclub;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
 
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
 
@Service
public class EmailService {
 
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
 
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
    
public void  sendHTMLEmail(String to, String from, String subject, String html) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    message.setFrom(new InternetAddress(from));
    message.setRecipients(MimeMessage.RecipientType.TO, to);
    message.setSubject(subject);
 
    message.setContent(html, "text/html; charset=utf-8");
    mailSender.send(message);
}
 
public void  sendTemplateEmail(String to, String from, String subject, String template, Context context) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    message.setFrom(new InternetAddress(from));
    message.setRecipients(MimeMessage.RecipientType.TO, to);
    message.setSubject(subject);
 
    // Process the template with the given context
        String html = templateEngine.process(template, context);
    
    message.setContent(html, "text/html; charset=utf-8");
    mailSender.send(message);
}
}
