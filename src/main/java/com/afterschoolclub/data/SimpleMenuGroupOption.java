package com.afterschoolclub.data;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SimpleMenuGroupOption {

	private int menuGroupOptionId = 0;	
	private int menuOptionId = 0;
	private int  menuGroupId = 0;
	private State state = State.ACTIVE;

	public SimpleMenuGroupOption()
	{
		super();					
	}
	
	public SimpleMenuGroupOption(MenuGroupOption mgo)
	{
		super();
		this.setMenuGroupId(mgo.getMenuGroupIdAsInt());
		this.setMenuOptionId(mgo.getMenuOptionIdAsInt());
		this.setState(mgo.getState());
		this.setMenuGroupOptionId(mgo.getMenuGroupOptionId());				
	}
	
	public static List<SimpleMenuGroupOption> findAll() {
		List<MenuGroupOption> allMGOs = MenuGroupOption.findAll();
		ArrayList<SimpleMenuGroupOption> allSMGOs = new ArrayList<SimpleMenuGroupOption>();
		
		for (MenuGroupOption mgo : allMGOs) {
			allSMGOs.add(mgo.getSimpleMenuGroupOption());
		}
		return  allSMGOs;		
	}	
	
	
	}
