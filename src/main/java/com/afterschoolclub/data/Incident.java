package com.afterschoolclub.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Incident {
	@Id
	private int incidentId;
	AggregateReference<Event, Integer> eventId;
	private String summary;
	
	/**
	 * @param eventId
	 * @param summary
	 */
	public Incident(AggregateReference<Event, Integer> eventId, String summary) {
		super();
		this.eventId = eventId;
		this.summary = summary;
	}
	
}
