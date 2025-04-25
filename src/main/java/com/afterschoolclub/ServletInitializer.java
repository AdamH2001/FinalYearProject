package com.afterschoolclub;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ServletInitializer for AfterSchoolClub
 */
public class ServletInitializer extends SpringBootServletInitializer {

	/**
	 * Return the application class for the main application
	 */	
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AfterSchoolClubApplication.class);
	}

}
