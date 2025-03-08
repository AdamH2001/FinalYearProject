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

	private List <TimelineEvent> timelineEvents = new ArrayList<TimelineEvent>(); 
	private Event originalSession;
	
	public enum TimelineEventType {
		START, END
	}
	
	
	class TimelineEvent {
		Event event;
		TimelineEventType type;
		
		
		
		public TimelineEvent(Event event, TimelineEventType type) {
			super();
			this.event = event;
			this.type = type;
		}



		public LocalDateTime getTime() {
			LocalDateTime result = null;
			if (type == TimelineEventType.START) {
				result = event.getStartDateTime();
			}
			else {
				result = event.getEndDateTime();
			}
			return result;
		}
		
		public int getResourceQuantity(int resourceId)
		{
			int result = event.getRequiredResourceQuantity(resourceId);
			if (type == TimelineEventType.END) {
				result *= -1;
			}
			return result;
		}						
	}
	
	public OverlappingTimeline(Event session) {
		originalSession = session; 
		
		List<Event> overlappingSessions = session.getOverlappingEvents();
		
		// create time-line event ordered by time of event
		for (Event overlappingSession : overlappingSessions) {
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
	
	public int getRequiredResourceQuantity(int resourceId) {
		int maxResourceRequired = 0;
		int currentRequirement = 0;
		
		// iterate over time-line events calculating maximum required instances of resource identified by resourceId
		for (TimelineEvent e : timelineEvents) {
			currentRequirement += e.getResourceQuantity(resourceId);
			if (currentRequirement > maxResourceRequired) {
				maxResourceRequired = currentRequirement;
			}
		}
		
		return maxResourceRequired;
	}
								

}
