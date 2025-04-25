package com.afterschoolclub.data.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Session;

/**
 * Repository based on the Session entity
 */
public interface SessionRepository extends CrudRepository<Session, Integer> {
	/**
	 * Return List of all the sessions
	 */
	List<Session> findAll();
	
	/**
	 * Return all the session start between the start and end dates
	 * @param startDate - start date
	 * @param endDate - end date
	 * @return List of session between the two dates
	 */
	@Query("SELECT * FROM session WHERE start_date_time >= :startDate AND start_date_time < :endDate ORDER BY start_date_time") 
	List<Session> findSessionsBetweenDates(LocalDate startDate, LocalDate endDate);
	
	/**
	 * Find all the overlapping sessions excluding 
	 * @param sessionId - session of one to exclude 
	 * @param startDateTime - start date time of session  
	 * @param endDateTime - end date time of session
	 * @return - All the overlapping sessions with 
	 */
	@Query("SELECT * FROM session WHERE (((start_date_time <= :startDateTime AND end_date_time > :startDateTime) OR (start_date_time >= :startDateTime and start_date_time < :endDateTime)) AND session_id != :sessionId) order by start_date_time")
	List<Session> findOverlappingSessions(int sessionId, LocalDateTime startDateTime, LocalDateTime endDateTime);	
	
	/**
	 * Return list of sessions for a specific Recurrence Specification
	 * @param recurrenceSpecificationId - primary key for recurrence specification
	 * @return List of sessions
	 */
	@Query("SELECT * FROM session WHERE recurrence_specification_id = :recurrenceSpecificationId")
	List<Session> findByReccurenceSpecificationId(int recurrenceSpecificationId);	


	/**
	 * Find all the sessions that use a specified resource
	 * @param resourceId - primary key for resource
	 * @return - List of Sessions 
	 */
	
	@Query("SELECT s.* FROM session s, session_resource er where er.resource_id=:resourceId and er.session_id = s.session_id")
	List<Session> findByResourceId(int resourceId);
	
	
	/**
	 * Find all the session that have future demand on a resource
	 * @param resourceId - primary key for resource
	 * @return - List of Sessions 
	 */
	
	@Query("SELECT s.* FROM session s, session_resource er where er.resource_id=:resourceId and er.session_id = s.session_id and s.start_date_time > NOW() order by s.start_date_time")
	List<Session> findByFutureDemandOnResourceId(int resourceId);
	
	/**
	 * Find all the sessions that have incidents 
	 * @return List of sessions
	 */
	
	@Query("SELECT DISTINCT s.* FROM session s, incident i where i.session_id = s.session_id order by  start_date_time desc")
	List<Session> findAllWithIncidents();
	
	
	/**
	 * Find all the sessions with incidents for a specfic student
	 * @param studentId - primary key for student
	 * @return List of sessions
	 */
	@Query("SELECT DISTINCT  s.* FROM session s, incident i, attendee_incident ai, attendee a  where i.session_id = s.session_id and ai.attendee_id = a.attendee_id and a.session_id = s.session_id and a.student_id = :studentId order by  start_date_time desc")
	List<Session> findAllWithIncidentsForStudent(int studentId);	

	
	
}
