package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.RecurrenceSpecification;

public interface RecurrenceSpecificationRepository extends CrudRepository<RecurrenceSpecification, Integer> {
	
	@Query("SELECT DISTINCT rs.* FROM recurrence_specification rs, event e where rs.start_date <> rs.end_date and e.recurrence_specification_id=rs.recurrence_specification_id and e.club_id = :clubId and rs.end_date >= now()")
	List<RecurrenceSpecification> findRegularByClubId(int clubId);	
}
