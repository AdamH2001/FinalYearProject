package com.afterschoolclub.data;


import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FilteredEvent {
	

	Event event;
	
	private boolean isAvailable = true;
	private boolean isAttending = false;
	private boolean isHidden = false;
	private boolean missed = false;
	private boolean attended = false;

	
	public FilteredEvent(Event event) {
		super();
		this.event = event;
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
