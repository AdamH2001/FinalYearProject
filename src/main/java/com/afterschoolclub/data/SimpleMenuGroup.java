package com.afterschoolclub.data;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.transaction.annotation.Transactional;

import com.afterschoolclub.data.repository.SimpleMenuGroupRepository;

import lombok.Getter;
import lombok.Setter;

/**
 *  Class that encapsulates the data and operations for a SimpleMenuGroup of AfterSchool Club
 *  This is used to simplify the RestInterface and hide the database complexities.  
 */

@Getter
@Setter
@Table("menu_group")
public class SimpleMenuGroup {

	/**
	 * Repository to retrieve and store instances
	 */	
	public static SimpleMenuGroupRepository repository = null;

	
	/**
	 * Set of fields related to MenuGroup
	 */
	@Id
	private int menuGroupId;
	private String name;
	private State state = State.ACTIVE;
	
	
	
	/**
	 * Return the SimpleMenuGroup from a menuGroupId
	 * @param menuGroupId - primary ey for the menuGroupId
	 * @return SimpleMenuGroup
	 */
	public static SimpleMenuGroup findById(int menuGroupId) {
		Optional<SimpleMenuGroup> o = repository.findById(menuGroupId); 		
		return o.isPresent() ? o.get() : null;
		
	}	
	
	
	/**
	 * Return all the SimpeMenuGroups
	 * @return List of SimpleMenuGroups
	 */
	public static List<SimpleMenuGroup> findAll() {
		return  new ArrayList<>((Collection<? extends SimpleMenuGroup>) repository.findAll());		
		
	}		
	
	/**
	 * Return all the active SimpleMenuGroups
	 * @return List of SimpleMenuGroups
	 */
	public static List<SimpleMenuGroup> findAllActive() {
		return new ArrayList<>((Collection<? extends SimpleMenuGroup>) repository.findByState(State.ACTIVE));
		
	}		
	

		
	/**
	 * Defalt Constructor
	 */
	public SimpleMenuGroup() {
		super();
	}	
	
	
	
	/**
	 * Save this object to repository 
	 */
	@Transactional
	public void save()
	{		
		repository.save(this);
	}
	
	/**
	 * Delete this object from repository
	 */
	@Transactional
	public void delete()
	{
		if (menuGroupId != 0) {
			repository.deleteById(menuGroupId);			
		}
	}	
	
	
}
