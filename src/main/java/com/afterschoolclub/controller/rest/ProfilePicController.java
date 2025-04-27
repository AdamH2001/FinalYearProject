package com.afterschoolclub.controller.rest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.controller.AdminController;
import com.afterschoolclub.service.ProfilePicService;

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


@RestController
public class ProfilePicController {


	@Autowired		
    private ProfilePicService profilePicService;

    private final SessionBean sessionBean;	
    
	static Logger logger = LoggerFactory.getLogger(AdminController.class);

	
    @Autowired
    public ProfilePicController(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }    
    
    
	
    @PostMapping("/profilePics")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam(name="id", required=false, defaultValue="0") String studentId, 
    		 @RequestParam(name="filename", required=false, defaultValue="") String filename) {
    	ResponseEntity<String> response = null;
    	if (sessionBean.isLoggedOn()) {    	
	        try {
	            // Save the file to the directory
	        	String filePath;
	        	if (filename.length() >0) {
	        		filePath = profilePicService.saveImage(file, filename);
	        	}
	        	else {
	        		filePath = profilePicService.saveImage(file, studentId);
	        	}
	            System.out.print("StudentId=".concat(String.valueOf(studentId)));
	            response = ResponseEntity.ok("Image uploaded successfully: " + filePath);
	        } catch (IOException e) {
	        	response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
	        }
    	}
    	else {
    		response = ResponseEntity.status(HttpStatus.FORBIDDEN).body("Need to be logged on");    		
    	}
    	return response;
    }



    @GetMapping("/profilePics/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
    	ResponseEntity<Resource> response = null;
    	if (sessionBean.isLoggedOn()) {       	
	        try {
	        	Resource resource = profilePicService.getResource(filename);
	            
	            if (resource.exists()) {
	                return ResponseEntity.ok()
	                        .contentType(MediaType.IMAGE_JPEG)
	                        .body(resource);
	            } else {
	                return ResponseEntity.notFound().build();
	            }
	        } catch (MalformedURLException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
    	}	
    	else {
    		response = ResponseEntity.status(HttpStatus.FORBIDDEN).build();    		
    	}
    	return response;        
    }
}