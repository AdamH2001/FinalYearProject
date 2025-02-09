package com.afterschoolclub.data;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Administrator {

	@Id
	public int administratorId;
	@MappedCollection(idColumn = "resource_id")
	private Set<Resource> resources = new HashSet<>();
	
	public Administrator() {
		super();		
	}
	
	public Resource getResourceObject() {
		Resource result = null;
		result = (Resource) this.resources.toArray()[0];
		return result;
	}
	
}
