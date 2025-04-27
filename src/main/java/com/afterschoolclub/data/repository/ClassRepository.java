package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.StudentClass;
/**
 * Repository based on the StudentClass entity
 */
public interface ClassRepository extends CrudRepository<StudentClass, Integer> {
	
	/**
	 * Return all List of all the studentCLasses
	 */
	List<StudentClass> findAll();
}
