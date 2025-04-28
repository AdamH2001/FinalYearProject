package com.afterschoolclub.data;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.annotation.Id;

import com.afterschoolclub.data.repository.MenuOptionRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a MenuOption   
 */

@Getter
@Setter
@ToString
public class MenuOption {

	/**
	 * Repository to retrieve and store instances
	 */
	public static MenuOptionRepository repository = null;
	
	/**
	 * Primary key for MenuOption
	 */
	@Id
	private int menuOptionId;
	/**
	 * Name of MenuOption
	 */
	private String name;
	/**
	 * Descripion of MenuOption
	 */
	private String description;
	/**
	 * additional cost in pennies
	 */
	private int additionalCost;
	/**
	 * any information that may cause issues with specific allergies
	 */
	private String allergyInformation;
	

	/**
	 * ACTIVE or INACTIVE 
	 */
	private State state = State.ACTIVE;
	
	/**
	 * Return the MenuOption given a name
	 * @param name
	 * @return MenuOption
	 */
	public static MenuOption findByName(String name) {
		List<MenuOption> nameMatched  = repository.findByName(name);
		MenuOption result = null; 
		if (nameMatched != null && nameMatched.size() > 0) {
			result = nameMatched.get(0);
		}
		return result;		
	}	
	


	/**
	 * Return all MenuOptions
	 * @return List of MenuOption
	 */
	public static Iterable<MenuOption> findAll() {		
		return repository.findAll();
	}	
	
	/**
	 * Return all MenuOptions by state
	 * @param state - ACTIVE or INACTIVE
	 * @return List of MenuOption
	 */
	public static Iterable<MenuOption> findByState(State state) {		
		return repository.findByState(state);
	}	
		
	/**
	 * return specific MenuOption 
	 * @param id - primary key of MenuOption
	 * @return
	 */
	public static MenuOption findById(int id) {
		Optional<MenuOption> o = repository.findById(Integer.valueOf(id)); 
		return o.isPresent() ? o.get() : null;
	}
	
	/**
	 * Save this MenuOption to the repository
	 */
	public void save()
	{
		repository.save(this);
	}	
	
	
	/**
	 * @return true if is ACTIVE otherwise return false
	 */
	public boolean isActive() {
		return state == State.ACTIVE;
	}	
	

	/**
	 * Return display able cost for the MenuOption
	 * @return
	 */
	public String getFormattedCost() {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);
		return n.format(additionalCost / 100.0);
	}	
		
		
}
