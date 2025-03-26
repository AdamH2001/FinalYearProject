package com.afterschoolclub.data;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.annotation.Id;

import com.afterschoolclub.data.repository.MenuOptionRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuOption {
	@Id
	private int menuOptionId;
	private String name;
	private String description;
	private int additionalCost;
	private String allergyInformation;
	

	private State state = State.ACTIVE;
	
	
	public String getFormattedCost() {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);
		return n.format(additionalCost / 100.0);
	}	
	
	public static MenuOptionRepository repository = null;

	public static Iterable<MenuOption> findAll() {		
		return repository.findAll();
	}	
	
	public static Iterable<MenuOption> findByState(State state) {		
		return repository.findByState(state);
	}	
		
	public static MenuOption findById(int id) {
		Optional<MenuOption> o = repository.findById(Integer.valueOf(id)); 
		return o.isPresent() ? o.get() : null;
	}
	
	public void save()
	{
		repository.save(this);
	}	
	
	
	public boolean isActive() {
		return state == State.ACTIVE;
	}	
		
}
