package com.afterschoolclub.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.afterschoolclub.data.repository.ResourceRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Resource {

	public static ResourceRepository repostiory = null;

	
	public enum Type {
		ROOM, STAFF, EQUIPMENT
	}
	
	public enum State {
		ACTIVE, INACTIVE
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
	
	@Transient
	private transient int maxDemand = 0;
	
	

	
	
	public static List<Resource> findByEventIdType(int eventId, Type type) {
		return repostiory.findByEventIdType(eventId, type);
	}
	
	public static List<Resource> findByType(Type type) {
		return repostiory.findByType(type);
	}
	
	public static List<Resource> findActiveByType(Type type) {
		return repostiory.findByTypeAndState(type, State.ACTIVE);
	}	
	
	public static Resource findById(int resourceId) {
		Optional<Resource> o = repostiory.findById(Integer.valueOf(resourceId)); 
		return o.isPresent() ? o.get() : null;
	}
	
	
	public static List<Resource> findAll() {
		return  new ArrayList<>((Collection<? extends Resource>) repostiory.findAll());		
	}	
	
	public static List<Resource> findAllActive() {
		return repostiory.findByState(State.ACTIVE);
		
	}
	
	public static void cleanUpInactiveResources() {
		List<Resource> allInactive = repostiory.findByState(State.INACTIVE);
		for (Resource resource : allInactive) {
			try {
				Resource.deleteById(resource.getResourceId());
			}
			catch (Exception e) {
				
			}
		}
		return;
		
	}
	
	
	
	public static void deleteById(int eventId) {
		repostiory.deleteById(eventId);		
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
		repostiory.save(this);
	}
	
	public int getMaxDemand() {
		OverlappingTimeline overlap = new OverlappingTimeline(this);
		maxDemand = overlap.getRequiredResourceQuantity(this.getResourceId());
		return maxDemand;
	}
	
}
