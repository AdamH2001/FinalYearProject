package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.State;
/**
 * Repository based on the MenuOption entity
 */
public interface MenuOptionRepository extends CrudRepository<MenuOption, Integer> {


	/**
	 * Return the active menu options defined by state
	 * @param state state of menu option (ACTIVE or INACTIVE)
	 * @return List of MenuOptions matching the specific criteria
	 */
	@Query("SELECT *  FROM menu_option where state=:state order by name")
	List<MenuOption> findByState(State state);	
	
	/**
	 * Return the active menu option defined by name
	 * @param name - name of the menu option
	 * @return List of MenuOptions matching the specific criteria
	 */
	@Query("SELECT *  FROM menu_option where name=:name AND state='ACTIVE' order by name")
	List<MenuOption> findByName(String name);		
	
}

