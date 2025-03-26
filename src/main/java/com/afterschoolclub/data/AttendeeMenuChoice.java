package com.afterschoolclub.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AttendeeMenuChoice {
	@Id
	private int attendeeMenuChoiceId;
	AggregateReference<MenuGroupOption, Integer> menuGroupOptionId;
	
	
	public AttendeeMenuChoice(AggregateReference<MenuGroupOption, Integer> menuGroupOptionId) {
		super();
		this.menuGroupOptionId = menuGroupOptionId;
	}
}
