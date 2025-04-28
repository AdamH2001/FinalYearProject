package com.afterschoolclub.data;

import java.util.Map;
import java.util.HashMap;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

import com.afterschoolclub.data.repository.SessionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a Session   
 */

@Getter
@Setter
@ToString
public class Session {
	/**
	 * Repository to retrieve and store instances
	 */
	public static SessionRepository repository = null;

	
	/**
	 * Primary Key for the session
	 */
	@Id
	private int sessionId;
		
	/**
	 * Foreign key to Club
	 */
	AggregateReference<Club, Integer> clubId;
	/**
	 * Foreign key to RecurrenceSpecificaton
	 */
	AggregateReference<RecurrenceSpecification, Integer> recurrenceSpecificationId;
	
	
	/**
	 * start date and time
	 */
	private LocalDateTime startDateTime;
	/**
	 * end date and time
	 */
	private LocalDateTime endDateTime;	
	/**
	 * Max allowed attendees
	 */
	private int maxAttendees;
	
	
	/**
	 * parent notes for the session
	 */
	private String parentNotes="";

	/**
	 * Adminsitraotr notes for the session
	 */
	private String administratorNotes="";
	
	/**
	 * Set of resources for this session
	 */
	@MappedCollection(idColumn = "session_id")
	private Set<SessionResource> sessionResources = new HashSet<>();
	
	/**
	 * Set of Attendees for this session 
	 */
	@MappedCollection(idColumn = "session_id")
	private Set<Attendee> attendees = new HashSet<>();

	/**
	 * Set of SessionMenus for this session
	 */
	@MappedCollection(idColumn = "session_id")
	private Set<SessionMenu> sessionMenus = new HashSet<>();	
	
	
	/**
	 * Cache of Club so don't have to keep retrieving from repository 
	 */
	@Transient
	private transient Club sessionClub = null;
	
	/**
	 * Cache of RecurrenceSpecificaiton so don't have to keep retrieving from repository
	 */
	@Transient
	private transient RecurrenceSpecification recurrenceSpecification = null; 	
	
	/**
	 * Cache of Menu Groups so don't have to keep retrieving from repository
	 */	@Transient
	private transient List<MenuGroup> menuGroups = null; 
	
	/**
	 * Cache of required resources so don't have to keep retrieving from repository
	 */
	@Transient
	private transient List<Resource> requiredResources = null; 
	

		
	/**
	 * Return all the sessions fo a specific month
	 * @param date - LocalDate - date within the month 
	 * @return List of Session
	 */
	public static List<Session> findForMonth(LocalDate date) {
		LocalDate monthStart = date.withDayOfMonth(1);
		LocalDate nextMonth = date.plusMonths(1);
		return repository.findSessionsBetweenDates(monthStart, nextMonth);
	}
	
	/**
	 * Find all the sessions occuring on a specific date
	 * @param date - LocalDate
	 * @return List of Sessions
	 */
	public static List<Session> findForDay(LocalDate date) {
		LocalDate dayStart = date;
		LocalDate nextDay = date.plusDays(1);
		return repository.findSessionsBetweenDates(dayStart, nextDay);
	}
		
	/**
	 * Find all sessions that have used or plan to use resource
	 * @param resourceId - primary key for resource 
	 * @return List of Session
	 */
	public static  List<Session> findByResourceId(int resourceId) {
		return repository.findByResourceId(resourceId);
	}		
	
	/**
	 * Return all the sessions with incidents
	 * @return List of Session
	 */
	public static  List<Session> findAllWithIncidents() {
		return repository.findAllWithIncidents();
	}		
	
	/**
	 * Return all Sessions that have a incident for specified student
	 * @param studentId - primary key for student
	 * @return List of Session
	 */
	public static  List<Session> findAllWithIncidentsForStudent(int studentId) {
		return repository.findAllWithIncidentsForStudent(studentId);
	}		
	
	
	/**
	 * Return  list of sessions that have future demand on a specific resource 
	 * @param resourceId - primary key for resource
	 * @return List of Session
	 */
	public static  List<Session> findByFutureDemandOnResourceId(int resourceId) {
		return repository.findByFutureDemandOnResourceId(resourceId);
	}	
	
	
	/**
	 * Return Session by its identifier
	 * @param sessionId - primary key for Session 
	 * @return Session or null if not found
	 */
	public static Session findById(int sessionId) {
		Optional<Session> optional = repository.findById(sessionId);
		Session session = null;
		if (optional.isPresent()) {
			session = optional.get();
		}
		return session;
	}	
	
