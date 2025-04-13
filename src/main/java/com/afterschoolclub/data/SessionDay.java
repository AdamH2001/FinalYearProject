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
public class SessionDay {
	private LocalDate date;
	private ArrayList<FilteredSession> filteredSessions = new ArrayList<FilteredSession>();
	private boolean holiday;
	
	/**
	 * @param date
	 */
	public SessionDay(LocalDate date, List<Holiday> allHolidays, List<Session> sessions, User user, Student student, Filter filter ) {
		super();
		this.date = date;
		this.addAllSessionsForDay(sessions, user, student, filter);
		this.holiday = Holiday.isDateInHolidays(date, allHolidays);
		
	}
	
	public void addAllSessionsForDay(List<Session> allSessions, User user, Student student, Filter filter) {
		for (Session session : allSessions) {
			if (session.getStartDateTime().toLocalDate().equals(this.date)) {
				filteredSessions.add(new FilteredSession(session, user, student, filter));				
			}
		}
	}
	
	
}
