package com.afterschoolclub.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Class that encapaultes all the information to show on a calendar view for a specific day on the calendar
 */
@Getter
@Setter
@ToString
public class SessionDay {
	/**
	 * Date for the timeline e.g. date of a specic day on calendar
	 */
	private LocalDate date;
	
	/**
	 * list of DilteredSession 
	 */
	private ArrayList<FilteredSession> filteredSessions = new ArrayList<FilteredSession>();
	
	
	/**
	 * true of it is a holiday otherwise false
	 */
	private boolean holiday;
	

	/**
	 * Constructor passing in all the required info
	 * 
	 * @param date
	 * @param allHolidays
	 * @param sessions
	 * @param user
	 * @param student
	 * @param filter
	 */
	public SessionDay(LocalDate date, List<Holiday> allHolidays, List<Session> sessions, User user, Student student, Filter filter ) {
		super();
		this.date = date;
		this.addAllSessionsForDay(sessions, user, student, filter);
		this.holiday = Holiday.isDateInHolidays(date, allHolidays);
		
	}
	
	/**
	 * Private method to create the internal structure required for the sessions
	 * @param allSessions
	 * @param user
	 * @param student
	 * @param filter
	 */
	private void addAllSessionsForDay(List<Session> allSessions, User user, Student student, Filter filter) {
		for (Session session : allSessions) {
			if (session.getStartDateTime().toLocalDate().equals(this.date)) {
				filteredSessions.add(new FilteredSession(session, user, student, filter));				
			}
		}
	}
	
	
}
