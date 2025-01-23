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
	private ArrayList<Event> events = new ArrayList<Event>();
	
	/**
	 * @param date
	 */
	public EventDay(LocalDate date) {
		super();
		this.date = date;
	}
	
	public void addAllEventsForDay(List<Event> allEvents) {
		for (Event event : allEvents) {
			if (event.getStartDateTime().toLocalDate().equals(this.date)) {
				events.add(event);
			}
		}
	}
	
}
