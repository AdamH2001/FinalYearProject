package com.afterschoolclub.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.transaction.annotation.Transactional;

import com.afterschoolclub.data.repository.ResourceRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a Resource   
 */

@Getter
@Setter
@ToString
public class Resource {

	/**
	 * Repository to retrieve and store instances
	 */
	public static ResourceRepository repository = null;

	
	public enum Type {
		LOCATION, STAFF, EQUIPMENT
	}

	
	/**
	 * Primary key of resource
	 */
	@Id
	private int resourceId;
	/**
	 * name of resource
	 */
	private String name = "";
	/**
	 * description for resource
	 */
	private String description = "";
	/**
	 * quanity of resource available in AFTERSCHOOL CLUB
	 */
	private int quantity = 1;
	/**
	 * Keywords used to find resource
	 */
	private String keywords = "";
	/**
	 * type of Resource LOCATION, EQUIPMENT or STAFF
	 */
	private Type type = Type.EQUIPMENT;
	/**
	 * ACTIVE or INACTIVE state of resource
	 */
	private State state = State.ACTIVE;
	/**
	 * capacit of resource e.g. LOCATION has a capacity 
	 */
	private int capacity = 0;
	/**
	 * Foreign key to the User if it is a STAFF resource  
	 */
	AggregateReference<User, Integer> userId;

	
	
	/**
	 * Cache of the peak demand
	 */
	@Transient
	private transient int maxDemand = 0;
	
	
	/**
	 * Return Resource required by s specifc session and of a specific type
	 * @param sessionId - primary key for session
	 * @param type - LOCATION, EQUIPMENT or LOCATION
	 * @return List of Resource
	 */
	public static List<Resource> findBySessionIdType(int sessionId, Type type) {
		return repository.findBySessionIdType(sessionId, type);
	}
	
	/**
	 * Return all resources of a specifc type
	 * @param type - LOCATION, EQUIPMENT or LOCATION
	 * @return
	 */
	public static List<Resource> findByType(Type type) {
		return repository.findByType(type);
	}
	
	/**
	 * Finad all the active resources of a specific type
	 * @param type - LOCATION, EQUIPMENT or LOCATION
	 * @return List of Resource
	 */
	public static List<Resource> findActiveByType(Type type) {
		return repository.findByTypeAndState(type, State.ACTIVE);
	}	
	
	/**
	 * Return specific Resoure
	 * @param resourceId - primary key for Resource
	 * @return Resource or null if not found
	 */
	public static Resource findById(int resourceId) {
		Optional<Resource> o = repository.findById(Integer.valueOf(resourceId)); 
		return o.isPresent() ? o.get() : null;
	}
	
	
	/**
	 * Return all resources
	 * @return List of Resource
	 */
	public static List<Resource> findAll() {
		return  new ArrayList<>((Collection<? extends Resource>) repository.findAll());		
	}	
	
	/**
	 * Return all ACTIVE resources
	 * @return List of Resource
	 */
	public static List<Resource> findAllActive() {
		return repository.findByState(State.ACTIVE);
		
	}
	
	/**
	 * Return all the resources by name and type
	 * @param name - name of the resource to return
	 * @param type - LOCATION, STAFF, EQUIPMENT
	 * @return List of Resource
	 */
	public static Resource findByNameAndType(String name, Type type) {
		List<Resource> nameMatched  = repository.findByNameAndType(name, type);
		Resource result = null; 
		if (nameMatched != null && nameMatched.size() > 0) {
			result = nameMatched.get(0);
		}
		return result;		
	}	
	
	
	/**
	 * Management function to delete any Resource we no longer need
	 */
	public static void cleanUpInactiveResources() {
		List<Resource> allInactive = repository.findByState(State.INACTIVE);
		for (Resource resource : allInactive) {
			try {
				
				if (resource.getType() != Type.STAFF) {
					Resource.deleteById(resource.getResourceId());
				}
			}
			catch (Exception e) {
				
			}
		}
		return;
		
	}
	
	
	
	/**
	 * Delete the specific resouce frpm the repository
	 * @param resourceId - primary key for the resource
	 */
	public static void deleteById(int resourceId) {
		repository.deleteById(resourceId);		
	}		
	
	/**
	 * Get the quantity of this resource
	 * @return quantity 
	 */
	public int getQuantity() {
		int result = quantity;
		if (state == State.INACTIVE) {
			result = 0;
		}
		return result;
	}
	
	/**
	 * @return true if resource is ACTIVE otherwise return false
	 */
	public boolean isActive() {
		return state == State.ACTIVE;
	}

	
	/**
	 * Save this resource to the repository
	 */
	public void save()
	{
		repository.save(this);
	}

	/**
	 * Update the repository with the latest resource details 
	 */
	@Transactional
	public void update()
	{
		repository.update(resourceId, name, description, quantity, type, state, capacity, keywords);
	}

	
	
	
	/**
	 * @return the peak  future  for a specific resource
	 */
	public int getMaxDemand() {
		OverlappingTimeline overlap = new OverlappingTimeline(this);
		maxDemand = overlap.getRequiredResourceQuantity(this.getResourceId());
		return maxDemand;
	}
	
}
