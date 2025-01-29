package com.afterschoolclub.data.respository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Resource;

public interface ResourceRepository extends CrudRepository<Resource, Integer> {

	
	List<Resource> findByType(Resource.Type type);
	
	@Query("SELECT r.*  FROM resource r , event_resource er where r.resource_id=er.resource_id and er.event_id = :eventId")
	List<Resource> findByEventId(int eventId);	

	@Query("SELECT r.*  FROM resource r , event_resource er where r.resource_id=er.resource_id and er.event_id = :eventId and r.type=:type")
	List<Resource> findByEventIdType(int eventId, Resource.Type type);	
}
