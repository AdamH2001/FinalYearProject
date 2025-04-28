package com.afterschoolclub.data;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;


/**
 *  Class that encapsulates the data and operations for a MedicalNote   
 */

@Getter
@Setter
@ToString
public class MedicalNote {
	
	public enum Type {
		
		ALLERGY, HEALTH, DIET, CAREPLAN, MEDICATION, OTHER
		
	}
	
	
	/**
	 * Primary Key for MedicalNote
	 */
	@Id
	private int medicalNoteId;
	/**
	 * Type of medical note
	 */
	private Type noteType;
	/**
	 * Text describing condition
	 */
	private String note;
	/**
	 * Foreign key to Student
	 */
	AggregateReference<Student, Integer> studentId;

	
	/**
	 * Contructor 
	 * @param noteType - type of Note 
	 * @param note - Text describing condition
	 */

	public MedicalNote(Type noteType, String note) {
		super();
		this.noteType = noteType;
		this.note = note;
	}
}
