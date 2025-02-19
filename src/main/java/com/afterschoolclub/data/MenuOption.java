package com.afterschoolclub.data;
import java.text.NumberFormat;
import java.util.Locale;

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
	
	
	public String getFormattedCost() {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);
		return n.format(additionalCost / 100.0);
	}
	
}
