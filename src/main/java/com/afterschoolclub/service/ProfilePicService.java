package com.afterschoolclub.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.afterschoolclub.data.User;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


@Service
public class ProfilePicService {

    @Value("${asc.file.profilePics}")
    private String uploadDir;
    
	public ProfilePicService() {
		super();
	}

    
    public String saveImage(MultipartFile file, String filename) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = String.format("%s.jpg",  filename);        
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }
    
    
    public String getTempfilename() {
    	String result = String.format("tmp-%d", new Random().nextInt(1000000, 9999999));
    	return result;
    }
    
    
	public void renameImage(String tempFile, User user) {		
		String sourceFilename = String.format("%s.jpg", tempFile) ;
		String destFilename = String.format("%d.jpg", user.getUserId()) ;
		
		Path sourceFilePath = Paths.get(uploadDir).resolve(sourceFilename);
		Path destFilePath = Paths.get(uploadDir).resolve(destFilename);

        File sourceFile = new File(sourceFilePath.toUri());
        File destFile = new File(destFilePath.toUri());
        
        if (sourceFile.exists()) {
        	if (destFile.exists()) {
        		destFile.delete();	
        	}
        	sourceFile.renameTo(destFile);
        }        
        return; 
        
	}    	
	
	public void deleteTempImage(User user) {		
		String sourceFilename = String.format("u%d.jpg", user.getUserId()) ;
		Path sourceFilePath = Paths.get(uploadDir).resolve(sourceFilename);
		File sourceFile = new File(sourceFilePath.toUri());
        if (sourceFile.exists()) {
        	sourceFile.delete();
        }        
        return; 
        
	}    	
	
	
    
	public String getImageURL(int id) {
		String filename = String.format("%d.jpg", id) ;
		String url;
		Path filePath = Paths.get(uploadDir).resolve(filename);

        File f = new File(filePath.toUri());
        
        if (f.isFile()) {
        	url = String.format("./profilePics/%d.jpg",  id);        	
        }
        else {
        	url =  "./images/missingProfile.jpg";
        }

		return url;
	}    
    
	public Resource getResource(String filename) throws MalformedURLException {	
		Path filePath = Paths.get(uploadDir).resolve(filename);
		return new UrlResource(filePath.toUri());
	}

    
    
    
}
