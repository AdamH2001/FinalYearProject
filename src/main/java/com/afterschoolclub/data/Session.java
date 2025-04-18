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
import org.springframework.transaction.annotation.Transactional;

@Getter
@Setter
@ToString
public class Session {
	public static SessionRepository repository = null;

	public static List<Session> findForMonth(LocalDate date) {
		LocalDate monthStart = date.withDayOfMonth(1);
		LocalDate nextMonth = date.plusMonths(1);
		return repository.findSessionsBetweenDates(monthStart, nextMonth);
	}
	
	public static List<Session> findForDay(LocalDate date) {
		LocalDate dayStart = date;
		LocalDate nextDay = date.plusDays(1);
		return repository.findSessionsBetweenDates(dayStart, nextDay);
	}
		
	public static  List<Session> findByResourceId(int resourceId) {
		return repository.findByResourceId(resourceId);
	}		
	
	public static  List<Session> findAllWithIncidents() {
		return repository.findAllWithIncidents();
	}		
	
	public static  List<Session> findAllWithIncidentsForStudent(int studentId) {
		return repository.findAllWithIncidentsForStudent(studentId);
	}		
	
	
	public static  List<Session> findByFutureDemandOnResourceId(int resourceId) {
		return repository.findByFutureDemandOnResourceId(resourceId);
	}	
	
	
	public static Session findById(int sessionId) {
		Optional<Session> optional = repository.findById(sessionId);
		Session session = null;
		if (optional.isPresent()) {
			session = optional.get();
		}
		return session;
	}	
	
	public static List<Session>  findRecurringSessions(int recurringId) {
		return repository.findByReccurenceSpecificationId(recurringId);		
		
	}	
		
	
	public static void saveAll(List<Session> allSessions) {
		repository.saveAll(allSessions);
		
	}	
	
	
	public static void deleteById(int sessionId) {
		repository.deleteById(sessionId);		
	}		
	
	
	
	@Id
	private int sessionId;
		
	AggregateReference<Club, Integer> clubId;
	AggregateReference<RecurrenceSpecification, Integer> recurrenceSpecificationId;
	
	
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;	
	private int maxAttendees;
	
	
	private String parentNotes="";

	private String administratorNotes="";
	
	@MappedCollection(idColumn = "session_id")
	private Set<SessionResource> sessionResources = new HashSet<>();
	
	@MappedCollection(idColumn = "session_id")
	private Set<Attendee> attendees = new HashSet<>();

	@MappedCollection(idColumn = "session_id")
	private Set<SessionMenu> sessionMenus = new HashSet<>();	
	
	@MappedCollection(idColumn = "session_id")
	private Set<Incident> incidents = new HashSet<>();
	
	
	@Transient
	private transient Club sessionClub = null; 

	@Transient
	private transient RecurrenceSpecification recurrenceSpecification = null; 	
	
	@Transient
	private transient List<MenuGroup> menuGroups = null; 
	
	@Transient
	private transient List<Resource> requiredResources = null; 
	

	
	public Session() {
		super();			
		this.clubId= null;
		this.maxAttendees = 10;	
		initialiseStartEndTimes();
	}

	public void setRecurrenceSpecification(RecurrenceSpecification rs) {
		this.recurrenceSpecification = rs;
		recurrenceSpecificationId = AggregateReference.to(rs.getRecurrenceSpecificationId());
	}
	
	
	public void initialiseStartEndTimes() {
		LocalDateTime tomorrow =LocalDateTime.now().plusDays(1); 
		LocalTime startTime = LocalTime.of(15, 30);
		LocalTime endTime = LocalTime.of(17, 0);
		this.startDateTime = LocalDateTime.of(tomorrow.toLocalDate(), startTime);
		this.endDateTime = LocalDateTime.of(tomorrow.toLocalDate(), endTime);;
	}
	
	
	
