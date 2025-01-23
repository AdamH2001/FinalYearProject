package com.afterschoolclub.data;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Resource {
	public enum Type {
		ROOM, STAFF, EQUIPMENT
	}
	
	@Id
	private int resourceId;
	private String name;
	private String description;
	private int quantity;
	private Type type;
	
}
