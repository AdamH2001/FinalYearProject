package com.afterschoolclub.data;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.transaction.annotation.Transactional;

import com.afterschoolclub.data.repository.SimpleMenuGroupRepository;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("menu_group")
public class SimpleMenuGroup {

	public static SimpleMenuGroupRepository repository = null;

	
	@Id
	private int menuGroupId;
	private String name;
	

	private State state = State.ACTIVE;
	
	
	
	public static SimpleMenuGroup findById(int menuGroupId) {
		Optional<SimpleMenuGroup> o = repository.findById(menuGroupId); 		
		return o.isPresent() ? o.get() : null;
		
	}	
	
	
	public static List<SimpleMenuGroup> findAll() {
		return  new ArrayList<>((Collection<? extends SimpleMenuGroup>) repository.findAll());		
		
	}		
	
	public static List<SimpleMenuGroup> findAllActive() {
		return new ArrayList<>((Collection<? extends SimpleMenuGroup>) repository.findByState(State.ACTIVE));
		
	}		
	

		
	public SimpleMenuGroup() {
		super();
	}	
	
	
	
	@Transactional
	public void save()
	{		
		repository.save(this);
	}
	
	@Transactional
	public void delete()
	{
		if (menuGroupId != 0) {
			repository.deleteById(menuGroupId);			
		}
	}	
	
	
}
