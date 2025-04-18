package com.afterschoolclub.data.repository;
 
import java.time.LocalDate;
import java.util.List;
 
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.State;
import com.afterschoolclub.data.Student;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
 
public interface StudentRepository extends CrudRepository<Student, Integer> {
 
	@Query("SELECT s.*  FROM student s , attendee a where s.student_id=a.student_id and a.session_id = :sessionId")
	List<Student> findBySessionId(int sessionId);
	

	
	@Query("SELECT s.*  FROM student s , attendee a where s.student_id=a.student_id and a.attendee_id = :attendeeId")	
	List<Student> findByAttendeeId(int attendeeId);	
	
	@Query("SELECT COUNT(*) FROM student s, attendee a, session  where s.student_id = :studentId and session.start_date_time > now() and a.student_id=s.student_id and a.session_id=session.session_id")
	int numFutureSessionsAttending(int studentId);

	@Query("SELECT s.*  FROM student s , attendee a where s.student_id=a.student_id and a.attendee_id = :attendeeId")	
	List<Student> find(int attendeeId);
	
	@Query("SELECT  s.*  FROM student s where s.admin_verified=:verified AND s.state=:state order by date_requested")	
	List<Student> findByStateVerified(State state, boolean verified);
	
	@Modifying
	@Query("Update student s set class_id = :classId, first_name = :firstName, surname = :surname, date_of_birth = :dateOfBirth, state = :state, admin_verified = :adminVerified where s.student_id = :studentId")	
	void update(int studentId, int classId, String firstName, String surname, LocalDate dateOfBirth, State state, boolean adminVerified);
	
	
	
}
