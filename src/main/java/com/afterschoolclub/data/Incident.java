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

@Getter
@Setter
@ToString
public class Incident {
	@Id
	private int incidentId;
	private String summary="";
	
	AggregateReference<Session, Integer> sessionId;
	
	public static IncidentRepository repository = null;


	
	@MappedCollection(idColumn = "incident_id")
	private Set<AttendeeIncident> attendeeIncidents = new HashSet<>();
	
	
	public static List<Incident> findAllBySessionId(int sessionId) {
		return repository.findAllBySessionId(sessionId);
	}
	
	
	public static Incident findById(int userId) {
		Optional<Incident> optional = repository.findById(userId);
		Incident incident = null;
		if (optional.isPresent()) {
			incident = optional.get();
		}
		return incident;		
	}	
	
	
	
	/**
	 * @param sessionId
	 * @param summary
	 */
	public Incident(String summary) {
		super();
		this.summary = summary;
	}
	
	/**
	 * @param sessionId
	 * @param summary
	 */
	public Incident() {
		super();		
	}	
	
	public void addAttendeeIncident(AttendeeIncident incident) {
		attendeeIncidents.add(incident);
	}
	
	public void resetAttendees() {
		attendeeIncidents = new HashSet<>();	
	}
	
	public boolean involves(int attendeeId) {		
        return getAttendeeIncident(attendeeId) != null;
	} 
	
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
	

	
	public AttendeeIncident getAttendeeIncident(Attendee attendee) {
		return getAttendeeIncident(attendee.getAttendeeId());		
	} 	
	
	
	public String getSummaryForAttendee(Attendee attendee) {
		AttendeeIncident attendeeIncident = getAttendeeIncident(attendee);
		return attendeeIncident.getSummary();
	}
	
	
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
	
	@Override
	public int hashCode() {
		return this.getIncidentId();	
	}

	public void save()
	{
		repository.save(this);
	}		
	
}
