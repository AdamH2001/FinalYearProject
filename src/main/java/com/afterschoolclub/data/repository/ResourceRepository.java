package com.afterschoolclub.data.repository;

import java.util.List;
import com.afterschoolclub.data.State;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Resource;
/**
 * Repository based on the Session entity
 */
public interface ResourceRepository extends CrudRepository<Resource, Integer> {


	/**
	 * Find list of resources by type 
	 * @param type of resource (LOCATION, STAFF EQUIPMENT) 
	 * @return List of Resources that match the criteria
	 */
	@Query("SELECT *  FROM resource where type=:type order by name")
	List<Resource> findByType(Resource.Type type);
	
	/**
	 * Find the resource used by a particular session
	 * @param sessionId - primary key for the session
	 * @return List of Resources that match the criteria
	 */
	
	@Query("SELECT r.*  FROM resource r , session_resource sr where r.resource_id=sr.resource_id and sr.session_id = :sessionId order by name")
	List<Resource> findBySessionId(int sessionId);	

	/**
	 * Find the specific resource used by a particular session and of specific type 
	 * @param sessionId
	 * @param type of resource (LOCATION, STAFF EQUIPMENT) 
	 * @return List of Resources that match the criteria
	 */
	@Query("SELECT r.*  FROM resource r , session_resource sr where r.resource_id=sr.resource_id and sr.session_id = :sessionId and r.type=:type order by name")
	List<Resource> findBySessionIdType(int sessionId, Resource.Type type);	
	
	/**
	 * type of resource (LOCATION, STAFF EQUIPMENT)
	 * @param state - state of resource (ACTIVE or INACTIVE)
	 * @return List of Resources that match the criteria
	 */
	@Query("SELECT *  FROM resource where state=:state and type=:type order by name")
	List<Resource> findByTypeAndState(Resource.Type type, State state);
	
	/**
	 * @param state - state of resource (ACTIVE or INACTIVE)
	 * @return List of Resources that match the criteria
	 */
	@Query("SELECT *  FROM resource where state=:state order by name")
	List<Resource> findByState(State state);		
	
	/**
	 * Return a resource based on name and state 
	 * @param name - name of resource to find
	 * @param type - type of resource to find (LOCATION, STAFF EQUIPMENT)
	 * @return List of Resources that match the criteria
	 */
	@Query("SELECT *  FROM resource where type=:type AND name=:name AND state='ACTIVE' order by name")
	List<Resource> findByNameAndType(String name, Resource.Type type);		

	/**
	 * Update the specific resource details  
	 * @param resourceId - primary keyof resource to be updated
	 * @param name - new name of resource
	 * @param description - new description for resource
	 * @param quantity - new quantity for resource
	 * @param type - type of resource (LOCATION, STAFF EQUIPMENT)
	 * @param state - state of resource (ACTIVE or INACTIVE)
	 * @param capacity - capacity of resource e.g. capacity of classroom
	 * @param keywords - keywords used to find a resource.
	 */
	@Modifying
	@Query("Update resource r set r.name=:name, r.description=:description, r.quantity=:quantity, r.type=:type, state=:state, capacity=:capacity, keywords=:keywords where r.resource_id = :resourceId")	
	void update(int resourceId, String name, String description, int quantity, Resource.Type type, State state, int capacity, String keywords);
		
}
