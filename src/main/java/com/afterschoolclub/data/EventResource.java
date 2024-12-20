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
	private String type;
	
	public EventResource() {
		super();	
	}
	
	public EventResource(EventResource er) {
		super();
		this.resourceId = er.resourceId;
		this.quantity = er.quantity;
		this.type = er.type;
	}
}
