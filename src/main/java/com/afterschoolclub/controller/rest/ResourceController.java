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
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.web.bind.annotation.RestController;

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.controller.AdminController;
import com.afterschoolclub.data.Resource;

import com.afterschoolclub.data.State;
import com.afterschoolclub.data.repository.ResourceRepository;
@RestController
@RequestMapping("api/resources")

public class ResourceController {

	
    private final SessionBean sessionBean;	
    
	static Logger logger = LoggerFactory.getLogger(AdminController.class);

	
    @Autowired
    public ResourceController(SessionBean sessionBean, ResourceRepository resourceRepository) {
        this.sessionBean = sessionBean;
        Resource.repository = resourceRepository;
    }        
    
    @GetMapping
    public List<Resource> getAllResources() {
    	return Resource.findAllActive();
    }

    @GetMapping(value="/{id}")
    public Optional<Resource> getResourceById(@PathVariable long id) {
    	Resource r = Resource.findById((int)id);
    	r.getMaxDemand();
    	return Optional.of(r);
    }

    @PostMapping(consumes = {"application/json"})
    public Resource createResource(@RequestBody Resource resource) {
    	resource.save();
        return resource;
    }

    @PutMapping(value="/{id}", consumes = {"application/json"})
    public Resource updateResource(@PathVariable long id, @RequestBody Resource resource) {
    	resource.save();
        return resource;
    }

    
    @DeleteMapping(value="/{id}")
    public void deleteResource(@PathVariable long id) {    	
    	Resource r = Resource.findById((int) id);
    	r.setState(State.INACTIVE);
    	r.save();    	
        return;
    }

}