	/**
	 * Find all the sessions with a specific recurrence specification  
	 * @param recurringId - primary key of RecurrenceSpecification
	 * @return List of Session
	 */
	public static List<Session>  findRecurringSessions(int recurringId) {
		return repository.findByReccurenceSpecificationId(recurringId);		
		
	}	
		
	
	/**
	 * Save all the session to te repository
	 * @param allSessions = List of Sessions that want to save 
	 */
	public static void saveAll(List<Session> allSessions) {
		repository.saveAll(allSessions);
		
	}	
	
	
	/**
	 * Delte the session 
	 * @param sessionId - primary key of session would like to delete
	 */
	public static void deleteById(int sessionId) {
		repository.deleteById(sessionId);		
	}		
	
	
	/**
	 * Default Constructor 
	 */
	public Session() {
		super();			
		this.clubId= null;
		this.maxAttendees = 10;	
		initialiseStartEndTimes();
	}

	/**
	 * st the recurrence specificaiton for the session
	 * @param rs RecurrenceSpecificaiton
	 */
	public void setRecurrenceSpecification(RecurrenceSpecification rs) {
		this.recurrenceSpecification = rs;
		recurrenceSpecificationId = AggregateReference.to(rs.getRecurrenceSpecificationId());
	}
	
	
	/**
	 * Initiaise the start end times 
	 */
	public void initialiseStartEndTimes() {
		LocalDateTime tomorrow =LocalDateTime.now().plusDays(1); 
		LocalTime startTime = LocalTime.of(15, 30);
		LocalTime endTime = LocalTime.of(17, 0);
		this.startDateTime = LocalDateTime.of(tomorrow.toLocalDate(), startTime);
		this.endDateTime = LocalDateTime.of(startDateTime.toLocalDate(), endTime);
	}
	
	
	
	/**
	 * Return a new Session given the basic feilsds required
	 * @param clubId - primary key for the club
	 * @param startDateTime - LocalDateTim
	 * @param endDateTime - LocalDateTime
	 * @param maxAttendees - max number of students that can attend
	 */
	public Session(AggregateReference<Club, Integer> clubId, LocalDateTime startDateTime,
			LocalDateTime endDateTime, int maxAttendees) {
		super();
		this.clubId= clubId;		
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.maxAttendees = maxAttendees;
		this.sessionClub=null;	
		this.parentNotes = "";
		this.administratorNotes = "";
				
		
		
	}
	
	/**
	 * Copy constructor
	 * @param session
	 */
	public Session(Session session) {
		super();
		
		// Don't copy sessionId 
		
		this.clubId = session.clubId;				
		this.startDateTime = session.startDateTime;
		this.endDateTime = session.endDateTime;
		this.maxAttendees = session.maxAttendees;
		this.sessionClub = session.sessionClub;
		
		for (SessionResource er : session.sessionResources) {
			this.sessionResources.add(new SessionResource(er));
		}		
		
		for (SessionMenu menu : session.sessionMenus) {
			this.sessionMenus.add(new SessionMenu(menu));
		}
		
		this.menuGroups = new ArrayList<MenuGroup>();
		for (MenuGroup menuGroup : session.getMenuGroups()) {
			this.menuGroups.add(menuGroup);
		}		
		
		this.sessionClub = session.sessionClub;
		
		this.recurrenceSpecificationId = session.getRecurrenceSpecificationId();
		this.recurrenceSpecification = session.getRecurrenceSpecification();
		this.parentNotes = session.parentNotes;
		this.administratorNotes = session.administratorNotes;
		
		// Don't copy attendees or incidents
		
		
		
	}

