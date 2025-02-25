package com.afterschoolclub.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OverlappingTimeline {

	private Event originalEvent;
	private List <TimelineEvent> timelineEvents = new ArrayList<TimelineEvent>(); 
	
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
	
	public OverlappingTimeline(Event event) {
		this.originalEvent = event;		
		List<Event> overlappingEvents = event.getOverlappingEvents();
		
		for (Event e : overlappingEvents) {
			timelineEvents.add(new TimelineEvent(e, TimelineEventType.START));
			timelineEvents.add(new TimelineEvent(e, TimelineEventType.END));			
		}
		timelineEvents.sort(new Comparator<TimelineEvent>() {
					public int compare(TimelineEvent tle1, TimelineEvent tle2) {
						return tle1.getTime().compareTo(tle2.getTime());
					}
				});
		return;		
	}
	
	public int getRequiredResourceQuantity(int resourceId) {
		int result = 0;
		int currentRequirement = 0;
		
		for (TimelineEvent e : timelineEvents) {
			currentRequirement += e.getResourceQuantity(resourceId);
			if (currentRequirement > result) {
				result = currentRequirement;
			}
		}
		
		return result;
	}
								

}
