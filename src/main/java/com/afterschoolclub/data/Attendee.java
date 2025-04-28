package com.afterschoolclub.data;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a Attendee   
 */

@Getter
@Setter
@ToString
public class Attendee {
	
	public enum Registration {
		ABSENT, 
		PRESENT, 
		NOTRECORDED 		
	}	
	
	/**
	 * Primary key for  Attendee 
	 */
	@Id
	private int attendeeId;
	
	/**
	 * Foreign key to session
	 */
	AggregateReference<Session, Integer> sessionId;
	
	/**
	 * Foreign key to student 
	 */
	AggregateReference<Student, Integer> studentId;

	/**
	 * registration state indicating whether attended or not  
	 */
	
	private Registration attended = Registration.NOTRECORDED;

	/**
	 * Set of AttendeeMenuCHoices
	 */
	@MappedCollection(idColumn = "attendee_id")
	private Set<AttendeeMenuChoice> menuChoices = new HashSet<>();
	
	/**
	 * Set of attendee Incidents
	 */
	@MappedCollection(idColumn = "attendee_id")
	private Set<AttendeeIncident> attendeeIncidents = new HashSet<>();
	
	
    /**
     * student cached so don't have to retrieve from repository all the time
     */
    @ToString.Exclude
	@Transient
	private transient Student student;
	
	/**
	 * Constructor
	 * @param sessionId - primary key to session
	 * @param studentId - primary key to student
	 */
	public Attendee(AggregateReference<Session, Integer> sessionId, int studentId) {
		super();
		this.sessionId = sessionId;
		this.studentId = AggregateReference.to(studentId);
	}
	

	
	/**
	 * Add a MenuChoice for attendee
	 * @param choice AttendeeMenuChoice
	 */
	public void addAttendeeMenuChoice(AttendeeMenuChoice choice) {
		menuChoices.add(choice);
	}
	
	/**
	 * Remove menu choices
	 */
	public void clearAttendeeMenuChoices() {
		menuChoices.clear();
	}	
	
	/**
	 * @return student for this attendee
	 */
	public Student  getStudent() {
		if (student == null) {
			student = Student.findByAttendeeId(this.getAttendeeId()); 
		}		
		return student; 
		
	}		
	
	/**
	 * @return true of attendee attended otherwise return false
	 */
	public boolean didAttend() {
		return attended == Registration.PRESENT;
	}

	
	/**
	 * @return true if attendee was absent otherwise return false
	 */
	public boolean wasAbsent() {
		return attended == Registration.ABSENT;
	}
}
