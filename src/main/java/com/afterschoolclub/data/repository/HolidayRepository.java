package com.afterschoolclub.data.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Holiday;
/**
 * Repository based on the Holiday entity
 */
public interface HolidayRepository extends CrudRepository<Holiday, Integer> {
	
	/**
	 * Return a list of all Holidays
	 */
	List<Holiday> findAll();
	
	/**
	 * Return the holiday for a given date
	 * @param date - date 
	 * @return List of holidays that match the search criteria
	 */
	
	@Query("SELECT * FROM holiday WHERE start_date <= :date AND end_date >= :date") 
	List<Holiday> findByDate(LocalDate date);	
}
