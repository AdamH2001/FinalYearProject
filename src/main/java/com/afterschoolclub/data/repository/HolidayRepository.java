package com.afterschoolclub.data.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Holiday;

public interface HolidayRepository extends CrudRepository<Holiday, Integer> {
	List<Holiday> findAll();
	
	@Query("SELECT * FROM holiday WHERE start_date <= :date AND end_date >= :date") 
	List<Holiday> findByDate(LocalDate date);	
}
