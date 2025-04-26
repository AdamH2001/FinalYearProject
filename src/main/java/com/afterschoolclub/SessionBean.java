package com.afterschoolclub;

import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.User;
import com.afterschoolclub.service.PolicyService;
import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.Filter;
import com.afterschoolclub.data.FilteredSession;
import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.Policy;
import com.afterschoolclub.data.Session;
import com.afterschoolclub.data.State;

import lombok.Getter;
import lombok.Setter;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;


/**
 *  Class that is used to main session state between client and server.
 *  We use this to also share session state cross the different controller 
 *  classes. 
 *  
 */


@Configuration
@Getter
@Setter
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)

public class SessionBean {

    /**
     *  Contact email address from properties
     */
    @Value("${asc.contactEmail}")
    private String contactEmail;	

    /**
     *  Contact telephone number from properties
     */
    @Value("${asc.contactTel}")
    private String contactTel;	

    /**
     * Contact homepage for properties
     */
    @Value("${asc.homepage}")
    private String homePage;	
    
    
	/**
	 * The service policy service class
	 */
	@Autowired
    private PolicyService policyService;
	
	/**
	 * Policy properties class
	 */
	@Autowired
    private PolicyProperties policyProperties;	
	
    /**
     *  Stores the logged on user as session data
     */
    private User loggedOnUser = null;
    
    
    /**
     * Stores the selected student by a parent as session data
     */
    private Student selectedStudent = null;
    
    
    /**
     * Sores the filter session as session state 
     */
    private Filter filter = new Filter();
    
    /**
     * Determines whether current screen shold be treated as a dialog
     * and unable to navigate away form it 
     */
    private boolean inDialogue = false;
    
    
    /**
     * Indicates whether showing finance summary or not
     */
    
    private boolean financeSummaryVisible = false;
    
    /**
     * Stores which tab the user last selected on user accounts page
     */
    private boolean newAccountsTab = false;
    
    /**
     * Stores which tab the user selected on student accounts page 
     */
    private boolean newStudentsTab = false;

    
    
    /**
     * store what url the user should return to when hitting back
     */
    private String returnUrl = "./";
    
    
    /**
     * store the previous return url 
     */
    private String previousReturnUrl = "./";
    
