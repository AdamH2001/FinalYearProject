package com.afterschoolclub.data;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.afterschoolclub.data.repository.ResourceRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Resource {

	public static ResourceRepository resourceRepository = null;

	
	public enum Type {
		ROOM, STAFF, EQUIPMENT
	}
	
	@Id
	private int resourceId;
	private String name;
	private String description;
	private int quantity;
	private Type type;
	
	public static List<Resource> findByEventIdType(int eventId, Type type) {
		return resourceRepository.findByEventIdType(eventId, type);
	}
	
	public static List<Resource> findByType(Type type) {
		return resourceRepository.findByType(type);
	}
	
	public static Iterable<Resource> findAll() {
		return resourceRepository.findAll();
	}		
	
}
