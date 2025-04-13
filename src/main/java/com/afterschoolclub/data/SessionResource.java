package com.afterschoolclub.data;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import lombok.Getter; 
import lombok.Setter;
import lombok.ToString;

import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@ToString
public class SessionResource {
	@Id
	private int sessionResourceId;
		
	AggregateReference<Resource, Integer> resourceId;
	AggregateReference<Session, Integer> sessionId;

	private int quantity;
	private boolean perAttendee;
	
	public SessionResource() {
		super();	
	}
	
	public SessionResource(AggregateReference<Resource, Integer> resourceId, int quantity, boolean perAttendee) {
		super();	
		this.resourceId = resourceId;
		this.quantity = quantity;
		this.perAttendee = perAttendee;
	}	
	
	public SessionResource(SessionResource er) {
		super();
		this.resourceId = er.resourceId;
		this.quantity = er.quantity;
		this.perAttendee = er.perAttendee;
	}
	
	public boolean isActive() {
		boolean result = false;
		List<Resource> allActiveResources = Resource.findAllActive();
		Iterator<Resource> itr = allActiveResources.iterator();
		while (!result && itr.hasNext()) {
			Resource nextResource = itr.next();
			result = nextResource.getResourceId() == resourceId.getId().intValue();
		}
		return result;
	}
}
