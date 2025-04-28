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


/**
 * CRUD Controller for MenuGroup entity
 */
@RestController
@RequestMapping("api/menugroup")
public class MenuGroupController {
	
	
    private final SessionBean sessionBean;	
    
	static Logger logger = LoggerFactory.getLogger(AdminController.class);

	
    /**
     * Spring autowired constructor
     * @param sessionBean - object that manage session state for the application
     * @param simpleMenuGroupOptionRepository - repository for SimpleMenuGroup
     */
	
    @Autowired
    public MenuGroupController(SessionBean sessionBean, SimpleMenuGroupRepository simpleMenuGroupOptionRepository) {
        this.sessionBean = sessionBean;
        SimpleMenuGroup.repository = simpleMenuGroupOptionRepository;
    }    
    
	
    /**
     * Returns all the MenuGroups
     * @return Iterable collection of MenuGroups
     */
    
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

    /**
     * Endpoint to retrieve a MenuGroup
     * @param id - primary key for the MenuGroup
     * @return - MenuGroup matching the primary key
     */
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

    /**
     * Endpoint to create a new MenuGroup
     * @param menuGroup - MenuGroup to create
     * @return MenuGroup created
     */
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

    /**
     * Endpoint to update a MenuGroup
     * @param id - primary key of the MenuGroup
     * @param menuGroup - object representing updated state
     * @return - updated MenuGroup
     */
    
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
    
    /**
     * Endpoint to delete an MenuGroup
     * @param id - primary key for the MenuGroup
     */
    
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
