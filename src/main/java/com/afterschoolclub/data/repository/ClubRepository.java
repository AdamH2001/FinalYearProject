package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.State;
/**
 * Repository based on the Club entity
 */
public interface ClubRepository extends CrudRepository<Club, Integer> {
	
	
	/**
	 * Return all the clubs order by title
	 */
	@Query("SELECT * FROM club ORDER BY title")	
	List<Club> findAll();	
	
	/**
	 * Return all the clubs that are in a particular state order by title
	 * @param state - ACTIVE or INACTIVE
	 * @return List of all Clubs matching search criteria
	 */
	@Query("SELECT * FROM club  where state=:state ORDER BY title")	
	List<Club> findByState(State state);	
	
	/**
	 * Return club for a specific title
	 * @param title - title of the club being searched for
	 * @return List of all Clubs matching search criteria
	 */
	@Query("SELECT * FROM club  where title=:title AND state='ACTIVE'")	
	List<Club> findByTitle(String title);	
	
	
	
}
