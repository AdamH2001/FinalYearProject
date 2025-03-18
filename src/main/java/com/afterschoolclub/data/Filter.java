package com.afterschoolclub.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class Filter {
	
	
	public enum AdminFilter {
		ALL, 
		FULLYBOOKED, 
		NOATTENDEES, 
		INSUFFICIENTRESOURCES;
	
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
		    	case 4: 
		    		result = AdminFilter.INSUFFICIENTRESOURCES;
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
		    	case INSUFFICIENTRESOURCES: 
		    		result = 4;
		    		break;		    		
		    	default:
		    		result = 1;
		    		break;
	    	}
	        return result;
	    }	    
			
	}		
    private boolean onlyMineFilter = false;    
    private AdminFilter adminFilter = AdminFilter.ALL;
    
    private boolean displayingAttending = true;
    private boolean displayingAvailable = true;
    private boolean displayingUnavailable = true;
    private boolean displayingMissed = true;
    private boolean displayingAttended = true;
    
    public int getAdminFilterAsInt() {
    	return AdminFilter.valueOf(adminFilter);
    }    
}
