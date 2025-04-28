package com.afterschoolclub.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.afterschoolclub.service.PolicyService;

/**
 * Simple class that encapsulates behaviour for a policy
 */
@Getter
@Setter
@ToString
public class Policy {

	
    /**
     * Policy serivce that handles the file management for policies 
     */
    public static PolicyService policyService;

	/**
	 * Name of policy
	 */
	private String name;
	
	
	/**
	 * Create a policy given a name
	 * @param name
	 */
	public Policy(String name) {
		this.name = name;
		
	}
	
	/**
	 * Default Constructor
	 */
	public Policy() {
		super();		
	}
	
	/**
	 * @return true if file for policy exists otherwise return false
	 */
	public boolean exists() {
		return policyService.policyExists(getFilename());		
	}
	
	/**
	 * @return filename for the policy
	 */
	public String getFilename() {
		return getName().replaceAll(" ", "").toLowerCase().concat(".pdf");		
	}
	
	/**
	 * @return URL for the policy
	 */
	public String getURL() {
		return policyService.getPolicyURL(getFilename());
	}		

}
