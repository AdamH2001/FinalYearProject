package com.afterschoolclub.data;

import java.util.Map;
import java.util.HashMap;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

import com.afterschoolclub.data.repository.EventRepository;
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

@Getter
@Setter
@ToString
public class Event {
	public static EventRepository repository = null;

	public static List<Event> findForMonth(LocalDate date) {
		LocalDate monthStart = date.withDayOfMonth(1);
		LocalDate nextMonth = date.plusMonths(1);
		return repository.findEventsBetweenDates(monthStart, nextMonth);
	}
	
	public static List<Event> findForDay(LocalDate date) {
		LocalDate dayStart = date;
		LocalDate nextDay = date.plusDays(1);
		return repository.findEventsBetweenDates(dayStart, nextDay);
	}
		
	public static  List<Event> findByResourceId(int resourceId) {
		return repository.findByResourceId(resourceId);
	}		
	
	public static  List<Event> findAllWithIncidents() {
		return repository.findAllWithIncidents();
	}		
	
	public static  List<Event> findAllWithIncidentsForStudent(int studentId) {
		return repository.findAllWithIncidentsForStudent(studentId);
	}		
	
	
	public static  List<Event> findByFutureDemandOnResourceId(int resourceId) {
		return repository.findByFutureDemandOnResourceId(resourceId);
	}	
	
	
	public static Event findById(int eventId) {
		Optional<Event> optional = repository.findById(eventId);
		Event event = null;
		if (optional.isPresent()) {
			event = optional.get();
		}
		return event;
	}	
	
	public static List<Event>  findRecurringEvents(int recurringId) {
		return repository.findByReccurenceSpecificationId(recurringId);		
		
	}	
		
	
	public static void saveAll(List<Event> allEvents) {
		repository.saveAll(allEvents);
		
	}	
	
	
	public static void deleteById(int eventId) {
		repository.deleteById(eventId);		
	}		
	
	
	
	@Id
	private int eventId;
		
	AggregateReference<Club, Integer> clubId;
	AggregateReference<RecurrenceSpecification, Integer> recurrenceSpecificationId;
	
	
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;	
	private int maxAttendees;
	
	
	private String parentNotes="";

	private String administratorNotes="";
	
	@MappedCollection(idColumn = "event_id")
	private Set<EventResource> eventResources = new HashSet<>();
	
	@MappedCollection(idColumn = "event_id")
	private Set<Attendee> attendees = new HashSet<>();

	@MappedCollection(idColumn = "event_id")
	private Set<EventMenu> eventMenus = new HashSet<>();	
	
	@MappedCollection(idColumn = "event_id")
	private Set<Incident> incidents = new HashSet<>();
	
	
	@Transient
	private transient Club eventClub = null; 

	@Transient
	private transient RecurrenceSpecification recurrenceSpecification = null; 	
	
	@Transient
	private transient List<MenuGroup> menuGroups = null; 
	
	@Transient
	private transient List<Resource> requiredResources = null; 
	

	
	public Event() {
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
	
	public Event(AggregateReference<Club, Integer> clubId, LocalDateTime startDateTime,
			LocalDateTime endDateTime, int maxAttendees) {
		super();
		this.clubId= clubId;		
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.maxAttendees = maxAttendees;
		this.eventClub=null;	
		this.parentNotes = "";
		this.administratorNotes = "";
				
		
		
	}
	
	/**
	 *
	 */
	public Event(Event e) {
		super();
		this.clubId = e.clubId;				
		this.startDateTime = e.startDateTime;
		this.endDateTime = e.endDateTime;
		this.maxAttendees = e.maxAttendees;
		this.eventClub = e.eventClub;
		
		for (EventResource er : e.eventResources) {
			this.eventResources.add(new EventResource(er));
		}
		
		for (EventMenu menu : e.eventMenus) {
			this.eventMenus.add(new EventMenu(menu));
		}
		
		this.eventClub = e.eventClub;
		
		this.recurrenceSpecificationId = e.getRecurrenceSpecificationId();
		this.recurrenceSpecification = e.getRecurrenceSpecification();
		this.parentNotes = e.parentNotes;
		this.administratorNotes = e.administratorNotes;
		
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
		if (this.eventClub == null && clubId != null) {
			eventClub = Club.findById(clubId.getId());
		}
		return eventClub;
		
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
			Iterator<EventResource> erIterator = eventResources.iterator();
			while (location == null & erIterator.hasNext()) {
				EventResource er = erIterator.next();
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
			for (EventResource er : eventResources) {
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
			Iterator<EventResource> erIterator = eventResources.iterator();
			while (erIterator.hasNext()) {
				EventResource er = erIterator.next();
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
			Iterator<EventResource> erIterator = eventResources.iterator();
			while (erIterator.hasNext()) {
				EventResource er = erIterator.next();
				if (er.getResourceId().getId().intValue() == r.getResourceId() && r.getType() == Resource.Type.STAFF) {
					staff.add(r);
				}				
			}
		}			
		return staff;
	}
	
	public List<MenuGroup> getMenuGroups() {
		if (menuGroups == null) {
			menuGroups = MenuGroup.findByEventId(eventId);		
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
	
	
	
	
	//TODO why do we do it this way 
	
	public boolean requiresStaff(int resourceId) {
		boolean required = false;
		List<Resource> requiredStaff = getStaff();
		ListIterator<Resource> staffIterator = requiredStaff.listIterator();
		while (staffIterator.hasNext() && !required) {
			required = staffIterator.next().getResourceId() == resourceId;
		}
		return required;
	}
		
	
	public void addResource(EventResource eventResource) {
		this.eventResources.add(eventResource);
	}
	
	public boolean usesResource(Resource resource) {
		boolean result = false;
		
		Iterator<EventResource> erIterator = eventResources.iterator();
		while (erIterator.hasNext() && !result) {
			result = erIterator.next().getResourceId().getId() == resource.getResourceId();
		}		
		
		return result; 
	}
	
	public void addEventMenu(EventMenu menu) {
		this.eventMenus.add(menu);
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
		
		if (this.getMaxAttendees() <= this.getAttendees().size() && !student.isAttendingEvent(this))
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
		return (eventMenus != null) && (eventMenus.size() > 0);
	}	

	
	public List<EventResource> getActiveEquipmentEventResources() {
		List<Resource> equipment = getEquipment();
		List <EventResource> result = new ArrayList<EventResource>();
		Set<EventResource> allResources = getEventResources();
		Iterator <EventResource> iterator = allResources.iterator();
		while (iterator.hasNext()) {
			EventResource er = iterator.next();
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
	
	
	public List<Event> getOverlappingEvents() {
		return repository.findOverlappingEvents(getEventId(), getStartDateTime(), getEndDateTime());		
	}	
	
	public List<ResourceStatus> getResourceStatus() {
		Map<Integer, Integer> resourceRequirements = new HashMap<>(); //maps resourceId to quantity
				
		//get list map of all resourceid and quantities needed
		for (EventResource er : eventResources) {
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
		eventResources = new HashSet<>();		
		requiredResources = null;				 
	}
	
	public void clearMenu() {
		eventMenus = new HashSet<>();			
		menuGroups = null; 	
	}	
	
	public int getRequiredResourceQuantity(int resourceId) {
		int result = 0;
		
		for (EventResource er : eventResources) {
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
	
}
