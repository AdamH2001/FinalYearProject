package com.afterschoolclub;

import java.util.Iterator;

import java.util.Set;

import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.Filter;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.User;
import com.afterschoolclub.data.FilteredEvent;

import com.afterschoolclub.data.MenuGroupOption;
import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.MenuGroup;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class DisplayHelper {

	
	public String getStudentClass(Student selectedStudent, Student currentStudent, Event event, boolean viewOnly)
	{
		String result = "";
		if ((event.registered(currentStudent) || !event.canAttend(currentStudent)) && !viewOnly) {
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
	
	public String getStudentCheckContainerClass(User user, Student selectedStudent, Student currentStudent, Event event, boolean editing, boolean viewOnly, Filter filter)
	{
		FilteredEvent filterEvent = new FilteredEvent(event, user, currentStudent, filter);
		return filterEvent.getFilterClass();
			
	}	
	
	public String getStudentClass(Student selectedStudent, Student currentStudent, Event event, boolean editing, boolean viewOnly)
	{
		String result = "";

		if (editing) {
			if (event.registered(currentStudent)) {
				if (selectedStudent.equals(currentStudent)) {
					result = "active ";
				}
			}
			else {
				result = "disabled ";
			}			
		}
		else
			result = getStudentClass(selectedStudent, currentStudent, event, viewOnly);
		
		return result;
			
	}
	
	public boolean checkedStudent(Student selectedStudent, Student currentStudent, Event event, boolean editing, boolean viewOnly)
	{
		return 	(!viewOnly && !editing && selectedStudent.equals(currentStudent)) || event.registered(currentStudent);

	}
	
	public String hiddenCheckedStudent(Student selectedStudent, Student currentStudent, Event event, boolean editing, boolean viewOnly)
	{
		String result = "";
		if (checkedStudent(selectedStudent, currentStudent, event, editing, viewOnly))
			result = "on";
		return result;			
	}
	
	
	public boolean checkedOption(MenuGroup menuGroup, int menuOptionId, Student student, Event event, boolean editing, boolean viewOnly)
	{
		boolean result = false; 
		if (menuOptionId == 0) {
			Set<MenuGroupOption> mgos = menuGroup.getMenuGroupOptions();
			boolean chosenSomethingElse = false;
			
			Iterator<MenuGroupOption> mgoIterator = mgos.iterator();
			while (mgoIterator.hasNext() && !chosenSomethingElse) {
				MenuGroupOption mgo = mgoIterator.next();
				Set<MenuOption> menuOptions = mgo.getMenuOptions();	
				Iterator<MenuOption> iterator = menuOptions.iterator();
				while (iterator.hasNext() && !chosenSomethingElse) {
					chosenSomethingElse = student.chosenMenuOptionForEvent(event, iterator.next().getMenuOptionId());
				}		
			}
			
			result = !chosenSomethingElse;
		}
		else {
			result = student.chosenMenuOptionForEvent(event, menuOptionId);
		}
		return result;
	}
	
	
	public String getOptionText(MenuGroup menuGroup, Student student, Event event)
	{
		boolean foundOption = false;
		String optionText = "None";
		
		
		Set<MenuGroupOption> mgos = menuGroup.getMenuGroupOptions();
		
		Iterator<MenuGroupOption> mgoIterator = mgos.iterator();
		while (mgoIterator.hasNext() && !foundOption) {
			MenuGroupOption mgo = mgoIterator.next();
			Set<MenuOption> menuOptions = mgo.getMenuOptions();	
			Iterator<MenuOption> iterator = menuOptions.iterator();
			while (iterator.hasNext() && !foundOption) {
				MenuOption mo = iterator.next();
				foundOption = student.chosenMenuOptionForEvent(event, mo.getMenuOptionId());
				if (foundOption) {
					optionText = mo.getName();
				}
			}		
		}
			
			
		return optionText;
	}
	
	
	
			
}
