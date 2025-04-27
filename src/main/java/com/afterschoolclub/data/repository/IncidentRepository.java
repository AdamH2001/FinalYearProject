package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Incident;
/**
 * Repository based on the Incident entity
 */
public interface IncidentRepository extends CrudRepository<Incident, Integer> {
	
	
	/**
	 * Return a list of all incidents
	 */
	List<Incident> findAll();
	
	/**
	 * Return list of all Incidents for a specific session
	 * @param SessionId - primary key of session
	 * @return List of Incidents that match the search criteria
	 */
	List<Incident> findAllBySessionId(int SessionId);
}
