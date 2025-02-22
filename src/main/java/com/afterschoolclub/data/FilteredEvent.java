package com.afterschoolclub.data;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FilteredEvent {
	

	Event event;
	User user;
	Student student;
	
	private boolean isAvailable = true;
	private boolean isAttending = false;
	private boolean isHidden = false;
	private boolean missed = false;
	private boolean attended = false;

	
	public FilteredEvent(Event event, User user, Student student, Filter filter) {
		super();
		this.event = event;
		this.user = user;
		this.student = student;
		
		
		if (user.isAdmin() ) {		    
			Resource resource = user.getAdministratorObject().getResourceObject();
			
			if (event.usesResource(resource)) {
				setAttending(true);						
			}
			else {
				if (filter.isOnlyMineFilter()) {
					setHidden(true);
				}
					
			}	
			switch (filter.getAdminFilter()) {				
				case FULLYBOOKED:
					if (!event.isFullyBooked()) {
						setHidden(true);
					}
						
					break;
				case NOATTENDEES:
					if (event.getNumberAttendees() != 0) {
						setHidden(true);
					}					
					break;
				default: 
					break;
			}			
		} else
		{			
			if (event.endInPast()) {
				if (student != null && event.didAttend(student)) {
					setAttended(true);
					if (!filter.isDisplayingAttended()) {
						setHidden(true);
					}							
				} 
				else if (student != null && event.registered(student)) {
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
				if (student != null && student.isAttendingEvent(event)) {
					setAttending(true);
					if (!filter.isDisplayingAttending()) {
						setHidden(true);
					}
				}
				else if (event.endInPast() || (student != null && !event.canAttend(student))) { 
					setAvailable(false);	
					if (!filter.isDisplayingAvailable()) {
						setHidden(true);
					}
				}
				else if (!filter.isDisplayingAvailable()) {
					setHidden(true);						
				}
			}								 										
		}				
	}

	
	public String getFilterClass() {
		String result = "Available";
		if (isHidden)
			result = "HiddenEvent";
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
