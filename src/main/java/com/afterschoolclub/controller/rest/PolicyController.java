package com.afterschoolclub.controller.rest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.controller.AdminController;
import com.afterschoolclub.service.PolicyService;
import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Class to retrieve and upload policy for AfterSchool CLub
 */

@RestController
public class PolicyController {

	@Autowired
    private PolicyService policyService;

    private final SessionBean sessionBean;	
    
	static Logger logger = LoggerFactory.getLogger(AdminController.class);

	
    @Autowired
    public PolicyController(SessionBean sessionBean) {
        this.sessionBean = sessionBean;

    }    
    

    
	
    /**
     * Endpoint to upload a policy for AfterSchool CLub
     * 
     * @param file - the file that is the policy
     * @param filename - filename for the policy
     * @return - response indicating success or not
     */
    
    
    @PostMapping("/policies")
    public ResponseEntity<String> uploadPolicy(@RequestParam("file") MultipartFile file, 
   		 @RequestParam(name="filename", required=true) String filename) {
    	ResponseEntity<String> response = null;    	
    	if (sessionBean.isLoggedOn()) {       		            
	    	try {
	            // Save the file to the directory
	        	String filePath;
	     		filePath = policyService.savePolicy(file, filename);        
	     		response = ResponseEntity.ok("Policy uploaded successfully: " + filePath);
	        } catch (IOException e) {
	        	response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading policy");
	        }
    	}
    	else {
    		response = ResponseEntity.status(HttpStatus.FORBIDDEN).body("Need to be logged on as administrator");    	
    	}
    	return response;
    }



    /**
     * Endpoint to return a policy
     * @param filename - filename of policy to retrieve
     * @return
     */
    
    @GetMapping("/policies/{filename}")
    public ResponseEntity<Resource> getPolicy(@PathVariable String filename) {
        try {
        	Resource resource = policyService.getResource(filename);
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}