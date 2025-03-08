package com.afterschoolclub;

import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.User;
import com.afterschoolclub.data.Filter;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;


@Configuration
@Getter
@Setter
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)

public class SessionBean {

	
    private String name = null;

    private User loggedOnUser = null;
    private Student selectedStudent = null;
    
    private Filter filter = new Filter();
    
    private boolean inDialogue = false;

    
    private LocalDate timetableStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);;
    private LocalDate transactionStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
    		
	
    public SessionBean() {
        this.reset();
    }
    
    public Parent getLoggedOnParent() {
    	Parent parent = null;
    	if (loggedOnUser != null) {
    		parent = loggedOnUser.getParent();
    	}
        return parent;
    }
    
    public boolean isLoggedOn() {
    	return getLoggedOnUser() != null;
    }    
    
    public boolean isParentLoggedOn() {
    	return isLoggedOn() && getLoggedOnUser().isParent() ;
    }   
    
    public boolean isAdminLoggedOn() {
    	return isLoggedOn() && getLoggedOnUser().isAdmin() ;
    }       
    
    public void reset() {
        loggedOnUser = null;
        filter = new Filter();
        timetableStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);;    
        transactionStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
    }
    

    
    public Student getSelectedStudent() {
    	
    	if (selectedStudent == null && isParentLoggedOn()) {
    		selectedStudent = getLoggedOnParent().getFirstStudent(); 
    	}
    	return selectedStudent;
    }
}

