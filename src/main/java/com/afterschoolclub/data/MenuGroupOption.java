package com.afterschoolclub.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import com.afterschoolclub.data.repository.MenuGroupOptionRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a MenuGroupOption   
 */

@Getter
@Setter
@ToString
public class MenuGroupOption {
	/**
	 * Repository to retrieve and store instances
	 */
	static public MenuGroupOptionRepository repository;
	

	/**
	 * Primary key for MenuGroupOption
	 */
	@Id
	private int menuGroupOptionId;
	
	/**
	 * Foreign key to MenuOption
	 */
	AggregateReference<MenuOption, Integer> menuOptionId;
	/**
	 * Foreign key to MenuGroup
	 */
	AggregateReference<MenuGroup, Integer> menuGroupId;
	
	
	/**
	 * ACTIVE or INACTIVE
	 */
	private State state = State.ACTIVE;
	
	/**
	 * Cached MenuOption to avoid repeatedly retrieving from the repository
	 */
	@Transient
	private transient MenuOption menuOption = null; 
	

		
	/**
	 * Return the associated MenuOption
	 * @return MenuOption
	 */
	public MenuOption getMenuOption() {		
		if (menuOption == null) {
			menuOption = MenuOption.findById(menuOptionId.getId().intValue());
		}
		return menuOption;		
	}	
	
	/**
	 * Default Constructor
	 */
	public MenuGroupOption()
	{
		super();
					
	}
	
	/**
	 * Copy Constructor taking a SimpleMenuGroupOption
	 * @param smgo - SimpleMenuGroupOption
	 */
	public MenuGroupOption(SimpleMenuGroupOption smgo)
	{
		super();
		this.setMenuGroupId(AggregateReference.to(smgo.getMenuGroupId()));
		this.setMenuOptionId(AggregateReference.to(smgo.getMenuOptionId()));
		this.setState(smgo.getState());
		this.setMenuGroupOptionId(smgo.getMenuGroupOptionId());				
	}
	
	/**
	 * Return a Simpe version of object
	 * @return SimpleMenuGroupOption
	 */
	public SimpleMenuGroupOption getSimpleMenuGroupOption() {
		return new SimpleMenuGroupOption(this);
	}

	/**
	 * Return all MenuGroupOption
	 * @return List of MenuGroupOption
	 */
	public static List<MenuGroupOption> findAll() {
		return  new ArrayList<>((Collection<? extends MenuGroupOption>) repository.findAll());		
	}	
	
	/**
	 * Return specific MenuGroupOption
	 * @param id - primary key of MenuGroupOption
	 * @return MenuGroupOption
	 */
	public static MenuGroupOption findById(int id) {
		Optional<MenuGroupOption> o = repository.findById(Integer.valueOf(id)); 
		return o.isPresent() ? o.get() : null;
	}
	
	
	/**
	 * @return true if is ACTIVE otherwise return false
	 */
	public boolean isActive() {
		return state == State.ACTIVE;
	}
	
	/**
	 * Save this MenuGroupOption to the repository
	 */
	public void save()
	{
		repository.save(this);
	}
	
	/**
	 * @return
	 */
	public int getMenuOptionIdAsInt() {
		return menuOptionId.getId().intValue();
	}
	
	/**
	 * @return
	 */
	public int getMenuGroupIdAsInt() {
		return menuGroupId.getId().intValue();
	}	
	
	
}
