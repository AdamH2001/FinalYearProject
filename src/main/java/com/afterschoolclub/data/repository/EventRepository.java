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
	
	
	@Query("SELECT * FROM event WHERE (((start_date_time <= :startDateTime AND end_date_time > :startDateTime) OR (start_date_time >= :startDateTime and start_date_time < :endDateTime)) AND event_id != :eventId)")
	List<Event> findOverlappingEvents(int eventId, LocalDateTime startDateTime, LocalDateTime endDateTime);	

	
}
