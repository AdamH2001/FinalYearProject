package com.afterschoolclub.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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
	
    @ToString.Exclude	
	@Transient 
	Student student = null;
	
	/**
	 * @param sessionId
	 * @param summary
	 */
	public AttendeeIncident(AggregateReference<Attendee, Integer> attendeeId, String summary) {
		super();		
		this.attendeeId = attendeeId;
		this.summary = summary;
	}
	
	
	public Student getStudent(Session session) {
		
		Student student = session.getAttendee(this.getAttendeeId().getId().intValue()).getStudent();
		return student;
		
	}	
	
	public Student getStudent() {
		if (student == null) {
			student = Student.findByAttendeeId(attendeeId.getId().intValue());
		}
		return student;
	}	
	
}
