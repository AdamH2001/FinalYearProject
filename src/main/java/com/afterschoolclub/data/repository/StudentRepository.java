package com.afterschoolclub.data.repository;
 
import java.time.LocalDate;
import java.util.List;
 
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.State;
import com.afterschoolclub.data.Student;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
 
/**
 * Repostiory based on the Student entity
 */
public interface StudentRepository extends CrudRepository<Student, Integer> {
 
	/**
	 * Find all the students attnending a specifc session
	 * @param sessionId - primary key for session
	 * @return list of students attending session
	 */
	@Query("SELECT s.*  FROM student s , attendee a where s.student_id=a.student_id and a.session_id = :sessionId")
	List<Student> findBySessionId(int sessionId);
	

	
	/**
	 * Find the Student for the specific attendee id 
	 * @param attendeeId - primary key for an attendee
	 * @return list of students matching the attendee - will ever by one or none
	 */
	@Query("SELECT s.*  FROM student s , attendee a where s.student_id=a.student_id and a.attendee_id = :attendeeId")	
	List<Student> findByAttendeeId(int attendeeId);	
	
	/**
	 * Return number of sessions in future that student is booked on  
	 * @param studentId - primary key for student
	 * @return - num of sessions booked for future
	 */
	
	
	@Query("SELECT COUNT(*) FROM student s, attendee a, session  where s.student_id = :studentId and session.start_date_time > now() and a.student_id=s.student_id and a.session_id=session.session_id")
	int numFutureSessionsAttending(int studentId);


	
	/**
	 * find list od students by the specifed state and whether they are verified or not
	 * @param state - ACTIVE or INACTIVE
	 * @param verified - true or false
	 * @return - list of students
	 */
	@Query("SELECT  s.*  FROM student s where s.admin_verified=:verified AND s.state=:state order by date_requested")	
	List<Student> findByStateVerified(State state, boolean verified);
	
	/**
	 * Update the student with all the supplied fields
	 * @param studentId
	 * @param classId
	 * @param firstName
	 * @param surname
	 * @param dateOfBirth
	 * @param state
	 * @param adminVerified
	 */
	@Modifying
	@Query("Update student s set class_id = :classId, first_name = :firstName, surname = :surname, date_of_birth = :dateOfBirth, state = :state, admin_verified = :adminVerified where s.student_id = :studentId")	
	void update(int studentId, int classId, String firstName, String surname, LocalDate dateOfBirth, State state, boolean adminVerified);
	
	
	
}
