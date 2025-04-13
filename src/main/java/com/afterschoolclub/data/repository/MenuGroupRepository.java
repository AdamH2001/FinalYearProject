package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.State;

public interface MenuGroupRepository extends CrudRepository<MenuGroup, Integer> {

	@Query("SELECT mg.* from Menu_Group mg, Session_Menu em where mg.menu_group_id = em.menu_group_id and em.session_id = :sessionId")
	List<MenuGroup> findBySessionId(int sessionId);
	
	List<MenuGroup> findByState(State state);
	
	@Modifying
	@Query("UPDATE Menu_Group SET name = :name, state = :state where menu_group_id = :menuGroupId")	
	void update(int menuGroupId, String name, State state);	
	
	
}
