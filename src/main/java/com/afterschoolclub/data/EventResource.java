package com.afterschoolclub.data;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import lombok.Getter; 
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EventResource {
	@Id
	private int eventResourceId;
		
	AggregateReference<Resource, Integer> resourceId;
	private int quantity;
	private boolean perAttendee;
	
	public EventResource() {
		super();	
	}
	
	public EventResource(AggregateReference<Resource, Integer> resourceId, int quantity, boolean perAttendee) {
		super();	
		this.resourceId = resourceId;
		this.quantity = quantity;
		this.perAttendee = perAttendee;
	}	
	
	public EventResource(EventResource er) {
		super();
		this.resourceId = er.resourceId;
		this.quantity = er.quantity;
		this.perAttendee = er.perAttendee;
	}
}
