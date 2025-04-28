package com.afterschoolclub.data;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import lombok.Getter; 
import lombok.Setter;
import lombok.ToString;

import java.util.Iterator;
import java.util.List;


/**
 *  Class that encapsulates the data and operations for a SessionResource of AfterSchool Club  
 */


@Getter
@Setter
@ToString
public class SessionResource {
	/**
	 * Primary key for this entity 
	 */
	@Id
	private int sessionResourceId;
		
	/**
	 *  Primary key for the resource
	 */
	AggregateReference<Resource, Integer> resourceId;
	
	/**
	 * Primary key for the session 
	 */
	AggregateReference<Session, Integer> sessionId;

	private int quantity;
	
	/**
	 * if true quantity is per attend other quantity is a total quantity 
	 */
	private boolean perAttendee;
	
	/**
	 * Default Constructor
	 */
	public SessionResource() {
		super();	
	}
	
	/**
	 * Create a Session resource given the 
	 * @param resourceId - primary key for the resource 
	 * @param quantity - quantity of resoruce requested
	 * @param perAttendee - is the quantity a total or a quantity per Attendee 
	 */
	public SessionResource(AggregateReference<Resource, Integer> resourceId, int quantity, boolean perAttendee) {
		super();	
		this.resourceId = resourceId;
		this.quantity = quantity;
		this.perAttendee = perAttendee;
	}	
	
	/**
	 * Copy Constructor
	 * @param er - SessionResource to copy
	 */
	public SessionResource(SessionResource er) {
		super();
		this.resourceId = er.resourceId;
		this.quantity = er.quantity;
		this.perAttendee = er.perAttendee;
	}
	
	/**
	 * @return true if is ACTIVE or false is is INACTIVE
	 */
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
