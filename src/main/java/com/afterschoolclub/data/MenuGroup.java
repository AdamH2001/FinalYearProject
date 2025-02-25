package com.afterschoolclub.data;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import com.afterschoolclub.data.repository.MenuGroupRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuGroup {

	public static MenuGroupRepository repository = null;

	
	@Id
	private int menuGroupId;
	private String name;
	
	@MappedCollection(idColumn = "menu_group_id")
	private Set<MenuGroupOption> menuGroupOptions = new HashSet<>();
	
	public static Iterable<MenuGroup> findAll() {		
		return repository.findAll();
	}	
	
	public static List<MenuGroup> findByEventId(int eventId) {
		return repository.findByEventId(eventId);		
	}		
	
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
