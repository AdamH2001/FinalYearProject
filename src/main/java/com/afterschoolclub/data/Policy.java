package com.afterschoolclub.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.afterschoolclub.service.PolicyService;

@Getter
@Setter
@ToString
public class Policy {

	
    public static PolicyService policyService;

	private String name;
	
	
	public Policy(String name) {
		this.name = name;
		
	}
	
	public Policy() {
		super();		
	}
	
	public boolean exists() {
		return policyService.policyExists(getFilename());		
	}
	
	public String getFilename() {
		return getName().replaceAll(" ", "").toLowerCase().concat(".pdf");		
	}
	
	public String getURL() {
		return policyService.getPolicyURL(getFilename());
	}		

}
