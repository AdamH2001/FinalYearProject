package com.afterschoolclub.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

import org.springframework.data.annotation.Id;

import org.springframework.data.relational.core.mapping.Table;

import com.afterschoolclub.data.repository.ClassRepository;

/**
 *  Class that encapsulates the data and operations for a StudentClass of AfterSchool Club  
 */
@Getter
@Setter
@ToString
@Table("class")
public class StudentClass {

	
	/**
	 * Repository to retrieve and store instances
	 */
	public static ClassRepository repository = null;

	
	/**
	 * Primary key for the StudentClass
	 */
	@Id
	private int classId;
	
	/**
	 * Name of the StudentClass
	 */
	private String name;
	
	/**
	 * yearGroup for the StudentClass
	 */
	private int yearGroup;
	
	
	/**
	 * Return all the StudentClasses
	 * @return List of StudentClasses
	 */	
	public static Iterable<StudentClass> findAll() {		
		return repository.findAll();
	}	
	
	/**
	 * Return the a specific studentClass  
	 * @param classId - primary key for StudentClass
	 * @return
	 */
	public static StudentClass findById(int classId) {
		Optional<StudentClass> optional = repository.findById(classId);
		StudentClass studentclass = null;
		if (optional.isPresent()) {
			studentclass = optional.get();
		}
		return studentclass;
	}		
		

}
