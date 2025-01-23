package com.afterschoolclub.data;
import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuGroup {

	@Id
	private int menuGroupId;
	private String name;
	/**
	 * @param name
	 */
	public MenuGroup(String name) {
		super();
		this.name = name;
	}
	

}