    /**
     * list of message to inform the user
     */
    private List<String> flashMessages = null;
    
   
    /**
     * date that should use to start showing the timetable
     */
    private LocalDate timetableStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);;
    /**
     * date should use for displaying transaction status
     */
    private LocalDate transactionStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
    /**
     * date should use for finance information 
     */
    private LocalDate financeStartDate = null;
    
    
    /**
     * stores whether we are shoowing calendar view or not
     */
    private boolean calendarView = false;	
	
    /**
     * return new SessionBean with initial values.
     */
    public SessionBean() {
        this.reset();
    }
    
    /**
     * @return the loggedOnParent if is a parent otherwise return null
     */
    public Parent getLoggedOnParent() {
    	Parent parent = null;
    	if (loggedOnUser != null) {
    		parent = loggedOnUser.getParent();
    	}
        return parent;
    }
    
    
    /**
     * Set the logged on user
     * @param user - new user to set the logged on user to
     */
    public void setLoggedOnUser(User user) {
    	loggedOnUser = user;
    	if (user != null && selectedStudent != null) {
    		int studentId = selectedStudent.getStudentId();
    		selectedStudent = loggedOnUser.getParent().getStudentFromId(studentId);
    	}
        return;
    }
    
    /**
     * Refresh the logged on user with details from the database
     */
    public void refreshLoggedOnUser() {
    	if (loggedOnUser != null) {
    		loggedOnUser = User.findById(loggedOnUser.getUserId());
    		if (selectedStudent != null) {
        		selectedStudent = loggedOnUser.getParent().getStudentFromId(selectedStudent.getStudentId());    			
    		}
    	}
        return;
    }    
    
    
    /**
     * @return true if loggedOn otherwsie return false
     */
    public boolean isLoggedOn() {
    	return getLoggedOnUser() != null;
    }    
    
    
    
    /**
     * @return true if parent is loggedOn otherwise return false
     */
    public boolean isParentLoggedOn() {
    	return isLoggedOn() && getLoggedOnUser().isParent() ;
    }   
    
    /**
     * @return true if administrator is loggedOn otherwise return false
     */
    public boolean isAdminLoggedOn() {
    	return isLoggedOn() && getLoggedOnUser().isAdmin() ;
    }       
    
    /**
     *  reset the value for this session i.e. log out the user
     */
    public void reset() {
        loggedOnUser = null;
        filter = new Filter();
        selectedStudent = null;
        financeSummaryVisible = false;
        timetableStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);;    
        transactionStartDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);        
        inDialogue = false;
        financeSummaryVisible = false;        
        newAccountsTab = false;        
        newStudentsTab = false;
        returnUrl = "./";        
        previousReturnUrl = "./";        
        flashMessages = null;                
    }
    

    
    /**
     * @return the selectedSutdent
     */
    public Student getSelectedStudent() {
    	
    	if (selectedStudent == null && isParentLoggedOn()) {
    		selectedStudent = getLoggedOnParent().getFirstStudent(); 
    	}
    	return selectedStudent;
    }
    
    /**
     * @return a list of all the clubs
     */
    public List<Club> findAllClubs() {
    	return Club.findAll();
    }
    
    /**
     * @param set in dialogue state
     */
    public void setInDialogue(boolean filterValue) {
    	inDialogue = filterValue;
    	calendarView = false;
    }
    

    
    /**
     * set the return url to newReturn
     * @param newReturn - new return url
     */
    
    public void setReturnUrl(String newReturn) {
    	if (!returnUrl.equals(newReturn)) {
    		previousReturnUrl = returnUrl;
    		returnUrl = newReturn;
    	}
    	return;
    }

    /** 
     * @return the url user should return to
     */
    public String getRedirectUrl() {
    	return String.format("redirect:%s", this.getReturnUrl());
    }
    
    
    /**
     * @return the date should use in the finace view
     */
    public LocalDate getDefaultFinanceStartDate() {
		LocalDate today = LocalDate.now();
		LocalDate defaultDate = LocalDate.of(today.getYear(), 9, 1);
		if (defaultDate.isAfter(today)) {
			defaultDate = defaultDate.minusYears(1);
		}    					
    	return defaultDate;
    }
    
    /**
     * @return the total amount of revenue for selected academic year
     */
    public int getTotalRevenue() {
    	LocalDate startDate = getDefaultFinanceStartDate();
    	LocalDate endDate = startDate.plusYears(1);
    	startDate = startDate.minusDays(1);    	
    	return ParentalTransaction.getTotalRevenueBetween(startDate, endDate);
    }
    
    /**
     * @return the date shold show for the finance report
     */
    public LocalDate getFinanceStartDate() {
    	if (financeStartDate == null) {
    		financeStartDate = getDefaultFinanceStartDate();    		    			
    	}
    	return financeStartDate;
    }
    
    /**
     * @return the end date for the finance rerpot
     */
    public LocalDate getFinanceEndDate() {
    	LocalDate financeEndDate = this.getFinanceStartDate().plusYears(1);
    	financeEndDate = financeEndDate.minusDays(1);    	
    	return financeEndDate;
    }    

    /**
     * @param monetaryAmount to be formatted
     * @return a monetary amount formatted according to UK standards
     */
    public String formatMoney(int monetaryAmount) {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);
		return n.format(monetaryAmount/100.0);
    }
    
	
	/**
	 * @return the total cash balance for AfterSchoolCLub
	 */
	public int getTotalCashBalance() {
		return ParentalTransaction.getTotalCashBalance();
	}

	/**
	 * @return the total voucher balance for AfterSchool Club
	 */
	public int getTotalVoucherBalance() {
		return ParentalTransaction.getTotalVoucherBalance();
	}
	
	/**
	 * @return the total amount owed to AfterSchool CLub by parents with overdrafts
	 */
	public int getTotalOwed() {
		return ParentalTransaction.getTotalOwed();
	}		
	
	/**
	 * @return The total amount of cash credit 
	 */
	public int getTotalCashCredit() {
		return ParentalTransaction.getTotalCashCredit();
	}		

	/**
	 * @return the PolicyService
	 */
	public PolicyService getPolicyService() {
		return policyService;
	}		
	
	
	/**
	 * @return all the message we need to communicate to the user
	 */
	public List<String> getFlashMessages() {
		List<String> result = flashMessages;
		flashMessages = null;
		return result;
	}		
	
	/**
	 * @param message to be formatted
	 * @return a new message converted to lower case and stripped of punctuation
	 */
	public String formatMessage(String message) {
		String formattedMessage = message.trim().toLowerCase();
		if (formattedMessage.length() > 0) {
			formattedMessage = formattedMessage.substring(0,1).toUpperCase().concat(formattedMessage.substring(1));
		    if (formattedMessage.charAt(formattedMessage.length() - 1) == '.') {
		    	formattedMessage = formattedMessage.substring(0, formattedMessage.length() - 1);
		    }			
			
		}
		return formattedMessage;
	}

		
	/**
	 * @param message to be communicated to user
	 */
	public void  setFlashMessage(String message) {
		flashMessages = new ArrayList<String>();

		flashMessages.add(formatMessage(message));
		return ;
	}
	
	/**
	 * @param message to be shown to user without any formatting treatment
	 */
	public void  setFlashMessagePreserve(String message) {
		flashMessages = new ArrayList<String>();

		flashMessages.add(message);
		return ;
	}
				
	
	
	/**
	 * @return true if have messages otherwise return false
	 */
	public boolean hasMessage() {
		return flashMessages != null;
	}			
	
	/**
	 * @return list of all the polices we should have
	 */
	public List<Policy> getPolicies() {
		Policy.policyService = policyService;
		List<Policy> allPolicies = policyProperties.policies(); 
		return allPolicies;
	}

	/**
	 * @return number of new user requests that are outstadning to be validated 
	 */
	public int numNewAccountRequests() {
		return User.findParentByStateVerified(State.ACTIVE, false).size();
	}

	/**
	 * @return total number of approved active users
	 */
	public int numUsers() {		
		return User.findParentByStateVerified(State.ACTIVE, true).size();
	}
	
	/**
	 * @return total number of active approved students
	 */
	public int numStudents() {
		return Student.findByStateVerified(State.ACTIVE, true).size();
	}
	
	/**
	 * @return number student accounts outstanding to be validated
	 */
	public int numNewStudentRequests() {
		return Student.findByStateVerified(State.ACTIVE, false).size();
	}
	
	
    /**
     * @param d date to be formatted
     * @return in format dd/MM/YYYY
     */
    public String formatDate(LocalDate d) {
		return d.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));		

    }	
	
    /**
     * @param dt date time to be formatted
     * @return formatted dd/MM/YYY HH:mm
     */
    public String formatDateTime(LocalDateTime dt) {
		return dt.format(DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm"));		

    }
	
	

	//// The following methods are a set off methods that implement logic that would be too tricky to implement in Thymeleaf
    
    
    

	
	/**
	 * Method use when displaying students against sessions to return the correct CSS class
	 * for the checkbox
	 * 
	 * @param currentStudent - the student that filter is required for
	 * @param session - the session against which need to determine if eligible for 
	 * @return the css class should be applied to the student
	 */
    
    
	public String getStudentCheckContainerClass(Student currentStudent, Session session)
	{
		FilteredSession filterSession = new FilteredSession(session, getLoggedOnUser(), currentStudent, getFilter());
		return filterSession.getFilterClass();			
	}	
	
	
	
	/**
	 * Method use when displaying students against sessions to return the correct CSS class
	 * for the tab
	 * 
	 * @param currentStudent - the student that filter is required for
	 * @param session - the session against which need to determine if eligible for 
	 * @return the css class should be applied to the student
	 */
	
	public String getStudentClass(Student currentStudent, Session session, boolean editing, boolean viewOnly)
	{
		String result = "";

		if (editing) {
			if (session.registered(currentStudent)) {
				if (selectedStudent.equals(currentStudent)) {
					result = "active ";
				}
			}
			else {
				result = "disabled ";
			}			
		}
		else {
			if ((session.registered(currentStudent) || !session.canAttend(currentStudent)) && !viewOnly) {
				result ="disabled ";
			}
			else if (this.getSelectedStudent().equals(currentStudent)) {
				result = "active ";
			}			
		}
		
		return result;
			
	}

	
	
	/**
	 * Returns whether menu item should be checked or not against refreshment 
	 * 
	 * @param menuGroup primary key for menu group
	 * @param menuOptionId primary key for menu option
	 * @param student - the primary key for the student
	 * @param session - the primary key for the session

	 * @return true is should be checked otherwise return false 
	 */
	public boolean checkedOption(MenuGroup menuGroup, int menuOptionId, Student student, Session session)
	{
		boolean result;
		MenuOption menuOption = menuGroup.getChosenMenuOption(student, session);
		if (menuOption != null) {
			result = menuOption.getMenuOptionId() == menuOptionId;
		}
		else {
			result = menuOptionId==0;
		}		
		return result;		
	}
	
	
	
	/**
	 * Return the option text for a given menu group
	 * @param menuGroup - primary key for the menu group
	 * @param student - primary key for the student
	 * @param session - primary key for the session 
	 * @return the name of the menu option
	 */
	
	public String getOptionText(MenuGroup menuGroup, Student student, Session session)
	{
		MenuOption menuOption = menuGroup.getChosenMenuOption(student, session);		
		
		String optionText = "None";
		if (menuOption != null) {
			optionText = menuOption.getName();
		}
		return optionText;
	}	
	
    
}

