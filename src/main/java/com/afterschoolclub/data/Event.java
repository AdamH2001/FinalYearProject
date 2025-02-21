package com.afterschoolclub.data;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

import com.afterschoolclub.data.repository.ClubRepository;
import com.afterschoolclub.data.repository.MenuGroupRepository;
import com.afterschoolclub.data.repository.ResourceRepository;

import java.time.LocalDateTime;
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
	public static ClubRepository clubRepository = null;
	public static ResourceRepository resourceRepository = null;
	public static MenuGroupRepository menuGroupRepository = null;

	
	@Id
	private int eventId;
		
	AggregateReference<Club, Integer> clubId;
	
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;	
	private int maxAttendees;
	
	@MappedCollection(idColumn = "event_id")
	private Set<EventResource> eventResources = new HashSet<>();
	
	@MappedCollection(idColumn = "event_id")
	private Set<Attendee> attendees = new HashSet<>();

	@MappedCollection(idColumn = "event_id")
	private Set<EventMenu> eventMenus = new HashSet<>();	
	
	@Transient
	private transient Club eventClub = null; 
	
	@Transient
	private transient Resource room = null;
	
	@Transient
	private transient List<Resource> staff = null; 

	@Transient
	private transient List<MenuGroup> menuGroups = null; 
	

	
	public Event() {
		super();	
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
		
		this.room = e.room;
		this.eventClub = e.eventClub;
		
		// Don't copy attendees or incidents
		
		//TODO will need to copy Event_Menu when get round to that.
		
		
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
			if (compare.getStudentId() == student.getStudentId())
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
		if (this.eventClub == null) {
			Optional<Club> clubFound = clubRepository.findById(clubId.getId());
			if (clubFound.isPresent()) {
				eventClub = clubFound.get();
			}
		}
		return eventClub;
		
	}
	
	
	public Resource getRoom() {
		if (this.room == null) {
			List<Resource> locations  = resourceRepository.findByEventIdType(eventId, Resource.Type.ROOM);
			if (locations.size() > 0) {
				room = locations.get(0);
			}
		}
		return room;
		
	}
	

	public List<Resource> getStaff() {
		if (staff == null) {
			staff = resourceRepository.findByEventIdType(eventId, Resource.Type.STAFF);		
		}
		return staff;
	}
	
	public List<MenuGroup> getMenuGroups() {
		if (menuGroups == null) {
			menuGroups = menuGroupRepository.findByEventId(eventId);		
		}
		return menuGroups;
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
			result = attendee.isAttended();
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
	
	public boolean hasOptions()
	{									
		return (eventMenus != null) && (eventMenus.size() > 0);
	}	
	
}
