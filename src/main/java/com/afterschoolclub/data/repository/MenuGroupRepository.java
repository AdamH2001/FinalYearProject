package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.State;
/**
 * Repository based on the MenuGroup entity
 */
public interface MenuGroupRepository extends CrudRepository<MenuGroup, Integer> {

	/**
	 * Return the menu groups for a specific session
	 * @param sessionId - primary key of the session 
	 * @return List of MenuGroup matching search criteria
	 */
	@Query("SELECT mg.* from menu_group mg, Session_Menu em where mg.menu_group_id = em.menu_group_id and em.session_id = :sessionId")
	List<MenuGroup> findBySessionId(int sessionId);
	
	/**
	 * Return the MenuGroup that are in a specific state
	 * @param state - state of MenuGroup (ACTIVE or INACTIVE)
	 * @return List of MenuGroup matching search criteria
	 */
	@Query("SELECT *  FROM menu_group where state=:state order by name")
	List<MenuGroup> findByState(State state);	
	
	/**
	 * @param menuGroupId
	 * @param name
	 * @param state
	 */
	@Modifying
	@Query("UPDATE menu_group SET name = :name, state = :state where menu_group_id = :menuGroupId")	
	void update(int menuGroupId, String name, State state);	
	
	/**
	 * Return List of MenuGroup that are active and have a specific name
	 * @param name - name of the menu group to be return
	 * @return List of MenuGroup matching search criteria
	 */
	@Query("SELECT * FROM menu_group where name=:name AND state='ACTIVE' order by name")
	List<MenuGroup> findByName(String name);		
	
}
