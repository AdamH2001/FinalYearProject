package com.afterschoolclub.data;
import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuOption {
	@Id
	private int menuOptionId;
	private String name;
	private String description;
	private int additionalCost;
	private String allergyInformation;
	
}
