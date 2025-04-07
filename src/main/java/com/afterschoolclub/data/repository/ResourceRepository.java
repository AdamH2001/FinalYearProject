package com.afterschoolclub.data.repository;

import java.util.List;
import com.afterschoolclub.data.State;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Resource;
public interface ResourceRepository extends CrudRepository<Resource, Integer> {


	@Query("SELECT *  FROM resource where type=:type order by name")
	List<Resource> findByType(Resource.Type type);
	
	@Query("SELECT r.*  FROM resource r , event_resource er where r.resource_id=er.resource_id and er.event_id = :eventId order by name")
	List<Resource> findByEventId(int eventId);	

	@Query("SELECT r.*  FROM resource r , event_resource er where r.resource_id=er.resource_id and er.event_id = :eventId and r.type=:type order by name")
	List<Resource> findByEventIdType(int eventId, Resource.Type type);	
	
	@Query("SELECT *  FROM resource where state=:state and type=:type order by name")
	List<Resource> findByTypeAndState(Resource.Type type, State state);
	
	@Query("SELECT *  FROM resource where state=:state order by name")
	List<Resource> findByState(State state);		
	

	@Modifying
	@Query("Update resource r set r.name=:name, r.description=:description, r.quantity=:quantity, r.type=:type, state=:state, capacity=:capacity, keywords=:keywords where r.resource_id = :resourceId")	
	void update(int resourceId, String name, String description, int quantity, Resource.Type type, State state, int capacity, String keywords);
		
}
