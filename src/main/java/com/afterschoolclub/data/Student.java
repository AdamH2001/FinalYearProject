package com.afterschoolclub.data;

import com.afterschoolclub.data.MedicalNote.Type;
import com.afterschoolclub.data.repository.StudentRepository;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;


import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 *  Class that encapsulates the data and operations for a Student of AfterSchool Club  
 */

@Getter
@Setter
@ToString
public class Student implements Comparable<Student>{
	
	/**
	 * Repository to retrieve and store instances
	 */
	public static StudentRepository repository = null;
	
	/**
	 * Primay key for student
	 */
	@Id
	private int studentId;
	
	/**
	 * Foreign key to class 
	 */
	AggregateReference<StudentClass, Integer> classId;
	
	/**
	 * Foreign key to parent 
	 */
	AggregateReference<Parent, Integer> parentId;
	
	/**
	 * Student first name
	 */
	private String firstName = "";
	
	/**
	 * Student surname 
	 */
	private String surname = "";
	
	/**
	 * Date of birth for student 
	 */
	private LocalDate dateOfBirth;
	
	/**
	 * Date and Time when healthQuestionnaire was last check / submitted 
	 */
	private LocalDateTime healthQuestionnaireCompleted = LocalDateTime.now();
	
	
	/**
	 * Indicates if parent has consented to share MedicalNotes
	 */
	private boolean consentToShare = false;
	
	private LocalDateTime dateRequested = LocalDateTime.now();
	
	/**
	 * True of has been verified by administrator 
	 */
	private boolean adminVerified = false;
	
	/**
	 * Indicates if student is ACTIVE or INACTIVE 
	 */
	private State state = State.ACTIVE;	
	
	/**
	 * Set of Attendees for this Student
	 */
	@MappedCollection(idColumn = "student_id")
	private Set<Attendee> attendees = new HashSet<>();
	
	/**
	 * Set of MedicalNotes for this Student
	 */
	@MappedCollection(idColumn = "student_id")
	private Set<MedicalNote> medicalNotes = new HashSet<>();


	/**
	 * Cached class so don't need to keep looking it up from repostiory
	 */
	@Transient
	private transient StudentClass studentClass = null;
	
	/**
	 * Cached user so don't need to keep looking it up from repostiory
	 */
	@ToString.Exclude
	@Transient
	private transient User user = null;
	
	
	/**
	 * Return specific Student for a given attendeeId
	 * @param attendeeId - primay key for attendee
	 * @return Student
	 */
	public static Student findByAttendeeId(int attendeeId) {		
		List<Student> students = repository.findByAttendeeId(attendeeId);
		Student result = null;
		if (students.size() > 0) {
			result = students.get(0);
		}
		return result;
	}	
	
	/**
	 * Return all the students
	 * @return List of Students
	 */
	public static List<Student> findAll() {
		return  new ArrayList<>((Collection<? extends Student>) repository.findAll());		
	}	
	
	/**
	 * Return a list of Students by state and verification status
	 * @param state - ACTIVE or INACTIVE
	 * @param verified - true of verified by administrator or flase if not
	 * @return List of Students
	 */
	public static List<Student> findByStateVerified(State state, boolean verified) {
		return repository.findByStateVerified(state, verified);		
	}		
	
	/**
	 * Return Student for a given id
	 * @param studentId - id of student to return
	 * @return Studnet
	 */
	
	public static Student findById(int studentId) {
		Optional<Student> optional = repository.findById(studentId);
		Student student = null;
		if (optional.isPresent()) {
			student = optional.get();
		}
		return student;
	}	
	
	
	/**
	 * Default constructor
	 */
	public Student() {
		super();
	}
	
	

	
	/**
	 * @return the full name
	 */
	public String getFullName() {
		return firstName.concat(" ").concat(surname);
	}
	
	/**
	 * Return true is this student is addeing a specified session
	 * @param session - Session Object
	 * @return - true if attending session otherwise false
	 */
	public boolean isAttendingSession(Session session) {
		return this.getAttendee(session.getSessionId()) != null;
	}
	
	/**
	 * Add attendeeo object to this student
	 * @param attendee -Attendee to add
	 */
	public void addAttendee(Attendee attendee) {
		this.attendees.add(attendee);
	}
	
	/**
	 * Add a medical note for this student
	 * @param medicalNote Medical note to add
	 */
	public void addMedicalNote(MedicalNote medicalNote) {
		this.medicalNotes.add(medicalNote);
	}
	
