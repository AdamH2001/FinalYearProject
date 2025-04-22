package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.Resource;
import com.afterschoolclub.data.State;

public interface MenuOptionRepository extends CrudRepository<MenuOption, Integer> {

	List<MenuOption> findByState(State state);		

	@Query("SELECT *  FROM menu_option where name=:name AND state='ACTIVE' order by name")
	List<MenuOption> findByName(String name);		
	
}

