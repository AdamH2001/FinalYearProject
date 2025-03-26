package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.MenuGroupOption;
import com.afterschoolclub.data.State;

public interface MenuGroupOptionRepository extends CrudRepository<MenuGroupOption, Integer> {

	List<MenuGroupOption> findByState(State state);		

}

