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

import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.User;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


@Service
public class ClubPicService {

    @Value("${file.clubPics}")
    private String uploadDir;
    
	public ClubPicService() {
		// TODO Auto-generated constructor stub
	}

    public String saveImage(MultipartFile file, String id) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = String.format("%s.jpg",  id);        
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }
    
    
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
    
	public void renameImage(User user, Club club) {		
		String sourceFilename = String.format("u%d.jpg", user.getUserId()) ;
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
	
	
	public Resource getResource(String filename) throws MalformedURLException {	
		Path filePath = Paths.get(uploadDir).resolve(filename);
		return new UrlResource(filePath.toUri());
	}

    
    
    
}
