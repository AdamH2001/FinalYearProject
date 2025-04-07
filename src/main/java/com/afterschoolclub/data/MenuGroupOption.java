package com.afterschoolclub.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import com.afterschoolclub.data.repository.MenuGroupOptionRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuGroupOption {


	@Id
	private int menuGroupOptionId;
	
	AggregateReference<MenuOption, Integer> menuOptionId;
	AggregateReference<MenuGroup, Integer> menuGroupId;
	
	
	private State state = State.ACTIVE;
	
	@Transient
	private transient MenuOption menuOption = null; 
	
	static public MenuGroupOptionRepository repository;
	
		
	public MenuOption getMenuOption() {		
		if (menuOption == null) {
			menuOption = MenuOption.findById(menuOptionId.getId().intValue());
		}
		return menuOption;
		
	}	
	
	public MenuGroupOption()
	{
		super();
					
	}
	
	public MenuGroupOption(SimpleMenuGroupOption smgo)
	{
		super();
		this.setMenuGroupId(AggregateReference.to(smgo.getMenuGroupId()));
		this.setMenuOptionId(AggregateReference.to(smgo.getMenuOptionId()));
		this.setState(smgo.getState());
		this.setMenuGroupOptionId(smgo.getMenuGroupOptionId());				
	}
	
	public SimpleMenuGroupOption getSimpleMenuGroupOption() {
		return new SimpleMenuGroupOption(this);
	}

	public static List<MenuGroupOption> findAll() {
		return  new ArrayList<>((Collection<? extends MenuGroupOption>) repository.findAll());		
	}	
	
	public static MenuGroupOption findById(int id) {
		Optional<MenuGroupOption> o = repository.findById(Integer.valueOf(id)); 
		return o.isPresent() ? o.get() : null;
	}
	
	
	public boolean isActive() {
		return state == State.ACTIVE;
	}
	
	public void save()
	{
		repository.save(this);
	}
	
	public int getMenuOptionIdAsInt() {
		return menuOptionId.getId().intValue();
	}
	
	public int getMenuGroupIdAsInt() {
		return menuGroupId.getId().intValue();
	}	
	
	
}
