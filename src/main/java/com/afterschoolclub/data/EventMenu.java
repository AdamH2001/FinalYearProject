package com.afterschoolclub.data;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EventMenu {
	@Id
	private int eventMenuId;
	AggregateReference<MenuGroup, Integer> menuGroupId;	
	
	public EventMenu() {
		super();	
	}
	
	public EventMenu(AggregateReference<MenuGroup, Integer> menuGroupId) {
		super();
		this.menuGroupId = menuGroupId;
	}
	
	public EventMenu(EventMenu em) {
		super();
		this.menuGroupId = em.menuGroupId;

	}
}

