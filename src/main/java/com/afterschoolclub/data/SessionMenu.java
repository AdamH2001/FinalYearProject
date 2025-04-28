package com.afterschoolclub.data;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a SessionMenu of AfterSchool Club  
 */

@Getter
@Setter
@ToString
public class SessionMenu {
	@Id
	private int sessionMenuId;
	AggregateReference<MenuGroup, Integer> menuGroupId;	
	AggregateReference<Session, Integer> sessionId;	
	
	
	public SessionMenu() {
		super();	
	}
	
	public SessionMenu(AggregateReference<MenuGroup, Integer> menuGroupId) {
		super();
		this.menuGroupId = menuGroupId;
	}
	
	public SessionMenu(SessionMenu em) {
		super();
		this.menuGroupId = em.menuGroupId;

	}
	
	public int getMenuGroupIdAsInt() {
		return menuGroupId.getId().intValue();
	}	
	
	
}

