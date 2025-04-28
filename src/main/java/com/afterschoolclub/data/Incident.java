package com.afterschoolclub.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

import com.afterschoolclub.data.repository.IncidentRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a Incident   
 */

@Getter
@Setter
@ToString
public class Incident {
	/**
	 * Repository to retrieve and store instances
	 */
	public static IncidentRepository repository = null;

	
	/**
	 * Primary key for Incident 
	 */
	@Id
	private int incidentId;
	/**
	 * Summary of incident for AfterSchoolClub purposes
	 */
	private String summary="";
	
	/**
	 * Foreign key to Session
	 */
	AggregateReference<Session, Integer> sessionId;
	


	
	/**
	 *  Set of AttendeeIncidents
	 */
	@MappedCollection(idColumn = "incident_id")
	private Set<AttendeeIncident> attendeeIncidents = new HashSet<>();
	
	
	/**
	 * Return all Incidents for a specific session
	 * @param sessionId - primary key for session
	 * @return List of Incident
	 */
	public static List<Incident> findAllBySessionId(int sessionId) {
		return repository.findAllBySessionId(sessionId);
	}
	
	
	/**
	 * Return specific Incident
	 * @param incidentId - primary key for Incident
	 * @return Incident
	 */
	public static Incident findById(int incidentId) {
		Optional<Incident> optional = repository.findById(incidentId);
		Incident incident = null;
		if (optional.isPresent()) {
			incident = optional.get();
		}
		return incident;		
	}	
	
	
	
	/**
	 * Constructor 
	 * @param summary - summary of incident
	 */
	public Incident(String summary) {
		super();
		this.summary = summary;
	}
	

	/**
	 * Default Constructor
	 */
	public Incident() {
		super();		
	}	
	
	/**
	 * Add attendeeIncident
	 * @param incident - AttendeeIncident
	 */
	public void addAttendeeIncident(AttendeeIncident incident) {
		attendeeIncidents.add(incident);
	}
	
	/**
	 * Remove all attendees
	 */
	public void resetAttendees() {
		attendeeIncidents = new HashSet<>();	
	}
	
	/**
	 * Determine if involves an Attendee
	 * @param attendeeId - primary key for Attendee
	 * @return tru if involves attendee otherwise return false
	 */
	public boolean involves(int attendeeId) {		
        return getAttendeeIncident(attendeeId) != null;
	} 
	
	/**
	 * Return the AttendeeIncident for specific AttendeeId
	 * @param attendeeId - primary key for Attendee
	 * @return AttendeeIncident
	 */
	public AttendeeIncident getAttendeeIncident(int attendeeId) {
		AttendeeIncident result = null; 
		Iterator<AttendeeIncident> itr = attendeeIncidents.iterator();
		while (result == null && itr.hasNext()) {
			AttendeeIncident next = itr.next();
			if (next.getAttendeeId().getId().intValue() == attendeeId) {
				result = next;
			}
		}
        return result;
	} 
	

	
	/**
	 * Return the AttendeeIncident for specific Attendee
	 * @param attendee - Attendee
	 * @return AttendeeIncident
	 */
	public AttendeeIncident getAttendeeIncident(Attendee attendee) {
		return getAttendeeIncident(attendee.getAttendeeId());		
	} 	
	
	
	/**
	 * Return text of attendee incident
	 * @param attendee - Attendee
	 * @return Notes for attendee
	 */
	public String getSummaryForAttendee(Attendee attendee) {
		AttendeeIncident attendeeIncident = getAttendeeIncident(attendee);
		return attendeeIncident.getSummary();
	}
	
	
	/**
	 * Return all the AttendeeIncidents
	 * @return List of AttendeeIncident
	 */
	public List<AttendeeIncident> getAttendeeIncidents() {
		
		Comparator<AttendeeIncident> comparator = new Comparator<AttendeeIncident>(){
			 
		    @Override
		    public int compare(final AttendeeIncident o1, final AttendeeIncident o2){
		    	return o1.getStudent().compareTo(o2.getStudent());
		    }
		};
		
		List<AttendeeIncident> sorted = new ArrayList<AttendeeIncident>(attendeeIncidents);
		Collections.sort(sorted, comparator);
		return sorted;
	
	} 	
	
	
	
	
	/**
	 * return true if other object has same incidentId otherwise return false
	 */
	@Override	
	public boolean equals(Object o) {
		boolean result = false; 
	    if (this == o) {
	        result = true;
	    } 
	    else if (o == null || getClass() != o.getClass()) {
	        result = false;
	    }
	    else {
	    	Incident incident = (Incident) o;
	    	result = this.getIncidentId() == incident.getIncidentId();
	    }	    
        return result;
	} 
	
	/**
	 * Return hashcode for this incident
	 */
	@Override
	public int hashCode() {
		return this.getIncidentId();	
	}

	/**
	 * Save this MenuOption to the repository
	 */
	public void save()
	{
		repository.save(this);
	}		
	
}
