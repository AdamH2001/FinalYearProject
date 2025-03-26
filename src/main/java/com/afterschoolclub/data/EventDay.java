package com.afterschoolclub.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EventDay {
	private LocalDate date;
	private ArrayList<FilteredEvent> filteredEvents = new ArrayList<FilteredEvent>();
	private boolean holiday;
	
	/**
	 * @param date
	 */
	public EventDay(LocalDate date, List<Holiday> allHolidays, List<Event> events, User user, Student student, Filter filter ) {
		super();
		this.date = date;
		this.addAllEventsForDay(events, user, student, filter);
		this.holiday = Holiday.isDateInHolidays(date, allHolidays);
		
	}
	
	public void addAllEventsForDay(List<Event> allEvents, User user, Student student, Filter filter) {
		for (Event event : allEvents) {
			if (event.getStartDateTime().toLocalDate().equals(this.date)) {
				filteredEvents.add(new FilteredEvent(event, user, student, filter));				
			}
		}
	}
	
	
}
