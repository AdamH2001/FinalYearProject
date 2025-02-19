package com.afterschoolclub.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuGroupOption {


	@Id
	private int menuGroupOptionId;
	private int menuOptionId;
	private int menuGroupId;
	
	
	@MappedCollection(idColumn = "menu_option_id")
	private Set<MenuOption> menuOptions = new HashSet<>();
	
	

	
	public MenuOption getMenuOption() {
		MenuOption result = null;
		Iterator<MenuOption> iterator = menuOptions.iterator();
		while (iterator.hasNext() && result == null) {
			result = iterator.next(); 
		}
		return result;
	}	
}
