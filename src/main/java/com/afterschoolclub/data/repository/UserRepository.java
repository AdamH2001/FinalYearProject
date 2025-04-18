package com.afterschoolclub.data.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.User;
import com.afterschoolclub.data.State;
import com.afterschoolclub.data.Student;
public interface UserRepository extends CrudRepository<User, Integer> {

	List<User> findByEmail(String email);
	List<User> findAll();
	
	@Query("SELECT u.* from user u, session_resource sr, resource r where u.user_id=r.user_id and sr.resource_id=r.resource_id and sr.session_id = :sessionId ORDER BY u.surname")
	List<User> findStaffBySessionId(int sessionId);	
	
	@Query("SELECT u.* from user u, parent p, student s where s.student_id = :studentId AND s.parent_id=p.parent_id AND p.user_id=u.user_id")
	List<User> findForStudentId(int studentId);		
	
	@Query("SELECT u.* FROM resource r, user u where r.type=\"STAFF\" and u.user_id=r.user_id AND u.state='ACTIVE'  ORDER BY u.surname")	
	List<User> findStaff();	
	
	@Query("SELECT u.* FROM parent p, user u where p.user_id=u.user_id AND u.state='ACTIVE' AND u.admin_verified=true ORDER BY u.surname")	
	List<User> findParents();		
	
	@Query("SELECT u.* FROM resource r, user u where r.type=\"STAFF\" and u.user_id=r.user_id and r.state =:state ORDER BY u.surname")	
	List<User> findStaffByState(State state);		
	
	@Query("SELECT  u.*  FROM parental_transaction  pt, parent p, user u where balance_type ='CASH' and p.user_id = u.user_id and p.parent_id = pt.parent_id AND u.state='ACTIVE'  GROUP BY pt.parent_id having SUM(pt.amount) < 0")	
	List<User> findInDebt();		
	
	@Query("SELECT  u.*  FROM user u, parent p where u.admin_verified=:verified AND p.user_id = u.user_id AND u.state=:state order by date_requested")	
	List<User> findParentByStateVerified(State state, boolean verified);
	
	
	@Modifying
	@Query("Update user u set u.email = :email, first_name = :firstName, surname = :surname, password =:password, validation_key = :validationKey, date_requested = :dateRequested, email_verified = :emailVerified,  title =:title, telephone_num = :telephoneNum, state=:state, admin_verified=:adminVerified where u.user_id = :userId")	
	void update(int userId, String firstName, String surname, String email, String title, String telephoneNum, String password, int validationKey, LocalDateTime dateRequested, boolean emailVerified, State state, boolean adminVerified);

	@Modifying
	@Query("Update parent p set p.alt_contact_name = :altContactName, p.alt_telephone_num = :altTelephoneNum, overdraft_limit=:overdraftLimit where p.parent_id = :parentId")	
	void updateParent(int parentId, String altContactName, String altTelephoneNum, int overdraftLimit);

		
	@Query("SELECT COUNT(*) FROM student s, attendee a, parent p, session WHERE p.parent_id = :parentId AND s.parent_id = p.parent_id and session.start_date_time > now() AND a.student_id=s.student_id AND a.session_id=session.session_id AND s.state='ACTIVE' AND s.admin_verified=true")
	Integer numFutureSessionsBooked(int parentId);
	
	@Query("SELECT SUM(overdraft_limit) FROM parent p, user u WHERE u.user_id = p.user_id AND u.state='ACTIVE'")
	Integer totalOverdraftLimit();	
	
	@Query("SELECT u.* FROM user u,  session sess, student s, Parent p, Attendee a where sess.session_id=:sessionId and a.session_id = sess.session_id and a.student_id = s.student_id and p.parent_id = s.parent_id and u.user_id = p.user_id")
	List<User> findAllForSession(int sessionId);
}
