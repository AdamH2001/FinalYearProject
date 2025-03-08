package com.afterschoolclub.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Attendee {
	
	public enum Registration {
		ABSENT, 
		PRESENT, 
		NOTRECORDED 		
	}	
	
	@Id
	private int attendeeId;
	AggregateReference<Event, Integer> eventId;
	private int studentId;
	private Registration attended = Registration.NOTRECORDED;
	@MappedCollection(idColumn = "attendee_id")
	private Set<Incident> incidents = new HashSet<>();
	@MappedCollection(idColumn = "attendee_id")
	private Set<AttendeeMenuChoice> menuChoices = new HashSet<>();
	
    @ToString.Exclude
	@Transient
	private transient Student student;
	
	/**
	 * @param eventId
	 * @param studentId
	 */
	public Attendee(AggregateReference<Event, Integer> eventId, int studentId) {
		super();
		this.eventId = eventId;
		this.studentId = studentId;
	}
	
	public void addIncident(Incident incident) {
		incidents.add(incident);
	}
	
	public void addAttendeeMenuChoice(AttendeeMenuChoice choice) {
		menuChoices.add(choice);
	}
	
	public void clearAttendeeMenuChoices() {
		menuChoices.clear();
	}	
	
	public Student  getStudent() {
		if (student == null) {
			List<Student> studList = Student.findByAttendeeId(this.getAttendeeId());
			student = studList.get(0);				
		}
		return student;
		
	}		
	
	public boolean didAttend() {
		return attended == Registration.PRESENT;
	}

	
	public boolean wasAbsent() {
		return attended == Registration.ABSENT;
	}
}
