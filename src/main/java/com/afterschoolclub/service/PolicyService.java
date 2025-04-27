package com.afterschoolclub.service;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


/**
 * Services that allows the upload of new policies
 */
@Service
public class PolicyService {

    /**
     * Directory where policies should be stored
     */
    @Value("${asc.file.policyDocuments}")
    private String uploadDir;
    
	/**
	 * 
	 */
	public PolicyService() {
		super();
	}

    /**
     * Save a policy identified by file 
     * @param file - the policy
     * @param filename - the filename where to save the policy
     * @return the path to the policy
     * @throws IOException
     */
    public String savePolicy(MultipartFile file, String filename) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }
    
    
	
    
	/**
	 * Get the policy urk for a given filename
	 * @param filename - filename of policy
	 * @return the full url to policy
	 */
	public String getPolicyURL(String filename) {
		String url;
		if (policyExists(filename)) {
        	url = String.format("./policies/%s",  filename);        	
        }
        else {
        	url =  "./missingPolicy";
        }

		return url;
	}    
	
	/**
	 *  Returns true if policy exists otherwise returns false
	 * @param filename - fiename of policy
	 * @return true if policy exists otherwise returns false
	 */
	public boolean policyExists(String filename) {
		Path filePath = Paths.get(uploadDir).resolve(filename);
        File f = new File(filePath.toUri());        
        return f.isFile();
		
	}    
	
	
	
	
	/**
	 * Return the resource for the filename
	 * @param filename - filename of the resource
	 * @return the policy resource for the specific filename i.e. the pdf file
	 * @throws MalformedURLException
	 */
	public Resource getResource(String filename) throws MalformedURLException {	
		Path filePath = Paths.get(uploadDir).resolve(filename);
		return new UrlResource(filePath.toUri());
	}

      
    
}
