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
    
	
    @GetMapping
    public Iterable<MenuOption> getAllMenuOptions() {
    	return MenuOption.findAll();
    }

    @GetMapping(value="/{id}")
    public Optional<MenuOption> getStaffById(@PathVariable long id) {
    	MenuOption mg = MenuOption.findById((int)id);
    	return Optional.of(mg);
    }

    @PostMapping(consumes = {"application/json"})
    public MenuOption createMenuOption(@RequestBody MenuOption menuOption) {
    	MenuOption o = MenuOption.findByName(menuOption.getName());
    	if (o != null) {
			throw new ResponseStatusException(
		           HttpStatus.BAD_REQUEST, "Already exists with same name");
    	}    		
    	else {
    		menuOption.save();
    	}
    	
    	
    	menuOption.save();
        return menuOption;
    }

    @PutMapping(value="/{id}", consumes = {"application/json"})
    public MenuOption updateMenuOption(@PathVariable long id, @RequestBody MenuOption menuOption) {
    	MenuOption o = MenuOption.findByName(menuOption.getName());
    	if (o != null && id != o.getMenuOptionId()) {
			throw new ResponseStatusException(
		           HttpStatus.BAD_REQUEST, "Already exists with same name");
    	}  
    	else {
    		menuOption.save();
    	}
        return menuOption;
    }
    
    @DeleteMapping(value="/{id}")
    public void deleteMenuOption(@PathVariable long id) {    	
    	MenuOption menuOption = MenuOption.findById((int) id);
    	menuOption.setState(State.INACTIVE);
    	menuOption.save();
        return;
    }

}
