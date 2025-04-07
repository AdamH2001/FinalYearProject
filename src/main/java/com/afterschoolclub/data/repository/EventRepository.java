package com.afterschoolclub.data.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Event;

public interface EventRepository extends CrudRepository<Event, Integer> {
	List<Event> findAll();
	
	@Query("SELECT * FROM event WHERE start_date_time >= :startDate AND start_date_time < :endDate ORDER BY start_date_time") 
	List<Event> findEventsBetweenDates(LocalDate startDate, LocalDate endDate);
	
	@Query("SELECT * FROM event WHERE (((start_date_time <= :startDateTime AND end_date_time > :startDateTime) OR (start_date_time >= :startDateTime and start_date_time < :endDateTime)) AND event_id != :eventId) order by start_date_time")
	List<Event> findOverlappingEvents(int eventId, LocalDateTime startDateTime, LocalDateTime endDateTime);	
	
	@Query("SELECT * FROM event WHERE recurrence_specification_id = :recurrenceSpecificationId")
	List<Event> findByReccurenceSpecificationId(int recurrenceSpecificationId);	


	@Query("SELECT e.* FROM event e, event_resource er where er.resource_id=:resourceId and er.event_id = e.event_id")
	List<Event> findByResourceId(int resourceId);
	
	
	@Query("SELECT e.* FROM event e, event_resource er where er.resource_id=:resourceId and er.event_id = e.event_id and e.start_date_time > NOW() order by e.start_date_time")
	List<Event> findByFutureDemandOnResourceId(int resourceId);
	
	@Query("SELECT DISTINCT e.* FROM event e, incident i where i.event_id = e.event_id order by  start_date_time desc")
	List<Event> findAllWithIncidents();
	
	
	@Query("SELECT DISTINCT  e.* FROM after_school_club2.event e, incident i, attendee_incident ai, attendee a  where i.event_id = e.event_id and ai.attendee_id = a.attendee_id and a.event_id = e.event_id and a.student_id = :studentId order by  start_date_time desc")
	List<Event> findAllWithIncidentsForStudent(int studentId);	

	
	
}