	/**
	 * Remove the booking for this student for the specified session
	 * @param sessionId - primary key for session
	 */
	public void deregister(int sessionId) {
		Attendee attendee = this.getAttendee(sessionId);
		if (attendee != null) {
			attendees.remove(attendee);
		}
	}
	
	/**
	 * Return the Attendee related to this student if attending
	 * a given session
	 * @param sessionId  - primary key for session 
	 * @return - Attendee
	 */
	public Attendee getAttendee(int sessionId) {
		Attendee result = null;
		Iterator<Attendee> attendeeIterator = attendees.iterator();
		
		while (result == null && attendeeIterator.hasNext()) {
			Attendee attendee = attendeeIterator.next();
			if (attendee.getSessionId().getId() == sessionId)
				result = attendee;	
		}
		return result;
	}
	
	/**
	 * Return the Attendee related to this student if attending
	 * a given session
	 * @param session - Session object
	 * @return - Attendee
	 */
	
	public Attendee getAttendee(Session session) {		
		return getAttendee(session.getSessionId());
	}	

	/**
	 * 
	 * @return the StudentClass this student is current in
	 */
	
	public StudentClass getStudentClass() {
		if (studentClass == null) {
			StudentClass classFound = StudentClass.findById(this.getClassId().getId());
			if (classFound != null) {
				studentClass = classFound;
			}
		}
		return studentClass;
		
	}
	
	
	/**
	 * Return true is this is same as otherStudent
	 * @param otherStudent - otherStudent to compare against
	 * @return true is the same entity otherwise returns false
	 */
	
	public boolean equals(Student otherStudent) {		
		return this.getStudentId() == otherStudent.getStudentId();		
	}	

	
	/**
	 * Return cost for this student attending a specific session. 
	 * The cost includes the baseprice and the cost of options 
	 * @param session - session object
 
	 * @return total cost
	 */
	
	
	public int getCostOfSession(Session session) {
		int totalCost = 0;				
		Attendee attendee = getAttendee(session.getSessionId());
		if (attendee != null) {
			totalCost += session.getClub().getBasePrice();	
			Set <AttendeeMenuChoice> menuChoices = attendee.getMenuChoices();
			
			for (AttendeeMenuChoice amc: menuChoices ) {
				totalCost +=session.getOptionCost(amc.getMenuGroupOptionId().getId());
			}
		}
		return totalCost;
	}
	
	
	
	/**
	 * Return true if chosen a specific menuoption for a session 
	 * @param session - session object
	 * @param menuGroupOptionId - primary key of the menu group option
	 * @return true if chosen otherwise return false
	 */
	public boolean chosenMenuOptionForSession(Session session, int menuGroupOptionId) {
		boolean result = false;				
				
		Attendee attendee = getAttendee(session.getSessionId());
		if (attendee != null) {
			Set <AttendeeMenuChoice> menuChoices = attendee.getMenuChoices();
			Iterator <AttendeeMenuChoice> iterator = menuChoices.iterator();
			while (iterator.hasNext() && !result) {
				result = iterator.next().getMenuGroupOptionId().getId() == menuGroupOptionId;
			}
		}
		return result;		
	}


	/**
	 * @return the StudentId as a String
	 */
	public String getIdAsString() {
		return String.valueOf(getStudentId());
	}
	

	/**
	 * @return - the number of sessions in the future this user is attending
	 */	
	public int numFutureSessionsAttending() {
		int result = repository.numFutureSessionsAttending(studentId);
		return result;
	}
	
	/**
	 * Get the MedicalNote of specified type
	 * @param type - type of note
	 * @return - MedicalNote
	 */
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
	
	/**
	 * Get text for the medical note of specified type
	 * @param type - type of note
	 * @return - text of note
	 */
	public String getMedicalNoteText(Type type) {
		
		MedicalNote note = getMedicalNote(type);
		String result = "";
		
		if (note != null) {
			result = note.getNote();
		}
		return result; 
	}	
	
	
	/**
	 * Set text for the medical note of specified type
	 * @param type - type of note 
	 * @param noteText - notes to store
	 */		

