package com.afterschoolclub.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OverlappingTimeline {

	private List <TimelineSession> timelineSessions = new ArrayList<TimelineSession>(); 
	private Session originalSession;
	
	public enum TimelineSessionType {
		START, END
	}
	
	
	class TimelineSession {
		Session session;
		TimelineSessionType type;
		
		
		
		public TimelineSession(Session session, TimelineSessionType type) {
			super();
			this.session = session;
			this.type = type;
		}



		public LocalDateTime getTime() {
			LocalDateTime result = null;
			if (type == TimelineSessionType.START) {
				result = session.getStartDateTime();
			}
			else {
				result = session.getEndDateTime();
			}
			return result;
		}
		
		public int getResourceQuantity(int resourceId)
		{
			int result = session.getRequiredResourceQuantity(resourceId);
			if (type == TimelineSessionType.END) {
				result *= -1;
			}
			return result;
		}						
	}
	
	public OverlappingTimeline(Session session) {
		originalSession = session; 
		
		List<Session> overlappingSessions = session.getOverlappingSessions();
		
		// create time-line session ordered by time of session
		for (Session overlappingSession : overlappingSessions) {
			timelineSessions.add(new TimelineSession(overlappingSession, TimelineSessionType.START));
			timelineSessions.add(new TimelineSession(overlappingSession, TimelineSessionType.END));			
		}
		timelineSessions.sort(new Comparator<TimelineSession>() {
					public int compare(TimelineSession tle1, TimelineSession tle2) {
						return tle1.getTime().compareTo(tle2.getTime());
					}
				}); 
		return;		
	}
	
	
	public OverlappingTimeline(Resource resource) {
		
		List<Session> overlappingSessions = Session.findByFutureDemandOnResourceId(resource.getResourceId());
		
		
		// create time-line session ordered by time of session
		for (Session overlappingSession : overlappingSessions) {
			timelineSessions.add(new TimelineSession(overlappingSession, TimelineSessionType.START));
			timelineSessions.add(new TimelineSession(overlappingSession, TimelineSessionType.END));			
		}
		timelineSessions.sort(new Comparator<TimelineSession>() {
					public int compare(TimelineSession tle1, TimelineSession tle2) {
						return tle1.getTime().compareTo(tle2.getTime());
					}
				}); 
		return;		
	}
	
	
	
	public int getRequiredResourceQuantity(int resourceId) {
		int maxResourceRequired = 0;
		int currentRequirement = 0;
		
		// iterate over time-line sessions calculating maximum required instances of resource identified by resourceId
		for (TimelineSession ts : timelineSessions) {
			currentRequirement += ts.getResourceQuantity(resourceId);
			if (currentRequirement > maxResourceRequired) {
				maxResourceRequired = currentRequirement;
			}
		}
		
		return maxResourceRequired;
	}
								

}
