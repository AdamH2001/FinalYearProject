package com.afterschoolclub;

import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.User;

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
	
	public enum AdminFilter {
		ALL, 
		FULLYBOOKED, 
		NOATTENDEES;
	
	    public static AdminFilter valueOf(int adminFilter) {
	    	AdminFilter result;	    	
	    	switch (adminFilter) {
		    	case 1: 
		    		result = AdminFilter.ALL;
		    		break;
		    	case 2: 
		    		result = AdminFilter.FULLYBOOKED;
	    			break;
		    	case 3: 
		    		result = AdminFilter.NOATTENDEES;
		    		break;
		    	default:
		    		result = AdminFilter.ALL;
		    		break;
	    	}
	        return result;
	    }
	    
	    public static int valueOf(AdminFilter adminFilter) {
	    	int result;	    	
	    	switch (adminFilter) {
		    	case ALL: 
		    		result = 1;
		    		break;
		    	case FULLYBOOKED: 
		    		result = 2;
	    			break;
		    	case NOATTENDEES: 
		    		result = 3;
		    		break;
		    	default:
		    		result = 1;
		    		break;
	    	}
	        return result;
	    }	    
			
	}	
	
    private String name = null;

    private User loggedOnUser = null;
    private Student selectedStudent = null;
    
    private boolean onlyMineFilter = false;
    private AdminFilter adminFilter = AdminFilter.ALL;
    
    private boolean displayingAttending = true;
    private boolean displayingAvailable = true;
    private boolean displayingUnavailable = true;
    private boolean displayingMissed = true;
    private boolean displayingAttended = true;
    
    private boolean inDialogue = false;

    
    private LocalDate timetableStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);;
    private LocalDate transactionStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
    		
	
    public SessionBean() {
        System.out.println("SessionScopeBean Constructor Called");
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
        onlyMineFilter = false;
        adminFilter = AdminFilter.ALL;  
        displayingAttending = true;
        displayingAvailable = true;
        displayingUnavailable = true;
        displayingMissed = true;
        displayingAttended = true;   
        timetableStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);;    
        transactionStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
    }
    
    public int getAdminFilterAsInt() {
    	return AdminFilter.valueOf(adminFilter);
    }
    
    public Student getSelectedStudent() {
    	
    	if (selectedStudent == null && isParentLoggedOn()) {
    		selectedStudent = getLoggedOnParent().getFirstStudent(); 
    	}
    	return selectedStudent;
    }
}

