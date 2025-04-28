package com.afterschoolclub.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Class that enable the calculation of peak resource demand over a timeline of sessions
 */
@Getter
@Setter
@ToString
public class OverlappingTimeline {

	/**
	 * List of TimelineEvents that make of the time points when resource demand changes
	 */
	private List <TimelineEvent> timelineEvents = new ArrayList<TimelineEvent>(); 
	/**
	 * original session 
	 */
	private Session originalSession;
	
	public enum TimelineEventType {
		START, END
	}
	
	
	/**
	 * Intenal class to track each TimelineEvent
	 */
	class TimelineEvent {
		Session session;
		TimelineEventType type;
		
		
		
		/** 
		 * Construct a TimelineEvent for a Session
		 * @param session - Session 
		 * @param type - START or END
		 */
		public TimelineEvent(Session session, TimelineEventType type) {
			super();
			this.session = session;
			this.type = type;
		}



		/**
		 * Return the time of this TimelineEvent 
		 * @return LocalDateTime
		 */
		public LocalDateTime getTime() {
			LocalDateTime result = null;
			if (type == TimelineEventType.START) {
				result = session.getStartDateTime();
			}
			else {
				result = session.getEndDateTime();
			}
			return result;
		}
		
		/** Return quantity of a specific resource demand
		 * @param resourceId - primary key for resource 
		 * @return - quantity
		 */
		public int getResourceQuantity(int resourceId)
		{
			int result = session.getRequiredResourceQuantity(resourceId);
			if (type == TimelineEventType.END) {
				result *= -1;
			}
			return result;
		}						
	}
	
	/**
	 * Construct an overlapping timeline for a session
	 * @param session - Session
	 */
	public OverlappingTimeline(Session session) {
		originalSession = session; 
		
		List<Session> overlappingSessions = session.getOverlappingSessions();
		
		// create time-line session ordered by time of session
		for (Session overlappingSession : overlappingSessions) {
			timelineEvents.add(new TimelineEvent(overlappingSession, TimelineEventType.START));
			timelineEvents.add(new TimelineEvent(overlappingSession, TimelineEventType.END));			
		}
		timelineEvents.sort(new Comparator<TimelineEvent>() {
					public int compare(TimelineEvent tle1, TimelineEvent tle2) {
						return tle1.getTime().compareTo(tle2.getTime());
					}
				}); 
		return;		
	}
	
	
	/**
	 * Construct an overlapping timeline for a resource to determine peak demand for resource
	 * @param resource - Resource
	 */
	public OverlappingTimeline(Resource resource) {
		
		List<Session> overlappingSessions = Session.findByFutureDemandOnResourceId(resource.getResourceId());
		
		
		// create time-line session ordered by time of session
		for (Session overlappingSession : overlappingSessions) {
			timelineEvents.add(new TimelineEvent(overlappingSession, TimelineEventType.START));
			timelineEvents.add(new TimelineEvent(overlappingSession, TimelineEventType.END));			
		}
		timelineEvents.sort(new Comparator<TimelineEvent>() {
					public int compare(TimelineEvent tle1, TimelineEvent tle2) {
						return tle1.getTime().compareTo(tle2.getTime());
					}
				}); 
		return;		
	}
	
	
	
	/**
	 * Return the required quantity of resource for this timeline
	 * @param resourceId
	 * @return peak number of units required 
	 */
	public int getRequiredResourceQuantity(int resourceId) {
		int maxResourceRequired = 0;
		int currentRequirement = 0;
		
		// iterate over time-line sessions calculating maximum required instances of resource identified by resourceId
		for (TimelineEvent ts : timelineEvents) {
			currentRequirement += ts.getResourceQuantity(resourceId);
			if (currentRequirement > maxResourceRequired) {
				maxResourceRequired = currentRequirement;
			}
		}
		
		return maxResourceRequired;
	}
								

}
