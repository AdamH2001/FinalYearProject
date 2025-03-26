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
public class ProfilePicService {

    @Value("${file.profilePics}")
    private String uploadDir;
    
	public ProfilePicService() {
		// TODO Auto-generated constructor stub
	}

    public String saveImage(MultipartFile file, int id) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = String.format("%d.jpg",  id);        
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
