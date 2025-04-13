package com.afterschoolclub.data.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Session;

public interface SessionRepository extends CrudRepository<Session, Integer> {
	List<Session> findAll();
	
	@Query("SELECT * FROM session WHERE start_date_time >= :startDate AND start_date_time < :endDate ORDER BY start_date_time") 
	List<Session> findSessionsBetweenDates(LocalDate startDate, LocalDate endDate);
	
	@Query("SELECT * FROM session WHERE (((start_date_time <= :startDateTime AND end_date_time > :startDateTime) OR (start_date_time >= :startDateTime and start_date_time < :endDateTime)) AND session_id != :sessionId) order by start_date_time")
	List<Session> findOverlappingSessions(int sessionId, LocalDateTime startDateTime, LocalDateTime endDateTime);	
	
	@Query("SELECT * FROM session WHERE recurrence_specification_id = :recurrenceSpecificationId")
	List<Session> findByReccurenceSpecificationId(int recurrenceSpecificationId);	


	@Query("SELECT s.* FROM session s, session_resource er where er.resource_id=:resourceId and er.session_id = s.session_id")
	List<Session> findByResourceId(int resourceId);
	
	
	@Query("SELECT s.* FROM session s, session_resource er where er.resource_id=:resourceId and er.session_id = s.session_id and s.start_date_time > NOW() order by s.start_date_time")
	List<Session> findByFutureDemandOnResourceId(int resourceId);
	
	@Query("SELECT DISTINCT s.* FROM session s, incident i where i.session_id = s.session_id order by  start_date_time desc")
	List<Session> findAllWithIncidents();
	
	
	@Query("SELECT DISTINCT  s.* FROM session s, incident i, attendee_incident ai, attendee a  where i.session_id = s.session_id and ai.attendee_id = a.attendee_id and a.session_id = s.session_id and a.student_id = :studentId order by  start_date_time desc")
	List<Session> findAllWithIncidentsForStudent(int studentId);	

	
	
}
