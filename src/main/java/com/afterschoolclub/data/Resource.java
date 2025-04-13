package com.afterschoolclub.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.transaction.annotation.Transactional;

import com.afterschoolclub.data.repository.ResourceRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Resource {

	public static ResourceRepository repository = null;

	
	public enum Type {
		LOCATION, STAFF, EQUIPMENT
	}

	
	@Id
	private int resourceId;
	private String name = "";
	private String description = "";
	private int quantity = 1;
	private String keywords = "";
	private Type type = Type.EQUIPMENT;
	private State state = State.ACTIVE;
	private int capacity = 0;
	AggregateReference<User, Integer> userId;

	
	
	@Transient
	private transient int maxDemand = 0;
	
	
	public static List<Resource> findBySessionIdType(int sessionId, Type type) {
		return repository.findBySessionIdType(sessionId, type);
	}
	
	public static List<Resource> findByType(Type type) {
		return repository.findByType(type);
	}
	
	public static List<Resource> findActiveByType(Type type) {
		return repository.findByTypeAndState(type, State.ACTIVE);
	}	
	
	public static Resource findById(int resourceId) {
		Optional<Resource> o = repository.findById(Integer.valueOf(resourceId)); 
		return o.isPresent() ? o.get() : null;
	}
	
	
	public static List<Resource> findAll() {
		return  new ArrayList<>((Collection<? extends Resource>) repository.findAll());		
	}	
	
	public static List<Resource> findAllActive() {
		return repository.findByState(State.ACTIVE);
		
	}
	
	public static void cleanUpInactiveResources() {
		List<Resource> allInactive = repository.findByState(State.INACTIVE);
		for (Resource resource : allInactive) {
			try {
				Resource.deleteById(resource.getResourceId());
			}
			catch (Exception e) {
				
			}
		}
		return;
		
	}
	
	
	
	public static void deleteById(int sessionId) {
		repository.deleteById(sessionId);		
	}		
	
	public int getQuantity() {
		int result = quantity;
		if (state == State.INACTIVE) {
			result = 0;
		}
		return result;
	}
	
	public boolean isActive() {
		return state == State.ACTIVE;
	}

	
	public void save()
	{
		repository.save(this);
	}

	@Transactional
	public void update()
	{
		repository.update(resourceId, name, description, quantity, type, state, capacity, keywords);
	}

	
	
	
	public int getMaxDemand() {
		OverlappingTimeline overlap = new OverlappingTimeline(this);
		maxDemand = overlap.getRequiredResourceQuantity(this.getResourceId());
		return maxDemand;
	}
	
}
