package com.afterschoolclub;

import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.Student;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class DisplayHelper {

	private boolean inDialogue = false;
	
	public String getStudentClass(Student selectedStudent, Student currentStudent, Event event)
	{
		String result = "";
		if (event.registered(currentStudent) || !event.canAttend(currentStudent)) {
			result ="disabled ";
		}
		else if (selectedStudent.equals(currentStudent)) {
			result = "active ";
		}
		else {
			result = "";
		}
		return result;
			
	}
	
	public String getFilterClass()
	{
		String result = "";
		if (inDialogue)
			result = "disabled ";
		
		return result;
			
	}	
	
			
}
