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
	AggregateReference<Session, Integer> sessionId;
	AggregateReference<Student, Integer> studentId;

	private Registration attended = Registration.NOTRECORDED;

	@MappedCollection(idColumn = "attendee_id")
	private Set<AttendeeMenuChoice> menuChoices = new HashSet<>();
	

    @ToString.Exclude
	@Transient
	private transient Student student;
	
	/**
	 * @param sessionId
	 * @param studentId
	 */
	public Attendee(AggregateReference<Session, Integer> sessionId, int studentId) {
		super();
		this.sessionId = sessionId;
		this.studentId = AggregateReference.to(studentId);
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
