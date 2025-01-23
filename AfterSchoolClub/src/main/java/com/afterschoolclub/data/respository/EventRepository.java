package com.afterschoolclub.data.respository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Event;

public interface EventRepository extends CrudRepository<Event, Integer> {
	List<Event> findAll();
}
