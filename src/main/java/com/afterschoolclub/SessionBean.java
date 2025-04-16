package com.afterschoolclub;

import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.User;
import com.afterschoolclub.service.PolicyService;
import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.Filter;
import com.afterschoolclub.data.Policy;

import lombok.Getter;
import lombok.Setter;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;


@Configuration
@Getter
@Setter
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)

public class SessionBean {

	@Autowired
    private PolicyService policyService;
	
	@Autowired
    private PolicyProperties policyProperties;	
	
    private String name = null;

    private User loggedOnUser = null;
    private Student selectedStudent = null;
    
    private Filter filter = new Filter();
    
    private boolean inDialogue = false;
    private boolean financeSummaryVisible = false;
    
    private String returnUrl = "./";
    private String previousReturnUrl = "./";
    
    private List<String> flashMessages = null;
    
   
    private LocalDate timetableStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);;
    private LocalDate transactionStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
    private LocalDate financeStartDate = null;
    
    private boolean calendarView = false;	
	
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
    
    public List<Club> findAllClubs() {
    	return Club.findAll();
    }
    
    public void setInDialogue(boolean filterValue) {
    	inDialogue = filterValue;
    	calendarView = false;
    }
    
    public void setReturnCalendar()  {
    	this.setReturnUrl("./calendar");
    	return;
    }
    
    public void setReturnClubs()  {
    	this.setReturnUrl("./viewClubs");
    	return;
    }

    public void setReturnIncidents()  {
    	this.setReturnUrl("./viewIncidents");
    	return;
    }
    
    public void setReturnParentFinances()  {
    	this.setReturnUrl("./parentFinances");
    	return;
    }    
    
    public void setReturnClubRevenue()  {
    	this.setReturnUrl("./clubRevenue");
    	return;
    }        

    public void setReturnTransactions()  {
    	this.setReturnUrl("./viewTransactions");
    	return;
    }    
    
    public void setReturnUrl(String newReturn) {
    	if (!returnUrl.equals(newReturn)) {
    		previousReturnUrl = returnUrl;
    		returnUrl = newReturn;
    	}
    	return;
    }

    public String getRedirectUrl() {
    	return String.format("redirect:%s", this.getReturnUrl());
    }
    
    
    public LocalDate getDefaultFinanceStartDate() {
		LocalDate today = LocalDate.now();
		LocalDate defaultDate = LocalDate.of(today.getYear(), 9, 1);
		if (defaultDate.isAfter(today)) {
			defaultDate = defaultDate.minusYears(1);
		}    					
    	return defaultDate;
    }
    
    public int getTotalRevenue() {
    	LocalDate startDate = getDefaultFinanceStartDate();
    	LocalDate endDate = startDate.plusYears(1);
    	startDate = startDate.minusDays(1);    	
    	return ParentalTransaction.getTotalRevenueBetween(startDate, endDate);
    }
    
    public LocalDate getFinanceStartDate() {
    	if (financeStartDate == null) {
    		financeStartDate = getDefaultFinanceStartDate();    		    			
    	}
    	return financeStartDate;
    }
    
    public LocalDate getFinanceEndDate() {
    	LocalDate financeEndDate = this.getFinanceStartDate().plusYears(1);
    	financeEndDate = financeEndDate.minusDays(1);    	
    	return financeEndDate;
    }    

    public String formatMoney(int monetaryAmount) {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);
		return n.format(monetaryAmount/100.0);
    }
    
	
	public int getTotalCashBalance() {
		return ParentalTransaction.getTotalCashBalance();
	}

	public int getTotalVoucherBalance() {
		return ParentalTransaction.getTotalVoucherBalance();
	}
	
	public int getTotalOwed() {
		return ParentalTransaction.getTotalOwed();
	}		
	
	public int getTotalCashCredit() {
		return ParentalTransaction.getTotalCashCredit();
	}		

	public PolicyService getPolicyService() {
		return policyService;
	}		
	
	
	public List<String> getFlashMessages() {
		List<String> result = flashMessages;
		flashMessages = null;
		return result;
	}		
	
	public void  setFlashMessage(String message) {
		flashMessages = new ArrayList<String>();
		flashMessages.add(message);
		return ;
	}
	
			
	
	
	public boolean hasMessage() {
		return flashMessages != null;
	}			
	
	public List<Policy> getPolicies() {
		Policy.policyService = policyService;
		List<Policy> allPolicies = policyProperties.policies(); 
		return allPolicies;
	}
}

