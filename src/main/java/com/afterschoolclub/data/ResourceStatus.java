package com.afterschoolclub.data;

import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResourceStatus {

	int committedDemand;
	int sessionDemand;
	Resource resource;
	
    @ToString.Exclude
	OverlappingTimeline overlapTimeline;
	
	public ResourceStatus(Resource r, int committedDemand, int sessionDemand, OverlappingTimeline overlapTimeline) {		
		this.resource = r;
		this.committedDemand = committedDemand;
		this.sessionDemand = sessionDemand;
		this.overlapTimeline = overlapTimeline;
	}
	
	public boolean isSufficient()
	{
		return getTotalDemand() <= resource.getQuantity();
	}
	
	public String getMessage()
	{
		String result = null; 
		String date = overlapTimeline.getOriginalSession().getStartDateTime().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));	;
		if (!isSufficient()) {
			switch (resource.getType()) {
			case EQUIPMENT:
				if (resource.isActive()) {
					if (resource.getQuantity() <= committedDemand) {
						result = String.format("Requested %d %s on %s but none available. Please choose alternative.", sessionDemand, resource.getName().toLowerCase(), date);
					}
					else {
						result = String.format("Requested %d %s on %s but only %d available. Please choose alternative.", sessionDemand, resource.getName().toLowerCase(), date, resource.getQuantity() - committedDemand);	
					}								
				}
				else {
					result = String.format("Requested %d %s but equipment is no longer available. Please choose alternative.", sessionDemand, resource.getName());
				}
				break;
			case STAFF:
				if (resource.isActive()) {				
					result = String.format("Staff member %s is already booked on %s. Please choose alternative.", resource.getName(), date);
				}
				else {
					result = String.format("Staff member %s is no longer available. Please choose alternative.", resource.getName(), date);
				}
				break;
			case LOCATION:
				if (resource.isActive()) {
					result = String.format("Location %s is already booked on %s. Please choose alternative.", resource.getName(), date);	
				}
				else {
					result = String.format("Location %s is no longer available. Please choose alternative.", resource.getName());
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
	
	public int getTotalDemand() {
		return committedDemand + sessionDemand;
	}

}
