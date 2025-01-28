package com.afterschoolclub.data.respository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.ParentalTransaction;

public interface EventRepository extends CrudRepository<Event, Integer> {
	List<Event> findAll();
	
	@Query("SELECT * FROM event WHERE start_date_time >= :startDate AND start_date_time < :endDate ORDER BY start_date_time") 
	List<Event> findEventsBetweenDates(LocalDate startDate, LocalDate endDate);	
}
