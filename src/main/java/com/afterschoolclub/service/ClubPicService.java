package com.afterschoolclub.service;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.afterschoolclub.data.Club;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


/**
 * Service to manage the pictures for clubs
 */
@Service
public class ClubPicService {

    /**
     * Upload directory for club pictures
     */
    @Value("${asc.file.clubPics}")
    private String uploadDir;
    
	/**
	 * Default constructor
	 */
	public ClubPicService() {
		super();
	}

    /**
     * Save the image identified by file to the filename
     * @param file - mage to be saved
     * @param filename - the filename to save it as
     * @return the path of the file
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
	 * Reutrns a list of images that can be used on promotional pages e.g. homepage
	 * @return a list of urls to all the images to be used as club images. 
	 */
	public List<String> getAllURLs() {
	    List<String> urlList = new ArrayList<>();
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(uploadDir))) {
	        for (Path path : stream) {
	            if (!Files.isDirectory(path)) {
	            	urlList.add(String.format("./clubPics/%s", path.getFileName().toString()));
	            }
	        }
	    }
	    catch (IOException e)
	    {
	    	// Will return an empty list
	    }
	    return urlList;
	}    
	
    
	/**
	 * Return the url for the clbu identified by the id
	 * @param id - primary key for the club
	 * @return url for image
	 */
	public String getImageURL(int id) {
		String filename = String.format("%d.jpg", id) ;
		String url;
		Path filePath = Paths.get(uploadDir).resolve(filename);

        File f = new File(filePath.toUri());
        
        if (f.isFile()) {
        	url = String.format("./clubPics/%d.jpg",  id);        	
        }
        else {
        	url =  "./images/missingClub.png";
        }

		return url;
	}    
	
	/**
	 * Rename the temporary imag to the correct image for the user
	 * @param tempFile tempgilename
	 * @param user user to assign the image to 
	 */	
	public void renameImage(String tempFile, Club club) {		
		String sourceFilename = String.format("%s.jpg", tempFile) ;
		String destFilename = String.format("%d.jpg", club.getClubId()) ;
		
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
     * @return a temporary filename for upload
     */
    public String getTempfilename() {
    	String result = String.format("tmp-%d", new Random().nextInt(1000000, 9999999));
    	return result;
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
    
}