	/**
	 *
	 * @param clubId
	 * @param location
	 * @param startDateTime
	 * @param endDateTime
	 * @param maxAttendees
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
	 *
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

	public void save()
	{
		repository.save(this);
	}
	
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

	public Club getClub() {
		if (this.sessionClub == null && clubId != null) {
			sessionClub = Club.findById(clubId.getId());
		}
		return sessionClub;
		
	}
	
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
	
	public List<MenuGroup> getMenuGroups() {
		if (menuGroups == null) {
			menuGroups = MenuGroup.findBySessionId(sessionId);		
		}
		return menuGroups;
	}
	
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
	
	
	
	
	
	public boolean requiresStaff(int resourceId) {
		boolean required = false;
		List<Resource> requiredStaff = getStaff();
		ListIterator<Resource> staffIterator = requiredStaff.listIterator();
		while (staffIterator.hasNext() && !required) {
			required = staffIterator.next().getResourceId() == resourceId;
		}
		return required;
	}
		
	
	public void addResource(SessionResource sessionResource) {
		this.sessionResources.add(sessionResource);
	}
	
	public boolean usesResource(Resource resource) {
		boolean result = false;
		
		Iterator<SessionResource> erIterator = sessionResources.iterator();
		while (erIterator.hasNext() && !result) {
			result = erIterator.next().getResourceId().getId() == resource.getResourceId();
		}		
		
		return result; 
	}
	
	public void addSessionMenu(SessionMenu menu) {
		this.sessionMenus.add(menu);
	}	
		
	public String getStartTime() {
		return startDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));		
	}	
	
	public String getEndTime() {
		return endDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));		
	}		

	
	public String getDisplayStartDate() {
		return startDateTime.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));		
	}	
	
	public String getDisplayEndDate() {
		return endDateTime.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));		
	}	

	
	public String getStartDate() {
		return startDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));		
	}	
	
	public String getEndDate() {
		return endDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));	}	
		
	
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
	
	public boolean inPast()
	{
		return this.getStartDateTime().isBefore(LocalDateTime.now());
	}
	
	public boolean endInPast()
	{
		return this.getEndDateTime().isBefore(LocalDateTime.now());
	}
	
	public boolean isNow()
	{
		return this.getStartDateTime().isBefore(LocalDateTime.now()) && this.getEndDateTime().isAfter((LocalDateTime.now()));
	}
	
	public boolean registered(Student student)
	{
		return getAttendee(student) != null;
	}
	
	public boolean didAttend(Student student)
	{
		boolean result = false; 
		Attendee attendee = getAttendee(student);
		if (attendee != null)
			result = attendee.didAttend();
		return result;
	}
	
	public int getNumberAttendees()
	{
		return this.getAttendees().size();		
	}	
	
	public boolean isFullyBooked()
	{
		return this.getMaxAttendees() <= this.getNumberAttendees();
	}
	
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
	
/*	
	public int getOptionCost(int optionId)
	{
		int optionCost = 0;

		List <MenuGroup> menuGroups = getMenuGroups();
		for (MenuGroup menuGroup: menuGroups) {											
			MenuOption menuOption = menuGroup.getMenuOption(optionId);
			if (menuOption != null) {					
				optionCost = menuOption.getAdditionalCost();
			}
		}														
					
		return optionCost;
	}
		*/
	
	
	
	public boolean hasOptions()
	{									
		return (sessionMenus != null) && (sessionMenus.size() > 0);
	}	

	
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
	
	
	public List<Session> getOverlappingSessions() {
		return repository.findOverlappingSessions(getSessionId(), getStartDateTime(), getEndDateTime());		
	}	
	
	public List<ResourceStatus> getResourceStatus() {
		Map<Integer, Integer> resourceRequirements = new HashMap<>(); //maps resourceId to quantity
				
		//get list map of all resourceid and quantities needed
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
	
		
	public void clearResources() {
		sessionResources = new HashSet<>();		
		requiredResources = null;				 
	}
	
	public void clearMenu() {
		sessionMenus = new HashSet<>();			
		menuGroups = null; 	
	}	
	
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
	
	public boolean isRecurring() {
		return startDateTime.toLocalDate().compareTo(getRecurrenceSpecification().getEndDate()) !=0; 
	}
	
	public void addIncident(Incident incident) {
		incidents.add(incident);
	}
	
	
	public Incident getIncident(int incidentId) {
		Incident result = null;
		Iterator<Incident> itr = incidents.iterator();
		
		while (result == null && itr.hasNext()) {
			Incident next = itr.next();
			if (next.getIncidentId() == incidentId) {
				result = next;
			}
		}
		return result;
	}	
	
	public void removeIncident(int incidentId) {		
		Incident incident = this.getIncident(incidentId);
		incidents.remove(incident);
		return;
	}	
	
	

	public List<Student> getStudentsForIncident(Incident incident) {
		
		List <Student> students = new ArrayList<>();
		Set<AttendeeIncident> allAttendeeIncidents = incident.getAttendeeIncidents();
		for (AttendeeIncident ai : allAttendeeIncidents) {
			Student s = this.getAttendee(ai.getAttendeeId().getId().intValue()).getStudent();
			students.add(s);
		}
		return students;
		
	}

	
	public void delete() {
		repository.delete(this);
	}	

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
	
	public List<User> getAllUsers() {
		return User.repository.findAllForSession(this.sessionId);
	}
	
}
