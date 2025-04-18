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


@Service
public class PolicyService {

    @Value("${asc.file.policyDocuments}")
    private String uploadDir;
    
	public PolicyService() {
		super();
	}

    public String savePolicy(MultipartFile file, String filename) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }
    
    
	
    
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
	
	public boolean policyExists(String filename) {
		String url;
		Path filePath = Paths.get(uploadDir).resolve(filename);
        File f = new File(filePath.toUri());        
        return f.isFile();
		
	}    
	
	
	
	
	public Resource getResource(String filename) throws MalformedURLException {	
		Path filePath = Paths.get(uploadDir).resolve(filename);
		return new UrlResource(filePath.toUri());
	}

      
    
}
