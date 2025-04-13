package com.afterschoolclub.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

import org.springframework.data.annotation.Id;

import org.springframework.data.relational.core.mapping.Table;

import com.afterschoolclub.data.repository.ClassRepository;

@Getter
@Setter
@ToString
@Table("class")
public class StudentClass {

	public static ClassRepository repository = null;

	
	@Id
	private int classId;
	private String name;
	private int yearGroup;
	
	
	public static Iterable<StudentClass> findAll() {		
		return repository.findAll();
	}	
	
	public static StudentClass findById(int sessionId) {
		Optional<StudentClass> optional = repository.findById(sessionId);
		StudentClass studentclass = null;
		if (optional.isPresent()) {
			studentclass = optional.get();
		}
		return studentclass;
	}		
		

}
