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
public class MenuGroup {

	@Id
	private int menuGroupId;
	private String name;
	
	@MappedCollection(idColumn = "menu_group_id")
	private Set<MenuGroupOption> menuGroupOptions = new HashSet<>();
	
	
	/**
	 * @param name
	 */
	public MenuGroup(String name) {
		super();
		this.name = name;
	}
		
	public MenuOption getMenuOption(int menuOptionId) {
		MenuOption result = null;
		Iterator<MenuGroupOption> iterator = menuGroupOptions.iterator();
		while (iterator.hasNext() && result == null) {
			MenuGroupOption current = iterator.next();
			if (current.getMenuOptionId() == menuOptionId) {
				result = current.getMenuOption(); 
			}
		}
		return result;
	}
	
}
