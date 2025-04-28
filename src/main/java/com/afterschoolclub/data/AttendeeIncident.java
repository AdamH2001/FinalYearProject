package com.afterschoolclub.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a AttendeeIncident  
 */

@Getter
@Setter
@ToString
public class AttendeeIncident {
	/**
	 * Primary key to attendeeIncident
	 */
	@Id
	private int attendeeIncidentId;		
	/**
	 * Foreign key to attendee
	 */
	private AggregateReference<Attendee, Integer> attendeeId;	
	/**
	 * Foreign key to incident
	 */
	private AggregateReference<Attendee, Integer> incidentId;		
	/**
	 * Summary from Attende perspective
	 */
	private String summary;
	
    /**
     * Cached student to preent retieving from repository all the time
     */
    @ToString.Exclude	
	@Transient 
	Student student = null;
	
	/**
	 * Constructor 
	 * @param attendeeId - primary key for Attendee
	 * @param summary - summary of incident from Attendee perspective
	 */
	public AttendeeIncident(AggregateReference<Attendee, Integer> attendeeId, String summary) {
		super();		
		this.attendeeId = attendeeId;
		this.summary = summary;
	}
	
	
	/**
	 * Get the student associated with the AttendeeIncident
	 * @param session - session of the incident
	 * @return Student
	 */
	public Student getStudent(Session session) {
		
		Student student = session.getAttendee(this.getAttendeeId().getId().intValue()).getStudent();
		return student;
		
	}	
	
	/**
	 * Get the student associated with the AttendeeIncident
	 * @return Student
	 */
	public Student getStudent() {
		if (student == null) {
			student = Student.findByAttendeeId(attendeeId.getId().intValue());
		}
		return student;
	}	
	
}
