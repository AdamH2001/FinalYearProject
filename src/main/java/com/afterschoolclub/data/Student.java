package com.afterschoolclub.data;

import com.afterschoolclub.data.repository.StudentRepository;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;


import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
public class Student {
	
	public static StudentRepository repository = null;
	
	@Id
	private int studentId;
	AggregateReference<StudentClass, Integer> classId;
	private String firstName;
	private String surname;
	private LocalDate dateOfBirth;
	private LocalDateTime healthQuestionnaireCompleted = LocalDateTime.now();
	private boolean consentToShare;
	@MappedCollection(idColumn = "student_id")
	private Set<Attendee> attendees = new HashSet<>();
	@MappedCollection(idColumn = "student_id")
	private Set<MedicalNote> medicalNotes = new HashSet<>();

	
	@Transient
	private transient StudentClass studentClass = null;
	
	
	public static List<Student> findByAttendeeId(int attendeeId) {		
		return repository.findByAttendeeId(attendeeId);
	}	
	
	
	public Student(String firstName, String surname, LocalDate dateOfBirth, boolean consentToShare) {
		super();
		this.firstName = firstName;
		this.surname = surname;
		this.dateOfBirth = dateOfBirth;
		this.consentToShare = consentToShare;
	}
	
	/**
	 * @return the full name
	 */
	public String getFullName() {
		return firstName.concat(" ").concat(surname);
	}
	
	public boolean isAttendingEvent(Event event) {
		return this.getAttendee(event.getEventId()) != null;
	}
	
	public void addAttendee(Attendee attendee) {
		this.attendees.add(attendee);
	}
	
	public void addMedicalNote(MedicalNote medicalNote) {
		this.medicalNotes.add(medicalNote);
	}
	
	public void deregister(int eventId) {
		Attendee attendee = this.getAttendee(eventId);
		if (attendee != null) {
			attendees.remove(attendee);
		}
	}
	
	public Attendee getAttendee(int eventId) {
		Attendee result = null;
		Iterator<Attendee> attendeeIterator = attendees.iterator();
		
		while (result == null && attendeeIterator.hasNext()) {
			Attendee attendee = attendeeIterator.next();
			if (attendee.getEventId().getId() == eventId)
				result = attendee;	
		}
		return result;
	}
	
	public Attendee getAttendee(Event event) {		
		return getAttendee(event.getEventId());
	}	

	public StudentClass getStudentClass() {
		if (studentClass == null) {
			StudentClass classFound = StudentClass.findById(this.getClassId().getId());
			if (classFound != null) {
				studentClass = classFound;
			}
		}
		return studentClass;
		
	}
	
	public boolean equals(Student otherStudent) {		
		return this.getStudentId() == otherStudent.getStudentId();		
	}	
	
	public int getCostOfEvent(Event event) {
		int totalCost = 0;				
		Attendee attendee = getAttendee(event.getEventId());
		if (attendee != null) {
			totalCost += event.getClub().getBasePrice();					
			Set <AttendeeMenuChoice> menuChoices = attendee.getMenuChoices();
			for (AttendeeMenuChoice amc: menuChoices ) {
				totalCost +=event.getOptionCost(amc.getMenuOptionId().getId());
			}
		}
		return totalCost;
	}
	
	public boolean chosenMenuOptionForEvent(Event event, int menuOptionId) {
		boolean result = false;				
				
		Attendee attendee = getAttendee(event.getEventId());
		if (attendee != null) {
			Set <AttendeeMenuChoice> menuChoices = attendee.getMenuChoices();
			Iterator <AttendeeMenuChoice> iterator = menuChoices.iterator();
			while (iterator.hasNext() && !result) {
				result = iterator.next().getMenuOptionId().getId() == menuOptionId;
			}
		}
		return result;		
	}

	public String getIdAsString() {
		return String.valueOf(getStudentId());
	}
	
}