	/**
	 * Save this session to the repository
	 */
	public void save()
	{
		repository.save(this);
	}
	
	/**
	 * Return the Attendee for the attendeeId 
	 * @param attendeeId - primary key for the attendeeId
	 * @return Attendee
	 */
	public Attendee getAttendee(int attendeeId)
	{
		Attendee result = null;
		
		Iterator<Attendee> attendeeIterator = attendees.iterator();
		while (result == null && attendeeIterator.hasNext()) {
			Attendee compare = attendeeIterator.next();
			if (compare.getAttendeeId() == attendeeId)
				result = compare;
		}
		
		return result;
	}
	
	/**
	 * Return the Attendee for a specific Student
	 * @param student
	 * @return Attendee
	 */
	public Attendee getAttendee(Student student)
	{
		Attendee result = null;
		
		Iterator<Attendee> attendeeIterator = attendees.iterator();
		while (result == null && attendeeIterator.hasNext()) {
			Attendee compare = attendeeIterator.next();
			if (compare.getStudentId().getId().intValue() == student.getStudentId())
				result = compare;
		}
		return result;
	}
	
	/**
	 * Return all the attendees sorted by student name
	 * @return List of Attendees
	 */
	public List<Attendee> getSortedAttendees()
	{
	
		Comparator<Attendee> comparator = new Comparator<Attendee>(){
 
		    @Override
		    public int compare(final Attendee o1, final Attendee o2){
		    	String s1 = o1.getStudent().getSurname().toLowerCase().concat(o1.getStudent().getFirstName().toLowerCase());
		    	String s2 = o2.getStudent().getSurname().toLowerCase().concat(o2.getStudent().getFirstName().toLowerCase());
		    	return s1.compareTo(s2);
		    }
		};
				
		List<Attendee> sortedAttendees = new ArrayList<Attendee>();
		sortedAttendees.addAll(attendees);
		Collections.sort(sortedAttendees, comparator);
		return sortedAttendees;
	}

	/**
	 * Get the club Club for this session 
	 * @return Club
	 */
	public Club getClub() {
		if (this.sessionClub == null && clubId != null) {
			sessionClub = Club.findById(clubId.getId());
		}
		return sessionClub;
		
	}
	
	/**
	 * Return the RecurrencSpecificaiton for this session
	 * @return RecurrenceSpecifcation
	 */
	public RecurrenceSpecification getRecurrenceSpecification() {
		if (this.recurrenceSpecification == null && recurrenceSpecificationId != null) {
			recurrenceSpecification = RecurrenceSpecification.findById(recurrenceSpecificationId.getId());
		}
		else if (recurrenceSpecification == null) 			
		{
			recurrenceSpecification = new RecurrenceSpecification();
			recurrenceSpecification.setStartDate(startDateTime.toLocalDate());
			recurrenceSpecification.setEndDate(startDateTime.toLocalDate());
			
		}
		return recurrenceSpecification;
		
	}
	
	
	/**
	 * Returns all the location resources for this session
	 * @return List of Resource
	 */
	public Resource getLocation() {
		Resource location = null;
		List <Resource> allResources = getRequiredResources();
		Iterator<Resource> resourceIterator = allResources.iterator();
		while (location == null & resourceIterator.hasNext()) {
			Resource r = resourceIterator.next();
			Iterator<SessionResource> erIterator = sessionResources.iterator();
			while (location == null & erIterator.hasNext()) {
				SessionResource er = erIterator.next();
				if (er.getResourceId().getId().intValue() == r.getResourceId() && r.getType() == Resource.Type.LOCATION) {
					location = r;
				}				
			}
		}			
		return location;		
	}
	
	/**
	 * Returns all the resources for this session
	 * @return List of Resource
	 */
	public List<Resource> getRequiredResources() {
		if (requiredResources == null) {
			Iterable <Resource> allResources = Resource.findAll();
			requiredResources = new ArrayList<Resource>();
			for (SessionResource er : sessionResources) {
				for (Resource r : allResources) {
					if (er.getResourceId().getId().intValue() == r.getResourceId()) {
						requiredResources.add(r);
					}
				}
			}
		}
		return requiredResources;
	}
	
