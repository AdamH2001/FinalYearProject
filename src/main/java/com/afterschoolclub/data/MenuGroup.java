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

/**
 *  Class that encapsulates the data and operations for a MenuGroup   
 */

@Getter
@Setter
@ToString
public class MenuGroup {

	/**
	 * Repository to retrieve and store instances
     */	
	public static MenuGroupRepository repository = null;
	
	/**
	 * Primary Key for MenuGroup
	 */
	@Id
	private int menuGroupId;
	/**
	 * Name of MenuGroup
	 */
	private String name;
	
	/**
	 * ACTIVE or INACTIVE 
	 */
	private State state = State.ACTIVE;
	

	/**
	 * Set of MenuGroupOptions
	 */
	@MappedCollection(idColumn = "menu_group_id")
	private Set<MenuGroupOption> menuGroupOptions = new HashSet<>();
	
	
	/**
	 * Return MenuGroup By Name 
	 * @param name - name of menu group
	 * @return MenuGrou or NULL of cannot find matching name
	 */
	public static MenuGroup findByName(String name) {
		List<MenuGroup> nameMatched  = repository.findByName(name);
		MenuGroup result = null; 
		if (nameMatched != null && nameMatched.size() > 0) {
			result = nameMatched.get(0);
		}
		return result;		
	}	
	
	/**
	 * Return all MenuGroup of a particular state
	 * @return List  of MenuGroup
	 */
	public static Iterable<MenuGroup> findAll() {		
		return repository.findAll();
	}	
	
	
	/**
	 * Return all MenuGroup of a particular state
	 * @param state - ACTIVE or INACTIVE
	 * @return List  of MenuGroup
	 */
	public static List<MenuGroup> findByState(State state) {		
		return repository.findByState(state);
	}	
	
		
	
	/**
	 * Return all MenuGroup for a Session
	 * @param sessionId - primary key for Session
	 * @return List of MenuGroup
	 */
	public static List<MenuGroup> findBySessionId(int sessionId) {
		return repository.findBySessionId(sessionId);		
	}	
	
	/**
	 * Return specific MenuGroup
	 * @param menuGroupId - primary key for MenuGroup
	 * @return MenuGroup
	 */
	public static MenuGroup findById(int menuGroupId) {
		Optional<MenuGroup> optional = repository.findById(menuGroupId);
		MenuGroup menuGroup = null;
		if (optional.isPresent()) {
			menuGroup = optional.get();
		}
		return menuGroup;
	}	
	
	/**
	 * Delete the MenuGroup from the repository  
	 * @param menuGroupId - primary key for MenuGroup
	 */
	public static void deleteById(int menuGroupId) {
		repository.deleteById(menuGroupId);		
	}		
		
	
	/**
	 * Constructor
	 * @param name - name of MenuGroup
	 */
	public MenuGroup(String name) {
		super();
		this.name = name;
	}
	
	
	/**
	 * Default Constructor 
	 */
	public MenuGroup() {
		super();
	
	}	
		
	/**
	 * Return the MenuOption 
	 * @param menuOptionId - Primary key for MenuOption
	 * @return MenuOption
	 */
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
	
	/**
	 * Return the MenuOption
	 * @param menuGroupOptionId - primary key for menuGroupOption
	 * @return MenuOption
	 */
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
	
	
	
	/**
	 * Return the MenuOptionId
	 * @param menuGroupOptionId - primary key for menuGroupOption
	 * @return MenuOptionId 
	 */
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
	
	/**
	 * Return the MenuGroupOption 
	 * @param menuOptionId - primary key for MenuOption 
	 * @return MenGroupOption
	 */
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
	
	
	
	
	/**
	 * Return list of all MenuOptions for this MenuGroup
	 * @return List of MenuOption
	 */
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
	
	/**
	 * Return all Active MenuGroupOptions for this MenuGroup
	 * @return List of MenuGroupOption
	 */
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
	
	
	/**
	 * Return the chosen menu option given the student and session
	 * @param student - Student
	 * @param session - Session
	 * @return MenuOption
	 */
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
	
	
	/**
	 * Save this MenuGroup to the repository
	 */
	public void save()
	{
		repository.save(this);
	}
	
	/**
	 * Update this MenuGroup in the repository
	 */
	public void update() {
		repository.update(menuGroupId, name, state);
	}
		
}
