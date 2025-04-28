package com.afterschoolclub.data;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Class that encapsultes all the logic for filtering a session based on filter setting, user, selected student
 */
@Getter
@Setter
@ToString
public class FilteredSession {
	

	/**
	 * Session to determine how to filter
	 */
	Session session;
	/**
	 * LoggedOnUser
	 */
	User user;
	/**
	 * Selected Student
	 */
	Student student;
	
	/**
	 * Filter settings 
	 */
	private boolean isAvailable = true;
	private boolean isAttending = false;	
	private boolean isHidden = false;
	private boolean missed = false;
	private boolean attended = false;

	
	/**
	 * Constructor for a FilteredSession
	 * @param session - Session 
	 * @param user - Logged in User
	 * @param student - Student Selected
	 * @param filter - Filter settings
	 */
	public FilteredSession(Session session, User user, Student student, Filter filter) {
		super();
		this.session = session;
		this.user = user;
		this.student = student;
		
		if (filter.getFilterClubId() != 0 && session.getClub().getClubId() != filter.getFilterClubId()) {
			setHidden(true);
		} 
		else if (user != null && user.isAdmin() ) {		    
			Resource resource = user.getResourceObject();
			
			if (session.usesResource(resource)) {
				setAttending(true);						
			}
			else {
				if (filter.isOnlyMineFilter()) {
					setHidden(true);
				}
					
			}	
			switch (filter.getAdminFilter()) {				
				case FULLYBOOKED:
					if (!session.isFullyBooked()) {
						setHidden(true);
					}
						
					break;
				case NOATTENDEES:
					if (session.getNumberAttendees() != 0) {
						setHidden(true);
					}					
					break;
				case INSUFFICIENTRESOURCES:
					if (session.hasSufficientResources()) {
						setHidden(true);
					}					
					break;					
				default: 
					break;
			}			
		} 
		else if (user != null)
		{		
			if (session.endInPast()) {
				if (student != null && session.didAttend(student)) {
					setAttended(true);
					if (!filter.isDisplayingAttended()) {
						setHidden(true);
					}							
				} 
				else if (student != null && session.registered(student)) {
					setMissed(true);
					if (!filter.isDisplayingMissed()) {
						setHidden(true);
					}						
				}
				else {
					setAvailable(false);
					if (!filter.isDisplayingUnavailable()) {
						setHidden(true);
					}
				}
			} 
			else {
				if (student != null && student.isAttendingSession(session)) {
					setAttending(true);
					if (!filter.isDisplayingAttending()) {
						setHidden(true);
					}
				}
				else if (student != null && !session.canAttend(student)) { 
					setAvailable(false);	
					if (!filter.isDisplayingUnavailable()) {
						setHidden(true);
					}
				}
				else if (!filter.isDisplayingAvailable()) {
					setHidden(true);						
				}
			}								 										
		}				
	}

	
	/**
	 * Return the filter Class for the session
	 * @return String
	 */
	public String getFilterClass() {
		String result = "Available";
		if (isHidden)
			result = "HiddenSession";
		else if (!isAvailable)
			result = "NotAvailable";
		else if (isAttending)
			result = "Attending";
		else if (missed)
			result = "Missed";
		else if (attended)
			result = "Attended";		
		
		return result;
				
	}	
			
}
