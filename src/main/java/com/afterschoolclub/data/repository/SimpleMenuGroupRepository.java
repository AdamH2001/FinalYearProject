package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.SimpleMenuGroup;
import com.afterschoolclub.data.State;

/**
 * Repository based on the Menu Group entity 
 * 
 */
public interface SimpleMenuGroupRepository extends CrudRepository<SimpleMenuGroup, Integer> {

	/**
	 * Reutrn list of Menu Groups for a given session 
	 * @param sessionId - primary ley for sessoin 
	 * @return List of Menu Groups
	 */
	@Query("SELECT mg.* from Menu_Group mg, Session_Menu em where mg.menu_group_id = em.menu_group_id and em.session_id = :sessionId")
	List<SimpleMenuGroup> findBySessionId(int sessionId);
	
	/**
	 * Return the list of Menu Groups determined by state
	 * @param state - ACTIVE or INACTIVE
	 * @return List of Menu Groups
	 */
	List<SimpleMenuGroup> findByState(State state);
	
	/**
	 * Update the the Menu Group based on the supplied inputs
	 * @param menuGroupId
	 * @param name
	 * @param state
	 */
	@Modifying
	@Query("UPDATE Menu_Group SET name = :name, state = :state where menu_group_id = :menuGroupId")	
	void update(int menuGroupId, String name, State state);	
	
	
}
