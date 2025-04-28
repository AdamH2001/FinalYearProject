package com.afterschoolclub.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a AttendeeMenuChoice  
 */

@Getter
@Setter
@ToString
public class AttendeeMenuChoice {
	/**
	 * Primary key for AttendeeMenuChoice
	 */
	@Id
	private int attendeeMenuChoiceId;
	
	/**
	 * Foreign Key to MenuGroupOption 
	 */
	AggregateReference<MenuGroupOption, Integer> menuGroupOptionId;
	
	/**
	 * Foreign key to Attendee
	 */
	AggregateReference<Attendee, Integer> attendeeId;

	
	/**
	 * Constructor 
	 * @param menuGroupOptionId
	 */
	public AttendeeMenuChoice(AggregateReference<MenuGroupOption, Integer> menuGroupOptionId) {
		super();
		this.menuGroupOptionId = menuGroupOptionId;
	}
}
