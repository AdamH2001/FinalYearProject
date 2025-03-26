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
import com.afterschoolclub.data.Staff;



@RestController
@RequestMapping("api/staff")
public class StaffController {
	
	
	
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
    	staff.save();
        return staff;
    }

    @PutMapping(value="/{id}", consumes = {"application/json"})
    public Staff updateStaff(@PathVariable long id, @RequestBody Staff staff) {
    	staff.save();
        return staff;
    }
    
    @DeleteMapping(value="/{id}")
    public void deleteStaff(@PathVariable long id) {    	
    	Staff staff = Staff.findById((int) id);
    	staff.inActivate();
    	//staff.setState(Resource.State.INACTIVE);
    	//staff.save();    	
        return;
    }

}
