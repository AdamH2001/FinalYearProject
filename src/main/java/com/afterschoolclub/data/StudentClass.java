package com.afterschoolclub.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;

import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Table("class")
public class StudentClass {
	@Id
	private int classId;
	private String name;
	private int yearGroup;

}
