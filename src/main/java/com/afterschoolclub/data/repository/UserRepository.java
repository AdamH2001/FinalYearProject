package com.afterschoolclub.data.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.User;
import com.afterschoolclub.data.State;
public interface UserRepository extends CrudRepository<User, Integer> {

	List<User> findByEmail(String email);
	List<User> findAll();
	
	@Query("SELECT u.* from user u, event_resource er, resource r where u.user_id=r.user_id and er.resource_id=r.resource_id and er.event_id = :eventId ORDER BY u.surname")
	List<User> findStaffByEventId(int eventId);	
	
	@Query("SELECT u.* FROM resource r, user u where r.type=\"STAFF\" and u.user_id=r.user_id AND u.state='ACTIVE'  ORDER BY u.surname")	
	List<User> findStaff();	
	
	@Query("SELECT u.* FROM parent p, user u where p.user_id=u.user_id AND u.state='ACTIVE'  ORDER BY u.surname")	
	List<User> findParents();		
	
	@Query("SELECT u.* FROM resource r, user u where r.type=\"STAFF\" and u.user_id=r.user_id and r.state =:state ORDER BY u.surname")	
	List<User> findStaffByState(State state);		
	
	@Query("SELECT  u.*  FROM parental_transaction  pt, parent p, user u where balance_type ='CASH' and p.user_id = u.user_id and p.parent_id = pt.parent_id AND u.state='ACTIVE'  GROUP BY pt.parent_id having SUM(pt.amount) < 0")	
	List<User> findUsersInDebt();	
	
	
	
	@Modifying
	@Query("Update user u set u.email = :email, first_name = :firstName, surname = :surname, password =:password, validation_key = :validationKey, date_requested = :dateRequested, email_verified = :emailVerified,  title =:title, telephone_num = :telephoneNum, state=:state where u.user_id = :userId")	
	void update(int userId, String firstName, String surname, String email, String title, String telephoneNum, String password, int validationKey, LocalDateTime dateRequested, boolean emailVerified, State state);

	@Modifying
	@Query("Update parent p set p.alt_contact_name = :altContactName, p.alt_telephone_num = :altTelephoneNum, overdraft_limit=:overdraftLimit where p.parent_id = :parentId")	
	void updateParent(int parentId, String altContactName, String altTelephoneNum, int overdraftLimit);

		
	@Query("SELECT COUNT(*) FROM student s, attendee a, parent p, event e  WHERE p.parent_id = :parentId AND s.parent_id = p.parent_id and e.start_date_time > now() AND a.student_id=s.student_id AND a.event_id=e.event_id")
	int numFutureSessionsBooked(int parentId);
	
	@Query("SELECT SUM(overdraft_limit) FROM parent")
	int totalOverdraftLimit();	
	
}
