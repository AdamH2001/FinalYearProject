package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;


import com.afterschoolclub.data.User;

public interface UserRepository extends CrudRepository<User, Integer> {

	List<User> findByEmail(String email);
	List<User> findAll();
	
	@Query("SELECT u.* from user u, administrator a,  event_resource er where u.user_id=a.user_id and er.resource_id=a.resource_id and er.event_id = :eventId")
	List<User> findStaffByEventId(int eventId);	
}
