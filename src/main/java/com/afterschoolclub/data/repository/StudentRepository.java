package com.afterschoolclub.data.repository;
 
import java.util.List;
 
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Student;

import org.springframework.data.jdbc.repository.query.Query;
 
public interface StudentRepository extends CrudRepository<Student, Integer> {
 
	@Query("SELECT s.*  FROM student s , attendee a where s.student_id=a.student_id and a.session_id = :sessionId")
	List<Student> findBySessionId(int sessionId);
	
	@Query("SELECT s.*  FROM student s , attendee a where s.student_id=a.student_id and a.attendee_id = :attendeeId")	
	List<Student> findByAttendeeId(int attendeeId);
	
	
	@Query("SELECT COUNT(*) FROM student s, attendee a, session  where s.student_id = :studentId and session.start_date_time > now() and a.student_id=s.student_id and a.session_id=session.session_id")
	int numFutureSessionsAttending(int studentId);

}
