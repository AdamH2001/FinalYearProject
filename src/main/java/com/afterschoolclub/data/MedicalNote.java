package com.afterschoolclub.data;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;



@Getter
@Setter
@ToString
public class MedicalNote {
	@Id
	private int medicalNoteId;
	private String noteType;
	private String note;
	
	/**
	 * @param noteType
	 * @param note
	 */
	public MedicalNote(String noteType, String note) {
		super();
		this.noteType = noteType;
		this.note = note;
	}
}
