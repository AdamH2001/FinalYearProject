package com.afterschoolclub.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.controller.AdminController;
import com.afterschoolclub.data.Email;
import com.afterschoolclub.data.Staff;
import com.afterschoolclub.data.User;
import com.afterschoolclub.service.EmailService;

import jakarta.mail.MessagingException;



@RestController
@RequestMapping("api/staff")
public class StaffController {
	
	@Autowired
	private EmailService mailService;	
	
    private final SessionBean sessionBean;	
    
	static Logger logger = LoggerFactory.getLogger(AdminController.class);

	
    @Autowired
    public StaffController(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }    
    
	
	
    @GetMapping
    public List<Staff> getAllStaff() {
    	return Staff.findAllActive();
    }

    @GetMapping(value="/{id}")
    public Optional<Staff> getStaffById(@PathVariable long id) {
    	Staff r = Staff.findById((int)id);
    	return Optional.of(r);
    }

    @PostMapping(consumes = {"application/json"})
    public Staff createStaff(@RequestBody Staff staff) {
    	//TODO whilst this works could handle it better. Should handle duplicate email address better.
    	User user = staff.save();
    	
		
		Context context = new Context();
		context.setVariable("user", user);
		context.setVariable("sessionBean", sessionBean);
		String link = String.format("%s/alterPassword?userId=%d&validationKey=%d", sessionBean.getHomePage(), user.getUserId(), user.getValidationKey());
		context.setVariable("link", link);			
		
		Email email = new Email(user.getEmail(), sessionBean.getContactEmail(), "After School Club New Staff Account", mailService.getHTML("email/newStaffAccount", context));
		
		try {
			mailService.sendHTMLEmail(email);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return staff;
    }

    @PutMapping(value="/{id}", consumes = {"application/json"})
    public Staff updateStaff(@PathVariable long id, @RequestBody Staff staff) {
    	staff.save();
        return staff;
    }
    
	@Transactional
    @DeleteMapping(value="/{id}")
    public void deleteStaff(@PathVariable long id) {    	
    	Staff staff = Staff.findById((int) id);
    	User user = staff.inActivate();
    	
		try {
			Context context = new Context();
			context.setVariable("user", user);
			context.setVariable("sessionBean", sessionBean);
			String link = String.format("%s/alterPassword?userId=%d&validationKey=%d", sessionBean.getHomePage(), user.getUserId(), user.getValidationKey());
			context.setVariable("link", link);
			
			Email email = new Email(user.getEmail(), sessionBean.getContactEmail(), "After School Club New Staff Removed", mailService.getHTML("email/staffAccountDisabled", context));
			
			try {
				mailService.sendHTMLEmail(email);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}    	
    	//staff.setState(Resource.State.INACTIVE);
    	//staff.save();    	
        return;
    }

}
