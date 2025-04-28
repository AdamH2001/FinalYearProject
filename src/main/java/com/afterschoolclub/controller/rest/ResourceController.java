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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.controller.AdminController;
import com.afterschoolclub.data.Resource;
import com.afterschoolclub.data.State;
import com.afterschoolclub.data.repository.ResourceRepository;
@RestController
@RequestMapping("api/resources")

/**
 * CRUD Controller for MenuGroup entity
 */
public class ResourceController {

	
    private final SessionBean sessionBean;	
    
	static Logger logger = LoggerFactory.getLogger(AdminController.class);

	
    @Autowired
    public ResourceController(SessionBean sessionBean, ResourceRepository resourceRepository) {
        this.sessionBean = sessionBean;
        Resource.repository = resourceRepository;
    }        
    
    /**
     * Returns all the Resources
     * @return Iterable collection of Resources
     */    
    @GetMapping
    public List<Resource> getAllResources() {
    	List<Resource> result;    	
    	if (sessionBean.isLoggedOn()) {      	
    		result = Resource.findAllActive();
    	}
	   	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
		}     
    	return result;    	
    }

    /**
     * Endpoint to retrieve a Resource
     * @param id - primary key for the Resource
     * @return - Resource  matching the primary key
     */    
    @GetMapping(value="/{id}")
    public Optional<Resource> getResourceById(@PathVariable long id) {
    	Optional<Resource> result;    	
    	if (sessionBean.isLoggedOn()) {      	    	    	    	    	    		    	
	    	Resource r = Resource.findById((int)id);	  	
	    	r.getMaxDemand();
	    	result = Optional.of(r);
    	}
	   	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
		}     
    	return result;
    }

    /**
     * Endpoint to create a new Resource
     * @param resource - MenuGroup to create
     * @return Resource created
     */
    @PostMapping(consumes = {"application/json"})
    public Resource createResource(@RequestBody Resource resource) {
    	if (sessionBean.isLoggedOn()) {      	    	    	    	    	    	
	    	Resource r = Resource.findByNameAndType(resource.getName(), resource.getType());
	    	if (r != null) {
				throw new ResponseStatusException(
			           HttpStatus.BAD_REQUEST, "Already exists with same name");
	    	}    		
	    	else {
	    		resource.save();
	    	}
    	}
	   	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
		}        	    	
        return resource;
    }

    /**
     * Endpoint to update a Resource
     * @param id - primary key of the Resource
     * @param resource - object representing updated state
     * @return - updated Resource
     */
        
    @PutMapping(value="/{id}", consumes = {"application/json"})
    public Resource updateResource(@PathVariable long id, @RequestBody Resource resource) {
    	if (sessionBean.isLoggedOn()) {      	    	    	    	    	
	    	Resource existingResource = Resource.findByNameAndType(resource.getName(), resource.getType());
	    	if (existingResource != null && resource.getResourceId() != existingResource.getResourceId()) {
				throw new ResponseStatusException(
			           HttpStatus.BAD_REQUEST, "Already exists with same name");
	    	}        	
	    	resource.save();
		}
	   	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
		}        	
        return resource;
    }

    /**
     * Endpoint to delete an Resource
     * @param id - primary key for the Resource
     */    
    @DeleteMapping(value="/{id}")
    public void deleteResource(@PathVariable long id) {
    	if (sessionBean.isLoggedOn()) {      	    	    	    	
	    	Resource r = Resource.findById((int) id);
	    	r.setState(State.INACTIVE);
	    	r.save();
    	}
	   	else {
			throw new ResponseStatusException(
			           HttpStatus.FORBIDDEN, "Need to be logged in");    		
		}    	
        return;
    }

}
