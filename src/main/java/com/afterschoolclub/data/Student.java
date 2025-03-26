package com.afterschoolclub.data;

import com.afterschoolclub.data.MedicalNote.Type;
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
import java.util.Optional;
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
	private String firstName = "";
	private String surname = "";
	private LocalDate dateOfBirth;
	private LocalDateTime healthQuestionnaireCompleted = LocalDateTime.now();
	private boolean consentToShare = false;
	
	@MappedCollection(idColumn = "student_id")
	private Set<Attendee> attendees = new HashSet<>();
	
	@MappedCollection(idColumn = "student_id")
	private Set<MedicalNote> medicalNotes = new HashSet<>();

	
	@Transient
	private transient StudentClass studentClass = null;
	
	
	public static List<Student> findByAttendeeId(int attendeeId) {		
		return repository.findByAttendeeId(attendeeId);
	}	
	
	public static Student findById(int studentId) {
		Optional<Student> optional = repository.findById(studentId);
		Student student = null;
		if (optional.isPresent()) {
			student = optional.get();
		}
		return student;
	}	
	
	
	public Student() {
		super();
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
/*	
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
	} */
	
	public int getCostOfEvent(Event event) {
		int totalCost = 0;				
		Attendee attendee = getAttendee(event.getEventId());
		if (attendee != null) {
			totalCost += event.getClub().getBasePrice();	
			Set <AttendeeMenuChoice> menuChoices = attendee.getMenuChoices();
			
			for (AttendeeMenuChoice amc: menuChoices ) {
				totalCost +=event.getOptionCost(amc.getMenuGroupOptionId().getId());
			}
		}
		return totalCost;
	}
	
	
	public boolean chosenMenuOptionForEvent(Event event, int menuGroupOptionId) {
		boolean result = false;				
				
		Attendee attendee = getAttendee(event.getEventId());
		if (attendee != null) {
			Set <AttendeeMenuChoice> menuChoices = attendee.getMenuChoices();
			Iterator <AttendeeMenuChoice> iterator = menuChoices.iterator();
			while (iterator.hasNext() && !result) {
				result = iterator.next().getMenuGroupOptionId().getId() == menuGroupOptionId;
			}
		}
		return result;		
	}
	
	/*
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
	 */

	public String getIdAsString() {
		return String.valueOf(getStudentId());
	}

	public int numFutureSessionsAttending() {
		int result = repository.numFutureSessionsAttending(studentId);
		return result;
	}
	

	public MedicalNote getMedicalNote(Type type) {
		Iterator <MedicalNote> itr = medicalNotes.iterator();
		MedicalNote result = null;
		while (result == null && itr.hasNext()) {
			MedicalNote nextNote = itr.next();
			if (nextNote.getNoteType() == type) {
				result = nextNote;
			}
		}
		return result; 
	}
	
	public String getMedicalNoteText(Type type) {
		
		MedicalNote note = getMedicalNote(type);
		String result = "";
		
		if (note != null) {
			result = note.getNote();
		}
		return result; 
	}	
	
	public void setMedicalNoteText(Type type, String noteText) {
		String result = "";		
		MedicalNote note = getMedicalNote(type);
		
		if (note == null) {
			if (noteText.length() > 0) {
				this.addMedicalNote(new MedicalNote(type, noteText));
			}
		}
		else {
			note.setNote(noteText);
		}
	}		
	
	
	public String getAllergyNoteText() {
		return getMedicalNoteText(Type.ALLERGY);
	}
	
	public void setAllergyNoteText(String noteText) {
		setMedicalNoteText(Type.ALLERGY, noteText);
	}
	
	public String getHealthNoteText() {
		return getMedicalNoteText(Type.HEALTH);
	}
	
	public void setHealthNoteText(String noteText) {
		setMedicalNoteText(Type.HEALTH, noteText);
	}
	
	public String getDietNoteText() {
		return getMedicalNoteText(Type.DIET);
	}
	
	public void setDietNoteText(String noteText) {
		setMedicalNoteText(Type.DIET, noteText);
	}	
	
	public String getCarePlanNoteText() {
		return getMedicalNoteText(Type.CAREPLAN);
	}
	
	public void setCarePlanNoteText(String noteText) {
		setMedicalNoteText(Type.CAREPLAN, noteText);
	}		
	
	public String getMedicationNoteText() {
		return getMedicalNoteText(Type.MEDICATION);
	}
	
	public void setMedicationNoteText(String noteText) {
		setMedicalNoteText(Type.MEDICATION, noteText);
	}		
	
	public String getOtherNoteText() {
		return getMedicalNoteText(Type.OTHER);
	}
	
	public void setOtherNoteText(String noteText) {
		setMedicalNoteText(Type.OTHER, noteText);
	}	
	
	public void updateTimestamp() {
		healthQuestionnaireCompleted = LocalDateTime.now();
	}
	
	
	
}
