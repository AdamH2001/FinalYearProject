package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.MenuGroup;


public interface MenuGroupRepository extends CrudRepository<MenuGroup, Integer> {

	@Query("SELECT mg.* from Menu_Group mg, Event_Menu em where mg.menu_group_id = em.menu_group_id and em.event_id = :eventId")
	List<MenuGroup> findByEventId(int eventId);	
}
