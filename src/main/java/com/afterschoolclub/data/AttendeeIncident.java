package com.afterschoolclub.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AttendeeIncident {
	@Id
	private int attendeeIncidentId;		
	private AggregateReference<Attendee, Integer> attendeeId;	
	private AggregateReference<Attendee, Integer> incidentId;		
	private String summary;
	
	/**
	 * @param eventId
	 * @param summary
	 */
	public AttendeeIncident(AggregateReference<Attendee, Integer> attendeeId, String summary) {
		super();		
		this.attendeeId = attendeeId;
		this.summary = summary;
	}
	
	
	public Student getStudent(Event event) {
		
		Student student = event.getAttendee(this.getAttendeeId().getId().intValue()).getStudent();
		return student;
		
	}	
	
}
