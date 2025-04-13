package com.afterschoolclub.data;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
	
	private State state = State.ACTIVE;
	
	
	@MappedCollection(idColumn = "menu_group_id")
	private Set<MenuGroupOption> menuGroupOptions = new HashSet<>();
	
	public static Iterable<MenuGroup> findAll() {		
		return repository.findAll();
	}	
	
	
	public static Iterable<MenuGroup> findByState(State state) {		
		return repository.findByState(state);
	}	
	
		
	
	public static List<MenuGroup> findBySessionId(int sessionId) {
		return repository.findBySessionId(sessionId);		
	}	
	
	public static MenuGroup findById(int menuGroupId) {
		Optional<MenuGroup> optional = repository.findById(menuGroupId);
		MenuGroup menuGroup = null;
		if (optional.isPresent()) {
			menuGroup = optional.get();
		}
		return menuGroup;
	}	
	
	public static void deleteById(int menuGroupId) {
		repository.deleteById(menuGroupId);		
	}		
		
	
	/**
	 * @param name
	 */
	public MenuGroup(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * @param name
	 */
	public MenuGroup() {
		super();
	
	}	
		
	public MenuOption getMenuOption(int menuOptionId) {
		MenuOption result = null;
		Iterator<MenuGroupOption> iterator = menuGroupOptions.iterator();
		while (iterator.hasNext() && result == null) {
			MenuGroupOption current = iterator.next();
			if (current.getMenuOptionIdAsInt() == menuOptionId) {
				result = current.getMenuOption(); 
			}
		}
		return result;
	}
	
	public MenuOption getMenuOptionFromGroupOptionId(int menuGroupOptionId) {
		MenuOption result = null;
		Iterator<MenuGroupOption> iterator = menuGroupOptions.iterator();
		while (iterator.hasNext() && result == null) {
			MenuGroupOption current = iterator.next();
			if (current.getMenuGroupOptionId() == menuGroupOptionId) {
				result = current.getMenuOption(); 
			}
		}
		return result;
	}
	
	
	
	public int getMenuOptionId(int menuGroupOptionId) {
		int result = 0;
		Iterator<MenuGroupOption> iterator = menuGroupOptions.iterator();
		while (iterator.hasNext() && result == 0) {
			MenuGroupOption current = iterator.next();
			if (current.getMenuGroupOptionId() == menuGroupOptionId) {
				result = current.getMenuOptionIdAsInt(); 
			}
		}
		return result;
	}	
	
	public int getMenuGroupOptionId(int menuOptionId) {
		int result = 0;
		Iterator<MenuGroupOption> iterator = menuGroupOptions.iterator();
		while (iterator.hasNext() && result == 0) {
			MenuGroupOption current = iterator.next();
			if (current.getMenuOptionIdAsInt() == menuOptionId) {
				result = current.getMenuGroupOptionId(); 
			}
		}
		return result;
	}
	
	
	
	
	public List<MenuOption> getAllMenuOptions() {

		List <MenuOption> result = new ArrayList<MenuOption>();
		
		Iterator<MenuGroupOption> iterator = menuGroupOptions.iterator();
		while (iterator.hasNext()) {
			MenuGroupOption current = iterator.next();
			result.add(current.getMenuOption());			
		}
		
		result.sort(new Comparator<MenuOption>() {
			public int compare(MenuOption mo1, MenuOption mo2) {
				return mo1.getName().compareTo(mo2.getName());
			}});
		return result;
	}
	
	public List<MenuGroupOption> getAllActiveMenuGroupOptions() {

		List <MenuGroupOption> result = new ArrayList<MenuGroupOption>();
		
		Iterator<MenuGroupOption> iterator = menuGroupOptions.iterator();
		while (iterator.hasNext()) {
			MenuGroupOption current = iterator.next();
			if (current.isActive() && current.getMenuOption().isActive()) {
				result.add(current);
			}
		}
		
		result.sort(new Comparator<MenuGroupOption>() {
			public int compare(MenuGroupOption mo1, MenuGroupOption mo2) {
				return mo1.getMenuOption().getName().compareTo(mo2.getMenuOption().getName());
			}});
		return result;
	}
	
	
	public MenuOption getChosenMenuOption(Student student, Session session)
	{
		boolean foundOption = false;
		MenuOption result = null;
		
		Set<MenuGroupOption> mgos = getMenuGroupOptions();
		Iterator<MenuGroupOption> mgoIterator = mgos.iterator();
		while (mgoIterator.hasNext() && !foundOption) {		
			MenuGroupOption mgo = mgoIterator.next();
			foundOption = student.chosenMenuOptionForSession(session, mgo.getMenuGroupOptionId());
			if (foundOption) {
				result = mgo.getMenuOption();
			}
		}
		return result;
	}	
	
	
	public void save()
	{
		repository.save(this);
	}
	
	public void update() {
		repository.update(menuGroupId, name, state);
	}
		
}
