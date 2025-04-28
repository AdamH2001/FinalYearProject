package com.afterschoolclub.controller.rest;

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.controller.AdminController;
import com.afterschoolclub.data.MenuGroupOption;
import com.afterschoolclub.data.SimpleMenuGroupOption;
import com.afterschoolclub.data.repository.MenuGroupOptionRepository;

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

import com.afterschoolclub.data.State;

/**
 * CRUD Controller for MenuGroup entity
 */
@RestController
@RequestMapping("api/menugroupoption")
public class MenuGroupOptionController {
	
	
    private final SessionBean sessionBean;	
    
	static Logger logger = LoggerFactory.getLogger(AdminController.class);

	
    @Autowired
    public MenuGroupOptionController(SessionBean sessionBean, MenuGroupOptionRepository menuGroupOptionRepository) {
        this.sessionBean = sessionBean;
        MenuGroupOption.repository = menuGroupOptionRepository;
    }    
    
    
    /**
     * Returns all the MenuGroupOptions
     * @return Iterable collection of MenuGroupOptions
     */
	
    @GetMapping
    public Iterable<SimpleMenuGroupOption> getAllMenuGroups() {
    	Iterable<SimpleMenuGroupOption> result = null;
    	if (sessionBean.isLoggedOn()) {    	
    		result = SimpleMenuGroupOption.findAll();
    	}
       	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
    	}
    	return result;    	
    }

    /**
     * Endpoint to retrieve a MenuGroupOption
     * @param id - primary key for the MenuGroupOption
     * @return - MenuGroupOptions matching the primary key
     */    
    @GetMapping(value="/{id}")
    public Optional<SimpleMenuGroupOption> getStaffById(@PathVariable long id) {
    	Optional<SimpleMenuGroupOption> result = null;
    	if (sessionBean.isLoggedOn()) {    	
    	   	MenuGroupOption mg = MenuGroupOption.findById((int)id);
	    	SimpleMenuGroupOption smgo = null;
	    	if (mg != null) {
	    		smgo = mg.getSimpleMenuGroupOption();
	    	}
    		result = Optional.of(smgo);
    	}
       	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
    	}    	
    	return result;
    }

    /**
     * Endpoint to create a new MenuGroupOption
     * @param simpleMenuGroupOption - MenuGroupOption to create
     * @return MenuGroupOption created
     */
    @PostMapping(consumes = {"application/json"})
    public SimpleMenuGroupOption createMenuGroup(@RequestBody SimpleMenuGroupOption simpleMenuGroupOption) {
    	SimpleMenuGroupOption result = null;
    	if (sessionBean.isLoggedOn()) {    	    	
	    	MenuGroupOption mgo = new MenuGroupOption(simpleMenuGroupOption);
	    	mgo.save();
	    	result = mgo.getSimpleMenuGroupOption();
    	}
       	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
    	}    	
    	return result;	    	
    }
    
    /**
     * Endpoint to update a MenuGroupOption
     * @param id - primary key of the MenuGroupOption
     * @param simpleMenuGroupOption - object representing updated state
     * @return - updated MenuGroupOption
     */
    
    @PutMapping(value="/{id}", consumes = {"application/json"})
    public SimpleMenuGroupOption updateMenuGroup(@PathVariable long id, @RequestBody SimpleMenuGroupOption simpleMenuGroupOption) {
    	SimpleMenuGroupOption result = null;
    	if (sessionBean.isLoggedOn()) {      	
	    	MenuGroupOption mgo = new MenuGroupOption(simpleMenuGroupOption);
	    	mgo.save();
	    	result = mgo.getSimpleMenuGroupOption();
    	}
       	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
    	}    	
    	return result;	
    }
    
    /**
     * Endpoint to delete an MenuGroupOption
     * @param id - primary key for the MenuGroup
     */    
    @DeleteMapping(value="/{id}")
    public void deleteMenuGroup(@PathVariable long id) {
    	if (sessionBean.isLoggedOn()) {      	
	    	MenuGroupOption mgo = MenuGroupOption.findById((int) id);    	
	    	mgo.setState(State.INACTIVE);
	    	mgo.save();
    	}
       	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
    	}    
        return;
    }

}
