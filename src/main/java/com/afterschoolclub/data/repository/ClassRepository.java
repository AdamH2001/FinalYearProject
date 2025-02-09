package com.afterschoolclub.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.StudentClass;

public interface ClassRepository extends CrudRepository<StudentClass, Integer> {
	List<StudentClass> findAll();
}