	/**
	 * Returns all the equipment resources for this session
	 * @return List of Resource
	 */
	public List<Resource> getEquipment() {
		List<Resource> equipment = new ArrayList<Resource>();
		List<Resource> allResources = getRequiredResources();
		Iterator<Resource> resourceIterator = allResources.iterator();
		while (resourceIterator.hasNext()) {
			Resource r = resourceIterator.next();
			Iterator<SessionResource> erIterator = sessionResources.iterator();
			while (erIterator.hasNext()) {
				SessionResource er = erIterator.next();
				if (er.getResourceId().getId().intValue() == r.getResourceId() && r.getType() == Resource.Type.EQUIPMENT) {
					equipment.add(r);
				}				
			}
		}			
		return equipment;
	}
	
	/**
	 * Returns all the staff resources for this session
	 * @return List of Resource
	 */
	public List<Resource> getStaff() {
		List<Resource> staff = new ArrayList<Resource>();
		List<Resource> allResources = getRequiredResources();
		Iterator<Resource> resourceIterator = allResources.iterator();
		while (resourceIterator.hasNext()) {
			Resource r = resourceIterator.next();
			Iterator<SessionResource> erIterator = sessionResources.iterator();
			while (erIterator.hasNext()) {
				SessionResource er = erIterator.next();
				if (er.getResourceId().getId().intValue() == r.getResourceId() && r.getType() == Resource.Type.STAFF) {
					staff.add(r);
				}				
			}
		}			
		return staff;
	}
	
	/**
	 * Return all the MenuGroups used by this session
	 * @return List of MenuGroups
	 */
	public List<MenuGroup> getMenuGroups() {
		if (menuGroups == null) {
			menuGroups = MenuGroup.findBySessionId(sessionId);		
		}
		return menuGroups;
	}
	
	/**
	 * Determine whether session includes a specfic menu group
	 * @param menuGroupId - primary key for menu group
	 * @return true if session uses this group otherwise return false
	 */
	public boolean includesMenuGroup(int menuGroupId) {
		List<MenuGroup> menuGroups = getMenuGroups();
		boolean result = false;
		Iterator<MenuGroup> iterator = menuGroups.iterator();
		
		while (iterator.hasNext() && !result) {
			MenuGroup mg = iterator.next();
			result = mg.getMenuGroupId() == menuGroupId;				
		}		
		return result;
	}
	
	
	
	
	
	/**
	 * Determine if session has a requirement on staff
	 * @param resourceId - primary key for staff resource
	 * @return true if has a requirement otherwise returns false
	 */
	public boolean requiresStaff(int resourceId) {
		boolean required = false;
		List<Resource> requiredStaff = getStaff();
		ListIterator<Resource> staffIterator = requiredStaff.listIterator();
		while (staffIterator.hasNext() && !required) {
			required = staffIterator.next().getResourceId() == resourceId;
		}
		return required;
	}
		
	
	/**
	 * Add SessionResource as requirement for this session
	 * @param sessionResource - SessionResource
	 */
	public void addResource(SessionResource sessionResource) {
		this.sessionResources.add(sessionResource);
	}
	
	/**
	 * Determines if session uses a specific resource
	 * @param resource - Resource to test
	 * @return true if session has a reuirement for resource otherwise returns false
	 */
	public boolean usesResource(Resource resource) {
		boolean result = false;
		
		Iterator<SessionResource> erIterator = sessionResources.iterator();
		while (erIterator.hasNext() && !result) {
			result = erIterator.next().getResourceId().getId() == resource.getResourceId();
		}		
		
		return result; 
	}
	
	/**
	 * Add a session menu to this session
	 * @param menu - SessionMenu to be added
	 */
	public void addSessionMenu(SessionMenu menu) {
		this.sessionMenus.add(menu);
	}	
		
