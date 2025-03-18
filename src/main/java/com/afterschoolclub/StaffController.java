package com.afterschoolclub;

import com.afterschoolclub.data.Resource;
import com.afterschoolclub.data.rest.Staff;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("api/staff")
public class StaffController {
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
