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

import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.repository.MenuOptionRepository;
import com.afterschoolclub.SessionBean;
import com.afterschoolclub.controller.AdminController;
import com.afterschoolclub.data.State;

/**
 * CRUD Controller for MenuGroup entity
 */
@RestController
@RequestMapping("api/menuoption")
public class MenuOptionController {
	
	
    private final SessionBean sessionBean;	
    
	static Logger logger = LoggerFactory.getLogger(AdminController.class);

	
    @Autowired
    public MenuOptionController(SessionBean sessionBean, MenuOptionRepository menuOptionRepository) {
        this.sessionBean = sessionBean;
        MenuOption.repository = menuOptionRepository;
    }    
    
    /**
     * Returns all the MenuOption
     * @return Iterable collection of MenuOption
     */	
    @GetMapping
    public Iterable<MenuOption> getAllMenuOptions() {
    	Iterable<MenuOption> result = null;
    	if (sessionBean.isLoggedOn()) {       	
    		result = MenuOption.findAll();
    	}
	   	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
		}
		return result;      	
    }

    /**
     * Endpoint to retrieve a MenuOption
     * @param id - primary key for the MenuOption
     * @return - MenuGroup matching the primary key
     */    
    @GetMapping(value="/{id}")
    public Optional<MenuOption> getStaffById(@PathVariable long id) {
    	Optional<MenuOption> result = null;
    	if (sessionBean.isLoggedOn()) {       	
    		MenuOption mg = MenuOption.findById((int)id);
    		result = Optional.of(mg);
    	}
	   	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
		}
		return result;       	
    }

    /**
     * Endpoint to create a new MenuOption
     * @param menuOption - MenuOption to create
     * @return MenuOption created
     */    
    @PostMapping(consumes = {"application/json"})
    public MenuOption createMenuOption(@RequestBody MenuOption menuOption) {
    	if (sessionBean.isLoggedOn()) {      	
	    	MenuOption o = MenuOption.findByName(menuOption.getName());
	    	if (o != null) {
				throw new ResponseStatusException(
			           HttpStatus.BAD_REQUEST, "Already exists with same name");
	    	}    		
	    	else {
	    		menuOption.save();
	    	}	    	
    	}
	   	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
		}
        return menuOption;
    }

    /**
     * Endpoint to update a MenuOption
     * @param id - primary key of the MenuOption
     * @param menuOption - object representing updated state
     * @return - updated MenuOption
     */
        
    @PutMapping(value="/{id}", consumes = {"application/json"})
    public MenuOption updateMenuOption(@PathVariable long id, @RequestBody MenuOption menuOption) {
    	if (sessionBean.isLoggedOn()) {      	    	
	    	MenuOption o = MenuOption.findByName(menuOption.getName());
	    	if (o != null && id != o.getMenuOptionId()) {
				throw new ResponseStatusException(
			           HttpStatus.BAD_REQUEST, "Already exists with same name");
	    	}  
	    	else {
	    		menuOption.save();
	    	}
    	}
	   	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
		}
        return menuOption;    }
    
    /**
     * Endpoint to delete an MenuOption
     * @param id - primary key for the MenuOption
     */    
    @DeleteMapping(value="/{id}")
    public void deleteMenuOption(@PathVariable long id) {
    	if (sessionBean.isLoggedOn()) {      	    	    	
	    	MenuOption menuOption = MenuOption.findById((int) id);
	    	menuOption.setState(State.INACTIVE);
	    	menuOption.save();
    	}
	   	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
		}	    	
        return;
    }

}
