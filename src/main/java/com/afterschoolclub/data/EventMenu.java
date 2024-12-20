package com.afterschoolclub.data;
import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EventMenu {
	@Id
	private int eventMenuId;
	private int eventId;
	private int menuGroupId;
}
