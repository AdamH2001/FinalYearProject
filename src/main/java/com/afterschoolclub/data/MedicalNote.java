package com.afterschoolclub.data;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;



@Getter
@Setter
@ToString
public class MedicalNote {
	
	public enum Type {
		
		ALLERGY, HEALTH, DIET, CAREPLAN, MEDICATION, OTHER
		
	}
	
	
	@Id
	private int medicalNoteId;
	private Type noteType;
	private String note;
	
	/**
	 * @param noteType
	 * @param note
	 */
	public MedicalNote(Type noteType, String note) {
		super();
		this.noteType = noteType;
		this.note = note;
	}
}
