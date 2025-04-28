package com.afterschoolclub.data;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a SimpleMenuGroupOption of AfterSchool Club
 *  This is used to simplify the RestInterface and hide the database complexities.  
 */
@Getter
@Setter
@ToString
public class SimpleMenuGroupOption {

	
	/**
	 * Set of fields related to e MenuGroupOption
	 */
	private int menuGroupOptionId = 0;	
	private int menuOptionId = 0;
	private int  menuGroupId = 0;
	private State state = State.ACTIVE;

	/**
	 * Default Constructor
	 */
	public SimpleMenuGroupOption()
	{
		super();					
	}
	
	/**
	 * Creates a SimpleMenuGroipOption from a MenuGroupOption
	 * @param mgo - MenuGroupOption to base this simple object on
	 */
	
	public SimpleMenuGroupOption(MenuGroupOption mgo)
	{
		super();
		this.setMenuGroupId(mgo.getMenuGroupIdAsInt());
		this.setMenuOptionId(mgo.getMenuOptionIdAsInt());
		this.setState(mgo.getState());
		this.setMenuGroupOptionId(mgo.getMenuGroupOptionId());				
	}
	
	/**
	 * Return all the MenuGroupOptions in simple form
	 * @return List of SimpleMenuGroupOptions
	 */
	public static List<SimpleMenuGroupOption> findAll() {
		List<MenuGroupOption> allMGOs = MenuGroupOption.findAll();
		ArrayList<SimpleMenuGroupOption> allSMGOs = new ArrayList<SimpleMenuGroupOption>();
		
		for (MenuGroupOption mgo : allMGOs) {
			allSMGOs.add(mgo.getSimpleMenuGroupOption());
		}
		return  allSMGOs;		
	}	
	
	
}
