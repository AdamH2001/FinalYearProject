package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.State;

public interface ClubRepository extends CrudRepository<Club, Integer> {
	List<Club> findAll();
	
	@Query("SELECT * FROM club  where state=:state ORDER BY title")	
	List<Club> findByState(State state);		
}
