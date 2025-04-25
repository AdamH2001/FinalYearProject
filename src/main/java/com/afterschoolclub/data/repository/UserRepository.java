package com.afterschoolclub.data.repository;

import java.time.LocalDateTime;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.User;
import com.afterschoolclub.data.State;

/**
 * Repository based on the User entity
 */
public interface UserRepository extends CrudRepository<User, Integer> {

	/**
	 * Method to find all the users by email address
	 * @param email - email address to find users by
	 * @return list of users for specified email
	 */	
	List<User> findByEmail(String email);
	
	/**
	 * Method to find all the users 	 
	 * @return list of all users 
	 */		
	List<User> findAll();

	/**
	 * Method to find all the club organisers for a specific session
	 * @param sessionId - primary key for session  	 
	 * @return list of club organiser for the session id
	 */			
	@Query("SELECT u.* from user u, session_resource sr, resource r where u.user_id=r.user_id and sr.resource_id=r.resource_id and sr.session_id = :sessionId ORDER BY u.surname")
	List<User> findStaffBySessionId(int sessionId);	
	
	/**
	 * Find the parent for a given student id
	 * @param studentId - primary key for student
	 * @return list of user that re parent for student
	 */
	@Query("SELECT u.* from user u, parent p, student s where s.student_id = :studentId AND s.parent_id=p.parent_id AND p.user_id=u.user_id")
	List<User> findForStudentId(int studentId);		
	
	/**
	 * Return a list of all users that are staff
	 * @return list of all users that are staff
	 */
	@Query("SELECT u.* FROM resource r, user u where r.type=\"STAFF\" and u.user_id=r.user_id AND u.state='ACTIVE'  ORDER BY u.surname")	
	List<User> findStaff();	
	
	/**
	 * Return a list of all users that are parent
	 * @return list of all users that are parents
	 */
	@Query("SELECT u.* FROM parent p, user u where p.user_id=u.user_id AND u.state='ACTIVE' AND u.admin_verified=true ORDER BY u.surname")	
	List<User> findParents();		
	
	/**
	 * Find list of staff bt state 
	 * @param state - ACTIVE or INACTIVE
	 * @return list of all staff with the supplied state
	 */
	@Query("SELECT u.* FROM resource r, user u where r.type=\"STAFF\" and u.user_id=r.user_id and r.state =:state ORDER BY u.surname")	
	List<User> findStaffByState(State state);		
	
	/**
	 * Find all parents that are in debt
	 * @return list of all users that are in debt
	 */
	@Query("SELECT  u.*  FROM parental_transaction  pt, parent p, user u where balance_type ='CASH' and p.user_id = u.user_id and p.parent_id = pt.parent_id AND u.state='ACTIVE'  GROUP BY pt.parent_id having SUM(pt.amount) < 0")	
	List<User> findInDebt();		
	
	/**
	 * find list of parents that have the specified state and verified status
	 * @param state - ACTIVE or INACTIVE
	 * @param verified - true or false
	 * @returnlist of all users that are parent give the supplied state and verified details
	 */
	@Query("SELECT  u.*  FROM user u, parent p where u.admin_verified=:verified AND p.user_id = u.user_id AND u.state=:state order by date_requested")	
	List<User> findParentByStateVerified(State state, boolean verified);
	
	
	/**
	 *  Update the user with all the supplied fields
	 * @param userId
	 * @param firstName
	 * @param surname
	 * @param email
	 * @param title
	 * @param telephoneNum
	 * @param password
	 * @param validationKey
	 * @param dateRequested
	 * @param emailVerified
	 * @param state
	 * @param adminVerified
	 */
	
	@Modifying
	@Query("Update user u set u.email = :email, first_name = :firstName, surname = :surname, password =:password, validation_key = :validationKey, date_requested = :dateRequested, email_verified = :emailVerified,  title =:title, telephone_num = :telephoneNum, state=:state, admin_verified=:adminVerified where u.user_id = :userId")	
	void update(int userId, String firstName, String surname, String email, String title, String telephoneNum, String password, int validationKey, LocalDateTime dateRequested, boolean emailVerified, State state, boolean adminVerified);

	/**
	 * Update the parent with all the supplied fields
	 * @param parentId
	 * @param altContactName
	 * @param altTelephoneNum
	 * @param overdraftLimit
	 */
	@Modifying
	@Query("Update parent p set p.alt_contact_name = :altContactName, p.alt_telephone_num = :altTelephoneNum, overdraft_limit=:overdraftLimit where p.parent_id = :parentId")	
	void updateParent(int parentId, String altContactName, String altTelephoneNum, int overdraftLimit);

	
		
	/**
	 * Return number of future sessions this parent has booked
	 * @param parentId - primary key for parent
	 * @return
	 */
	@Query("SELECT COUNT(*) FROM student s, attendee a, parent p, session WHERE p.parent_id = :parentId AND s.parent_id = p.parent_id and session.start_date_time > now() AND a.student_id=s.student_id AND a.session_id=session.session_id AND s.state='ACTIVE' AND s.admin_verified=true")
	Integer numFutureSessionsBooked(int parentId);
	
	/**
	 * Return the total overdraft limit for AfterSchool Club
	 * @return total overdraft limit across all parents
	 */
	@Query("SELECT SUM(overdraft_limit) FROM parent p, user u WHERE u.user_id = p.user_id AND u.state='ACTIVE'")
	Integer totalOverdraftLimit();	
	
	/**
	 * Find the user that booked the session 
	 * @param sessionId - primary key for session 
	 * @return - List of users that booked the session
	 */
	@Query("SELECT u.* FROM user u,  session sess, student s, Parent p, Attendee a where sess.session_id=:sessionId and a.session_id = sess.session_id and a.student_id = s.student_id and p.parent_id = s.parent_id and u.user_id = p.user_id")
	List<User> findAllForSession(int sessionId);
}
