package com.afterschoolclub.data;

import java.util.HashSet;

import java.util.Set;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Attendee {
	@Id
	private int attendeeId;
	AggregateReference<Event, Integer> eventId;
	private int studentId;
	private boolean attended = false;
	@MappedCollection(idColumn = "attendee_id")
	private Set<Incident> incidents = new HashSet<>();
	@MappedCollection(idColumn = "attendee_id")
	private Set<AttendeeMenuChoice> menuChoices = new HashSet<>();
	@Transient
	private transient Student student;
	
	/**
	 * @param eventId
	 * @param studentId
	 */
	public Attendee(AggregateReference<Event, Integer> eventId, int studentId) {
		super();
		this.eventId = eventId;
		this.studentId = studentId;
	}
	
	public void addIncident(Incident incident) {
		incidents.add(incident);
	}
	
	
}
