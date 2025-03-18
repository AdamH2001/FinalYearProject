package com.afterschoolclub.data.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Resource;
import com.afterschoolclub.data.User;

public interface UserRepository extends CrudRepository<User, Integer> {

	List<User> findByEmail(String email);
	List<User> findAll();
	
	@Query("SELECT u.* from user u, event_resource er, resource r where u.user_id=r.user_id and er.resource_id=r.resource_id and er.event_id = :eventId ORDER BY u.surname")
	List<User> findStaffByEventId(int eventId);	
	
	@Query("SELECT u.* FROM resource r, user u where r.type=\"STAFF\" and u.user_id=r.user_id ORDER BY u.surname")	
	List<User> findStaff();	
	
	
	@Query("SELECT u.* FROM resource r, user u where r.type=\"STAFF\" and u.user_id=r.user_id and r.state =:state ORDER BY u.surname")	
	List<User> findStaffByState(Resource.State state);		
	
	
	// TODO Need to add title and telephone num at some point
	
	
	
	@Modifying
	@Query("Update user u set u.email = :email, first_name = :firstName, surname = :surname, password =:password, validation_key = :validationKey, date_requested = :dateRequested, email_verified = :emailVerified,  title =:title, telephone_num = :telephoneNum where u.user_id = :userId")	
	void updateUser(int userId, String firstName, String surname, String email, String title, String telephoneNum, String password, int validationKey, LocalDateTime dateRequested, boolean emailVerified);	
}