	/**
	 * Return start time  as a String. Suitable for  display in a list or value input field
	 * @return String version of start time	 
	 * */
	public String getStartTime() {
		return startDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));		
	}	
	
	/**
	 * Return end time  as a String. Suitable for  display in a list or value input field
	 * @return String version of end time	 
	 * */
	public String getEndTime() {
		return endDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));		
	}		

	
	/**
	 * Return start date as a String. Suitable for  display in a list
	 * @return String version of start date	 
	 * */
	
	public String getDisplayStartDate() {
		return startDateTime.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));		
	}	
	
	/**
	 * Return end  date as a String. Suitable for  display in a list
	 * @return String version of start date	 
	 * 
	 * */
	public String getDisplayEndDate() {
		return endDateTime.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));		
	}	

	
	/**
	 * Return start date as a String. Suitable for value of input field
	 * @return String version of start date
	 */
	public String getStartDate() {
		return startDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));		
	}	
	
	/**
	 * Return start date as a String. Suitable for value of input field
	 * @return String version of end date
	 */
	public String getEndDate() {
		return endDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));	}	
		
	
	/**
	 * Return whether student is elligible to attend session
	 * @param student
	 * @return return true if is elligible otherwise return false
	 */
	public boolean canAttend(Student student)
	{
		boolean result = this.getClub().isEligible(student);
			
		if (this.endInPast())
			result = false;
		
		if (this.getMaxAttendees() <= this.getAttendees().size() && !student.isAttendingSession(this))
			result = false;
		
		if (!student.healthQuestionaireValid())
			result = false;
		
		return result;				
									
					
	}
	
	/**
	 * @return true if start of event is in the past otherwise return false 
	 */
	public boolean inPast()
	{
		return this.getStartDateTime().isBefore(LocalDateTime.now());
	}
	
	/**
	 * @return true if end of event is in the past otherwise return false
	 */
	public boolean endInPast()
	{
		return this.getEndDateTime().isBefore(LocalDateTime.now());
	}
	
	/**
	 * Is session supposed to be happening now
	 * @return true is supposed to be happening not otherwise return false
	 */
	public boolean isNow()
	{
		return this.getStartDateTime().isBefore(LocalDateTime.now()) && this.getEndDateTime().isAfter((LocalDateTime.now()));
	}
	
	/**
	 *  Return whether student is already booked on session
	 * @param student - student
	 * @return true if is booked on session otherwise return false
	 */
	public boolean registered(Student student)
	{
		return getAttendee(student) != null;
	}
	
	/**
	 * Return whether a student did or did not attend the session
	 * @param student - student 
	 * @return true if attended false otherwise
	 */
	public boolean didAttend(Student student)
	{
		boolean result = false; 
		Attendee attendee = getAttendee(student);
		if (attendee != null)
			result = attendee.didAttend();
		return result;
	}
	
	/**
	 * @return number of students planning to attend
	 */
	public int getNumberAttendees()
	{
		return this.getAttendees().size();		
	}	
	
	/**
	 * @return true if session is fully booked otherwise return false
	 */
	public boolean isFullyBooked()
	{
		return this.getMaxAttendees() <= this.getNumberAttendees();
	}
	
	/**
	 * Return the cost for a specific option for this session
	 * @param menuGroupOptionId - primary key for the option
	 * @return cost in pence
	 */
	public int getOptionCost(int menuGroupOptionId)
	{
		int optionCost = 0;

		List <MenuGroup> menuGroups = getMenuGroups();
		for (MenuGroup menuGroup: menuGroups) {											
			MenuOption menuOption = menuGroup.getMenuOptionFromGroupOptionId(menuGroupOptionId);
			if (menuOption != null) {					
				optionCost = menuOption.getAdditionalCost();
			}
		}														
					
		return optionCost;
	}
	

	
	
	
	/**
	 * @return true if this session has refreshment options otherwise return false
	 */
	public boolean hasOptions()
	{									
		return (sessionMenus != null) && (sessionMenus.size() > 0);
	}	

	
	/**
	 * @return List of SessionResource for this session that are ACTIVE
	 */
	public List<SessionResource> getActiveEquipmentSessionResources() {
		List<Resource> equipment = getEquipment();
		List <SessionResource> result = new ArrayList<SessionResource>();
		Set<SessionResource> allResources = getSessionResources();
		Iterator <SessionResource> iterator = allResources.iterator();
		while (iterator.hasNext()) {
			SessionResource er = iterator.next();
			int resourceId = er.getResourceId().getId().intValue();
			Iterator<Resource> equipmentIterator = equipment.iterator();
			boolean isEquipment = false;
			while (!isEquipment && equipmentIterator.hasNext() ) {
				Resource resource = equipmentIterator.next();
				isEquipment = resource.getResourceId() == resourceId && resource.isActive();
			}
				
			if (isEquipment) {
				result.add(er);				
			}
		}
		
		return result;
	}
	
	
	/**
	 * @return List of Sessions who's timeline overlaps with this session
	 */
	public List<Session> getOverlappingSessions() {
		return repository.findOverlappingSessions(getSessionId(), getStartDateTime(), getEndDateTime());		
	}	
	
	
	
	/**
	 * @return true if this session has sufficient resources otherwise return false
	 */
	public boolean hasSufficientResources() {
		boolean result = true;
		if (!endInPast()) {
			List<ResourceStatus> resourceStatus = getResourceStatus();
			Iterator<ResourceStatus> itr = resourceStatus.iterator();
			while (result && itr.hasNext()) {
				ResourceStatus nextResource = itr.next();
				result = nextResource.isSufficient();
			}						
		}
		return result; 
	}
	
		
	/**
	 * Resset the resource requirements 
	 */
	public void clearResources() {
		sessionResources = new HashSet<>();		
		requiredResources = null;				 
	}
	
	/**
	 * Reset the refreshment menu
	 */
	public void clearMenu() {
		sessionMenus = new HashSet<>();			
		menuGroups = null; 	
	}	
	
	/**
	 * Get the quantity requirements for a specific resource for this session
	 * @param resourceId - primary key for resource
	 * @return
	 */
	public int getRequiredResourceQuantity(int resourceId) {
		int result = 0;
		
		for (SessionResource er : sessionResources) {
			if (er.getResourceId().getId().intValue() == resourceId)
			{				
				if (er.isPerAttendee()) {
					result += er.getQuantity() * this.getMaxAttendees();
				}
				else {
					result += er.getQuantity();
				}					
			}
		}				
		return result; 
	}
	
	/**
	 * @return true of this session is recurring otherwise return false
	 */
	public boolean isRecurring() {
		return startDateTime.toLocalDate().compareTo(getRecurrenceSpecification().getEndDate()) !=0; 
	}
	
	
	/**
	 * Get the incident for this session given an incident Id
	 * @param incidentId - primary key for incident
	 * @return Incident
	 */
	public Incident getIncident(int incidentId) {
		return  Incident.findById(incidentId);
	}	
	

	

	/**
	 * Return all the students for a specific incident
	 * @param incident - Incident to return students for
	 * @return List of Students
	 */
	public List<Student> getStudentsForIncident(Incident incident) {
		List <Student> students = new ArrayList<>();
		List<AttendeeIncident> allAttendeeIncidents = incident.getAttendeeIncidents();
		for (AttendeeIncident ai : allAttendeeIncidents) {
			Student s = this.getAttendee(ai.getAttendeeId().getId().intValue()).getStudent();
			students.add(s);
		}
		Collections.sort(students);

		return students;
		
	}

	
	/**
	 * Delete this session from the repository
	 */
	public void delete() {
		repository.delete(this);
	}	

	/**
	 * Cancel the session and refund all the attendees
	 */
	public void cancel() {
		Set<Attendee> allAttendees = getAttendees();
		for (Attendee attendee: allAttendees) {
			Student student = attendee.getStudent();
			User user = student.getUser();					
			Parent parent = user.getParent();
			int cost = student.getCostOfSession(this);														
			ParentalTransaction pt = parent.recordRefundForClub(cost, getClub(), String.format("Refund for %s due to %s cancelled scheduled on %s", student.getFirstName(), getClub().getTitle(), this.getStartDate()));
			pt.save();					
		}		
		delete();
	}
	
	/**
	 * @return return all the users for this session
	 */
	public List<User> getAllUsers() {
		return User.repository.findAllForSession(this.sessionId);
	}

	/**
	 * Add a list of staff as requirements for this session
	 * @param newStaff
	 */
	public void addStaff(List<Integer>newStaff) {		
		for (Integer staffMember : newStaff) {
			SessionResource er = new SessionResource(AggregateReference.to(staffMember), 1, false);
			this.addResource(er);
		}				
	}
	
	/**
	 * Add a list of equipment as a resource requirement for this session
	 * @param equipment - list of equipment ids 
	 * @param equipmentQuantity - list of corresponding quantities
	 * @param perAttendee - list of indicators whether is required per attendee or not
	 */
	public void addEquipment(List<Integer>equipment, List<Integer>equipmentQuantity,  List<Boolean>perAttendee  ) {
		int counter = 0;				
		for (Integer item : equipment) {					
			if ( item.intValue() != 0) {
				int quantity = equipmentQuantity.get(counter).intValue();
				if (quantity > 0) {
					boolean bPerAttendee = perAttendee.get(counter).booleanValue();
					
					SessionResource er = new SessionResource(AggregateReference.to(item), quantity, bPerAttendee);
					this.addResource(er);
				}
			}
			counter++;
		}					
	}	
	
	/**
	 * Set the menu groups for this session
	 * @param menuGroups - List of primary keys for the MenuGroups
	 */
	public void setMenuGroups(List<Integer>menuGroups) {
		clearMenu();
		if (menuGroups != null)
		{
			for (Integer menu : menuGroups) {
			
				SessionMenu newMenu = new SessionMenu(AggregateReference.to(menu));
				this.addSessionMenu(newMenu);						
			}
		}	
	}
	
	/**
	 * @return all the resource status that are a challenge for this session
	 */
	public List<ResourceStatus> getResourceChallenges() {
		List<ResourceStatus> allStatus = this.getResourceStatus();
		List<ResourceStatus> allChallenges = new ArrayList<ResourceStatus>();
	
		Iterator<ResourceStatus> rsIterator = allStatus.iterator();
		while (rsIterator.hasNext()) {
			ResourceStatus nextStatus = rsIterator.next();
			if (!nextStatus.isSufficient()) {																	
				allChallenges.add(nextStatus);						
			}		
		}
		return allChallenges;
	}
	
	/**
	 * @return all the status of all the resources required for this session
	 */
	public List<ResourceStatus> getResourceStatus() {
		Map<Integer, Integer> resourceRequirements = new HashMap<>(); //maps resourceId to quantity
				
		//get  map of all resourceid and quantities needed
		for (SessionResource er : sessionResources) {
			if (!resourceRequirements.containsKey(er.getResourceId().getId())) {
				resourceRequirements.put(er.getResourceId().getId(), getRequiredResourceQuantity(er.getResourceId().getId().intValue()));
			}
		}
		
		// list of all resource objects requested
		List <Resource> allResources = getRequiredResources();

		OverlappingTimeline overlapTimeline = new OverlappingTimeline(this); 
				
		List<ResourceStatus> result = new ArrayList<ResourceStatus>();
		
		// for each type of resource requested creat a resource status
		for (Integer resourceId : resourceRequirements.keySet()) {
			
			// find the resource object from the id
			
			Resource r = null;
			Iterator<Resource> resourceIterator = allResources.iterator();
			while (r == null && resourceIterator.hasNext()) {
				Resource tmp = resourceIterator.next();
				if (tmp.getResourceId() == resourceId.intValue()) {
					r = tmp;
				}
			}
			
			// determine existing demand for resource
			int committedResource = overlapTimeline.getRequiredResourceQuantity(resourceId.intValue());			
			int quantityRequested = resourceRequirements.get(resourceId);
			
			// create a resource status for each resource requested
			result.add(new ResourceStatus(r, committedResource, quantityRequested, overlapTimeline));						
		}

		return result; 
	}		

	/**
	 * @return all the incident for this session
	 */
	public List<Incident> getIncidents() {
		return Incident.findAllBySessionId(this.sessionId);
	}
}
