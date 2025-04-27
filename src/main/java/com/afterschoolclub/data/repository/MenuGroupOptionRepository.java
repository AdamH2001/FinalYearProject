package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.MenuGroupOption;
import com.afterschoolclub.data.State;
/**
 * Repository based on the MenuGroupOptons entity
 */
public interface MenuGroupOptionRepository extends CrudRepository<MenuGroupOption, Integer> {

	/**
	 * Return all the MenuGroup options with a given state
	 * @param state - state ACTIVE or INACTIVE
	 * @return List of MenuGroupOption that match search criteria
	 */
	List<MenuGroupOption> findByState(State state);		

}

