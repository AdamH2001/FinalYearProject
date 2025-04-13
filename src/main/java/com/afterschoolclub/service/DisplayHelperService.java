package com.afterschoolclub.service;

import org.springframework.stereotype.Service;

import com.afterschoolclub.data.Session;
import com.afterschoolclub.data.Filter;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.User;
import com.afterschoolclub.data.FilteredSession;

import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.MenuGroup;


@Service
public class DisplayHelperService {

	
	public String getStudentClass(Student selectedStudent, Student currentStudent, Session session, boolean viewOnly)
	{
		String result = "";
		if ((session.registered(currentStudent) || !session.canAttend(currentStudent)) && !viewOnly) {
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
	
	public String getStudentCheckContainerClass(User user, Student selectedStudent, Student currentStudent, Session session, boolean editing, boolean viewOnly, Filter filter)
	{
		FilteredSession filterSession = new FilteredSession(session, user, currentStudent, filter);
		return filterSession.getFilterClass();
			
	}	
	
	public String getStudentClass(Student selectedStudent, Student currentStudent, Session session, boolean editing, boolean viewOnly)
	{
		String result = "";

		if (editing) {
			if (session.registered(currentStudent)) {
				if (selectedStudent.equals(currentStudent)) {
					result = "active ";
				}
			}
			else {
				result = "disabled ";
			}			
		}
		else
			result = getStudentClass(selectedStudent, currentStudent, session, viewOnly);
		
		return result;
			
	}

	
	
	public boolean checkedOption(MenuGroup menuGroup, int menuOptionId, Student student, Session session, boolean editing, boolean viewOnly)
	{
		boolean result;
		MenuOption menuOption = menuGroup.getChosenMenuOption(student, session);
		if (menuOption != null) {
			result = menuOption.getMenuOptionId() == menuOptionId;
		}
		else {
			result = menuOptionId==0;
		}		
		return result;		
	}
	
	
	
	public String getOptionText(MenuGroup menuGroup, Student student, Session session)
	{
		MenuOption menuOption = menuGroup.getChosenMenuOption(student, session);		
		
		String optionText = "None";
		if (menuOption != null) {
			optionText = menuOption.getName();
		}
		return optionText;
	}	
	
			
}
