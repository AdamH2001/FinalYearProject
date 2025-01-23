package com.afterschoolclub.data.respository;
 
import java.util.List;
 
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Student;

import org.springframework.data.jdbc.repository.query.Query;
 
public interface StudentRepository extends CrudRepository<Student, Integer> {
 
	@Query("SELECT s.*  FROM student s , attendee a where s.student_id=a.student_id and a.event_id = :eventId")
	List<Student> findByEventId(int eventId);
	@Query("SELECT s.*  FROM student s , attendee a where s.student_id=a.student_id and a.attendee_id = :attendeeId")	
	List<Student> findByAttendeeId(int attendeeId);
}
