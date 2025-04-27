package com.afterschoolclub.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestBody;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.SimpleMenuGroup;
import com.afterschoolclub.data.repository.SimpleMenuGroupRepository;
import com.afterschoolclub.SessionBean;
import com.afterschoolclub.controller.AdminController;
import com.afterschoolclub.data.State;


@RestController
@RequestMapping("api/menugroup")
public class MenuGroupController {
	
	
    private final SessionBean sessionBean;	
    
	static Logger logger = LoggerFactory.getLogger(AdminController.class);

	
    @Autowired
    public MenuGroupController(SessionBean sessionBean, SimpleMenuGroupRepository simpleMenuGroupOptionRepository) {
        this.sessionBean = sessionBean;
        SimpleMenuGroup.repository = simpleMenuGroupOptionRepository;
    }    
    
	
    @GetMapping
    public Iterable<SimpleMenuGroup> getAllMenuGroups() {
    	Iterable<SimpleMenuGroup> result = null;
    	if (sessionBean.isLoggedOn()) {
    		result = SimpleMenuGroup.findAll();
    	}
    	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
    	}
    	return result;
    }

    @GetMapping(value="/{id}")
    public Optional<SimpleMenuGroup> getStaffById(@PathVariable long id) {
    	Optional<SimpleMenuGroup> result = null;
    	if (sessionBean.isLoggedOn()) {    	
	    	SimpleMenuGroup mg = SimpleMenuGroup.findById((int)id);
	    	result = Optional.of(mg);
    	}
    	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
    	}
    	return result;    	
    }

    @PostMapping(consumes = {"application/json"})
    public SimpleMenuGroup createMenuGroup(@RequestBody SimpleMenuGroup menuGroup) {
    	if (sessionBean.isLoggedOn()) {     
    		MenuGroup mg = MenuGroup.findByName(menuGroup.getName());
	    	if (mg != null) {
				throw new ResponseStatusException(
			           HttpStatus.BAD_REQUEST, "Already exists with same name");
	    	}  
	    	else {    	
	    		menuGroup.save();
	    	}
    	}
    	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
    	}
        return menuGroup;
    }

    @PutMapping(value="/{id}", consumes = {"application/json"})
    public SimpleMenuGroup updateMenuGroup(@PathVariable long id, @RequestBody SimpleMenuGroup menuGroup) {
    	if (sessionBean.isLoggedOn()) {         	
	    	MenuGroup mg = MenuGroup.findByName(menuGroup.getName());
	    	if (mg != null && id != mg.getMenuGroupId()) {
				throw new ResponseStatusException(
			           HttpStatus.BAD_REQUEST, "Already exists with same name");
	    	}  
	    	else {    	
	    		menuGroup.save();
	    	}
    	}
    	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
    	}    	
    	return menuGroup;
    }
    
    @DeleteMapping(value="/{id}")
    public void deleteMenuGroup(@PathVariable long id) {
    	if (sessionBean.isLoggedOn()) {         	    	
	    	SimpleMenuGroup menuGroup = SimpleMenuGroup.findById((int) id);
	    	menuGroup.setState(State.INACTIVE);
	    	menuGroup.save();
    	}
    	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
    	}    	
        return;
    }

}
