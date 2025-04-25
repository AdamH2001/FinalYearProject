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


/**
 * Service to manage the profile pictures for users
 * 
 */
@Service
public class ProfilePicService {

    /**
     * Directory where images will be stored
     */
    @Value("${asc.file.profilePics}")
    private String uploadDir;
    
	/**
	 * Default constructor
	 */
	public ProfilePicService() {
		super();
	}

    
    /**
     * save the image in file to the filename
     * @param file image to save
     * @param filename place where to save it
     * @return the path to the new image
     * @throws IOException
     */
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
    
    
    /**
     * @return a temporary filename for upload
     */
    public String getTempfilename() {
    	String result = String.format("tmp-%d", new Random().nextInt(1000000, 9999999));
    	return result;
    }
    
    
	/**
	 * Rename the temporary imag to the correct image for the user
	 * @param tempFile tempgilename
	 * @param user user to assign the image to 
	 */
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
	
	/**
	 * Deletes any temp image specified by filename  
	 * @param sourceFilename delete this image
	 */
	public void deleteTempImage(String sourceFilename) {				
		Path sourceFilePath = Paths.get(uploadDir).resolve(sourceFilename);
		File sourceFile = new File(sourceFilePath.toUri());
        if (sourceFile.exists()) {
        	sourceFile.delete();
        }        
        return; 
        
	}    	

    
	/**
	 * Returns the image url for a user
	 * @param id - primary key for user require url for
	 * @return the image url for a given user id 
	 */
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
    
	/**
	 * Returns the resource foa given filename 
	 * @param filename
	 * @return the url resource for a given filename 
	 * @throws MalformedURLException
	 */
	public Resource getResource(String filename) throws MalformedURLException {	
		Path filePath = Paths.get(uploadDir).resolve(filename);
		return new UrlResource(filePath.toUri());
	}

    
    
    
}
