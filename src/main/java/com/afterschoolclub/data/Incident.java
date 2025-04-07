package com.afterschoolclub.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

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
	
	AggregateReference<Event, Integer> eventId;
	
	@MappedCollection(idColumn = "incident_id")
	private Set<AttendeeIncident> attendeeIncidents = new HashSet<>();
	
		
	
	/**
	 * @param eventId
	 * @param summary
	 */
	public Incident(String summary) {
		super();
		this.summary = summary;
	}
	
	/**
	 * @param eventId
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
        return getAttendeeIncident(attendeeId) == null;
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
	
}
