package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.State;

public interface MenuOptionRepository extends CrudRepository<MenuOption, Integer> {

	List<MenuOption> findByState(State state);		

}

