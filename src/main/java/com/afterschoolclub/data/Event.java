package com.afterschoolclub.data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Event {


	@Id
	private int eventId;
	private String title;
	private String description;
	private String location;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private int basePrice;
	private int maxAttendees;
	private boolean yearRCanAttend;
	private boolean yearOneCanAttend;
	private boolean yearTwoCanAttend;
	private boolean yearThreeCanAttend;
	private boolean yearFourCanAttend;
	private boolean yearFiveCanAttend;
	private boolean yearSixCanAttend;
	@MappedCollection(idColumn = "event_id")
	private Set<EventResource> eventResources = new HashSet<>();
	@MappedCollection(idColumn = "event_id")
	private Set<Attendee> attendees = new HashSet<>();
	
	public Event() {
		super();	
	}
	
	/**
	 * @param title
	 * @param description
	 * @param location
	 * @param startDateTime
	 * @param endDateTime
	 * @param basePrice
	 * @param maxAttendees
	 * @param yearRCanAttend
	 * @param yearOneCanAttend
	 * @param yearTwoCanAttend
	 * @param yearThreeCanAttend
	 * @param yearFourCanAttend
	 * @param yearFiveCanAttend
	 * @param yearSixCanAttend
	 */
	public Event(String title, String description, String location, LocalDateTime startDateTime,
			LocalDateTime endDateTime, int basePrice, int maxAttendees, boolean yearRCanAttend,
			boolean yearOneCanAttend, boolean yearTwoCanAttend, boolean yearThreeCanAttend, boolean yearFourCanAttend,
			boolean yearFiveCanAttend, boolean yearSixCanAttend) {
		super();
		this.title = title;
		this.description = description;
		this.location = location;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.basePrice = basePrice;
		this.maxAttendees = maxAttendees;
		this.yearRCanAttend = yearRCanAttend;
		this.yearOneCanAttend = yearOneCanAttend;
		this.yearTwoCanAttend = yearTwoCanAttend;
		this.yearThreeCanAttend = yearThreeCanAttend;
		this.yearFourCanAttend = yearFourCanAttend;
		this.yearFiveCanAttend = yearFiveCanAttend;
		this.yearSixCanAttend = yearSixCanAttend;
	}
	
	/**
	 *
	 */
	public Event(Event e) {
		super();
		this.title = e.title;
		this.description = e.description;
		this.location = e.location;
		this.startDateTime = e.startDateTime;
		this.endDateTime = e.endDateTime;
		this.basePrice = e.basePrice;
		this.maxAttendees = e.maxAttendees;
		this.yearRCanAttend = e.yearRCanAttend;
		this.yearOneCanAttend = e.yearOneCanAttend;
		this.yearTwoCanAttend = e.yearTwoCanAttend;
		this.yearThreeCanAttend = e.yearThreeCanAttend;
		this.yearFourCanAttend = e.yearFourCanAttend;
		this.yearFiveCanAttend = e.yearFiveCanAttend;
		this.yearSixCanAttend = e.yearSixCanAttend;
		for (EventResource er : e.eventResources) {
			this.eventResources.add(new EventResource(er));
		}
		// Don't copy attendees or incidents
		
		//TODO will need to copy Event_Menu when get round to that.
		
		
	}

	public Attendee getAttendee(int attendeeId)
	{
		Attendee result = null;
		
		Iterator<Attendee> attendeeIterator = attendees.iterator();
				while (result == null && attendeeIterator.hasNext()) {
			Attendee compare = attendeeIterator.next();
			if (compare.getAttendeeId() == attendeeId)
				result = compare;
		}
				return result;
	}
	
	public List<Attendee> getSortedAttendees()
	{
	
		Comparator<Attendee> comparator = new Comparator<Attendee>(){
 
		    @Override
		    public int compare(final Attendee o1, final Attendee o2){
		    	String s1 = o1.getStudent().getSurname().toLowerCase().concat(o1.getStudent().getFirstName().toLowerCase());
		    	String s2 = o2.getStudent().getSurname().toLowerCase().concat(o2.getStudent().getFirstName().toLowerCase());
		    	return s1.compareTo(s2);
		    }
		};
				
		List<Attendee> sortedAttendees = new ArrayList<Attendee>();
		sortedAttendees.addAll(attendees);
		Collections.sort(sortedAttendees, comparator);
		return sortedAttendees;
	}
	
}
