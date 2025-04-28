package com.afterschoolclub.data;

import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * class to encapsulate the status of a resource for a specfic session
 */
@Getter
@Setter
@ToString
public class ResourceStatus {

	/**
	 * The already committed demand for the resource
	 */
	int committedDemand;
	/**
	 * The new session demand for this resource
	 */
	int sessionDemand;
	/**
	 * Resource we a re gathering a status for
	 */
	Resource resource;
	
    /**
     * the timeline we are considering 
     */
    @ToString.Exclude
	OverlappingTimeline overlapTimeline;
	
	/**
	 * Create a resource status
	 * @param r - Resource
	 * @param committedDemand - already committed demand
	 * @param sessionDemand - session demand 
	 * @param overlapTimeline - overlapping timeline
	 */
	public ResourceStatus(Resource r, int committedDemand, int sessionDemand, OverlappingTimeline overlapTimeline) {		
		this.resource = r;
		this.committedDemand = committedDemand;
		this.sessionDemand = sessionDemand;
		this.overlapTimeline = overlapTimeline;
	}
	
	/**
	 * @return true if there are sufficient resources otherwise return false
	 */
	public boolean isSufficient()
	{
		return getTotalDemand() <= resource.getQuantity();
	}
	
	/**
	 * @return message for user summarising resource status
	 */
	public String getMessage()
	{
		String result = null; 
		String date = overlapTimeline.getOriginalSession().getStartDateTime().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));	;
		if (!isSufficient()) {
			switch (resource.getType()) {
			case EQUIPMENT:
				if (resource.isActive()) {
					if (resource.getQuantity() <= committedDemand) {
						result = String.format("Requested %d %s on %s but none available - please choose an alternative", sessionDemand, resource.getName().toLowerCase(), date);
					}
					else {
						result = String.format("Requested %d %s on %s but only %d available - please choose an alternative", sessionDemand, resource.getName().toLowerCase(), date, resource.getQuantity() - committedDemand);	
					}								
				}
				else {
					result = String.format("Requested %d %s but equipment is no longer available - please choose an alternative", sessionDemand, resource.getName());
				}
				break;
			case STAFF:
				if (resource.isActive()) {				
					result = String.format("Staff member %s is already booked on %s - please choose an alternative", resource.getName(), date);
				}
				else {
					result = String.format("Staff member %s is no longer available - please choose an alternative", resource.getName(), date);
				}
				break;
			case LOCATION:
				if (resource.isActive()) {
					result = String.format("Location %s is already booked on %s - please choose an alternative", resource.getName(), date);	
				}
				else {
					result = String.format("Location %s is no longer available - please choose an alternative", resource.getName());
				}
								
				break;
			default:
				result = "Resource misconfiguration error.";
			}
		}
		else {
			resource.getName().concat(" is available.");
		}
		return result;	
	}
	
	/**
	 * @return the total demand for the resource
	 */
	public int getTotalDemand() {
		return committedDemand + sessionDemand;
	}

}
