package com.afterschoolclub.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResourceStatus {

	int committedDemand;
	int eventDemand;
	Resource resource;
	OverlappingTimeline overlapTimeline;
	
	public ResourceStatus(Resource r, int committedDemand, int eventDemand, OverlappingTimeline overlapTimeline) {		
		this.resource = r;
		this.committedDemand = committedDemand;
		this.eventDemand = eventDemand;
		this.overlapTimeline = overlapTimeline;
	}
	
	public boolean isSufficient()
	{
		return (committedDemand+ eventDemand) <= resource.getQuantity();
	}
	
	public String getMessage()
	{
		String result = null; 
		if (!isSufficient()) {
			switch (resource.getType()) {
			case EQUIPMENT:
				if (resource.getQuantity() <= committedDemand) {
					result = String.format("Requested %d %s but none available.", eventDemand, resource.getName().toLowerCase());
				}
				else {
					result = String.format("Requested %d %s but only %d available.", eventDemand, resource.getName().toLowerCase(), resource.getQuantity() - committedDemand);	
				}								
				break;
			case STAFF:
				result = String.format("Staff member %s is already booked.", resource.getName());				
				break;
			case ROOM:
				result = String.format("Location %s is already booked.", resource.getName());				
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
	

}
