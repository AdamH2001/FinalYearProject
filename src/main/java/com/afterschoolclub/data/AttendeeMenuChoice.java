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
	AggregateReference<MenuOption, Integer> menuOptionId;
	
	
	public AttendeeMenuChoice(AggregateReference<MenuOption, Integer> menuOptionId) {
		super();
		this.menuOptionId = menuOptionId;
	}
}