	public void setMedicalNoteText(Type type, String noteText) {
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
	
	
	
	/**
	 * @return the notes against note type Allergy
	 */	
	public String getAllergyNoteText() {
		return getMedicalNoteText(Type.ALLERGY);
	}
	
	/**
	 * Set text for the type of note Allergy
	 * @param noteText - notes to store
	 */	
	public void setAllergyNoteText(String noteText) {
		setMedicalNoteText(Type.ALLERGY, noteText);
	}
	
	
	/**
	 * @return the notes against note type Health
	 */	
	public String getHealthNoteText() {
		return getMedicalNoteText(Type.HEALTH);
	}
	
	/**
	 * Set text for the type of note Health
	 * @param noteText - notes to store
	 */	
	public void setHealthNoteText(String noteText) {
		setMedicalNoteText(Type.HEALTH, noteText);
	}
	
	
	/**
	 * @return the notes against note type Diet
	 */	
	public String getDietNoteText() {
		return getMedicalNoteText(Type.DIET);
	}
	
	/**
	 * Set text for the type of note Diet
	 * @param noteText - notes to store
	 */	
	public void setDietNoteText(String noteText) {
		setMedicalNoteText(Type.DIET, noteText);
	}	
	
	
	/**
	 * @return the notes against note type Careplan
	 */	
	
	public String getCarePlanNoteText() {
		return getMedicalNoteText(Type.CAREPLAN);
	}
	
	/**
	 * Set text for the type of note CarePlan
	 * @param noteText - notes to store
	 */	
	public void setCarePlanNoteText(String noteText) {
		setMedicalNoteText(Type.CAREPLAN, noteText);
	}		
	
	
	/**
	 * @return the notes against note type Medication
	 */	
	public String getMedicationNoteText() {
		return getMedicalNoteText(Type.MEDICATION);
	}
	
	/**
	 * Set text for the type of note Medication
	 * @param noteText - notes to store
	 */	
	public void setMedicationNoteText(String noteText) {
		setMedicalNoteText(Type.MEDICATION, noteText);
	}		
	
	/**
	 * @return the notes against note type Other
	 */
	public String getOtherNoteText() {
		return getMedicalNoteText(Type.OTHER);
	}
	
	/**
	 * Set text for the type of note Other
	 * @param noteText - notes to store
	 */
	public void setOtherNoteText(String noteText) {
		setMedicalNoteText(Type.OTHER, noteText);
	}	
	
	/**
	 * Update the timestamp when the healthQuestionnaire was completed
	 */
	public void updateTimestamp() {
		healthQuestionnaireCompleted = LocalDateTime.now();
	}	
	
	/**
	 * @return the user for this Student
	 */
	public User getUser() {
		if (user == null) {
			List<User> users = User.repository.findForStudentId(studentId); 		
			if (users.size() > 0) {
				user = users.get(0);
			}		
		}
		return user;
	}
	
	/**
	 * @return the parent for this student
	 */
	public Parent getParent() {
		return getUser().getParent();
		
	}	

	/**
	 * Update all the fields in for this student in the repository
	 */
	public void update() {
		repository.update(studentId, classId.getId().intValue(), firstName, surname, dateOfBirth, state, adminVerified);
	}
	
	/**
	 *  Delete this Student from the repository
	 */
	public void delete()
	{
		repository.delete(this);
	}	
	
	/**
	 *  Save this student to the repository
	 */
	public void save()
	{
		repository.save(this);
	}
	
	/**
	 * @return true if the healthQuestionnaie has not expired
	 */
	public boolean healthQuestionaireValid() {
		int cannotBeOlderThan = 365; //TODO should be a configurable value.
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime questionaireExpires = this.getHealthQuestionnaireCompleted().plusDays(cannotBeOlderThan);
		return questionaireExpires.isAfter(today);		
	}

	
			
	/**
	 * @return true if has any type of medical not otherwise return false
	 */
	public boolean hasMedicalNotes() {
		return getAllergyNoteText().length()>0 || getHealthNoteText().length() > 0 || getDietNoteText().length() > 0 ||
				getCarePlanNoteText().length() > 0 || getMedicationNoteText().length()>0 || getOtherNoteText().length() > 0;
	}
	
	/**
	 * Compare this Student with another based on the name
	 */
	public int compareTo(Student otherStudent) {
    	String s1 = this.getSurname().toLowerCase().concat(this.getFirstName().toLowerCase());
    	String s2 = otherStudent.getSurname().toLowerCase().concat(otherStudent.getFirstName().toLowerCase());
    	return s1.compareTo(s2);		
	}
}
