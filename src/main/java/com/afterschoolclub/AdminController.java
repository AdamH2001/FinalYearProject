package com.afterschoolclub;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.afterschoolclub.data.Attendee;
import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.EventMenu;
import com.afterschoolclub.data.EventResource;
import com.afterschoolclub.data.Filter;
import com.afterschoolclub.data.Incident;
import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.Resource;
import com.afterschoolclub.data.ResourceStatus;
import com.afterschoolclub.data.Student; 
  
@Controller
@SessionAttributes({"sessionBean"})
public class AdminController {

	@Autowired	
    private MainController mainController;
	
    private final SessionBean sessionBean;	
        
	static Logger logger = LoggerFactory.getLogger(AdminController.class);

	
    @Autowired
    public AdminController(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }    
    
	public void setInDialogue(boolean inDialogue, Model model)
	{
		sessionBean.setInDialogue(inDialogue);
	    model.addAttribute("sessionBean", sessionBean);	    
		return;
	}
	
	public String setupCalendar(Model model)
	{
		return mainController.setupCalendar(model);				
	}    
    
	public String validateIsAdmin(Model model)
	{
		String returnPage = null;
		model.addAttribute("sessionBean", sessionBean);
		
		if (sessionBean.isLoggedOn()) {
			if (!sessionBean.isAdminLoggedOn()) {
				model.addAttribute("flashMessage","You need to be a parent to perform this action.");	
				setInDialogue(false,model);
				returnPage = setupCalendar(model);								
			}
		}
		else {
			model.addAttribute("flashMessage","Please login to perform this action.");
			setInDialogue(false,model);
			returnPage = "home";		
		}
		return returnPage;		
	}
	
	
    
    @PostMapping("/updateAdminFilters")
    public String updateAdminFilters(
            @RequestParam(name="onlyMine", required=false) Boolean onlyMine,
            @RequestParam(name="adminFilter") Integer adminFilter,
    		Model model) 
    {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			setInDialogue(false,model);
			sessionBean.getFilter().setOnlyMineFilter(onlyMine!=null);
			sessionBean.getFilter().setAdminFilter(Filter.AdminFilter.valueOf(adminFilter));
			
				    		    
			returnPage = setupCalendar(model);
		}		
		return returnPage;
    }   


	@PostMapping("/addIncident")
	public String addIncident(@RequestParam(name = "attendee") int attendeeId,
			@RequestParam(name = "summary") String summary, int eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Event event = Event.findById(eventId);						
			if (event!=null) {	
				Attendee attendee = event.getAttendee(attendeeId);
				Incident newIncident = new Incident(AggregateReference.to(event.getEventId()), summary);
				attendee.addIncident(newIncident);
				event.save();
				model.addAttribute("flashMessage", "Incident has been added");
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
			}
			returnPage = setupCalendar(model);					
		}
		return returnPage;
	}    
	

	
	@GetMapping("/createIncident")
	public String createIncident(@RequestParam (name="eventId") int eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Event event = Event.findById(eventId);
			if (event != null) {
				for (Attendee attendee: event.getAttendees()) {
					List<Student> studList = Student.findByAttendeeId(attendee.getAttendeeId());
					
					for (Student student: studList) {
						logger.info("student = {}", student);
						attendee.setStudent(student);
					}					
				}
				model.addAttribute("event",event);
				this.setInDialogue(true,model);
				returnPage = "createincident";			
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				returnPage = setupCalendar(model);	
			}
		}
		return returnPage;
	}	
	
	@GetMapping("/takeRegister")
	public String takeRegister(@RequestParam (name="eventId") int eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			Event event = Event.findById(eventId);
			if (event != null) {
				for (Attendee attendee: event.getAttendees()) {
					List<Student> studList = Student.findByAttendeeId(attendee.getAttendeeId());
					
					for (Student student: studList) {
						logger.info("student = {}", student);
						attendee.setStudent(student);
					}					
				}
				model.addAttribute("event",event);
				this.setInDialogue(true,model);
				returnPage = "takeregister";
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				returnPage = setupCalendar(model);				
			}
		}
		return returnPage;
	
			
	}
	
	@PostMapping("/addRegister")
	public String addRegister(@RequestParam Map<String,String> register, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {			
			Event event = Event.findById(Integer.parseInt(register.get("eventId")));
			if (event!=null) {
				for (Map.Entry<String, String> entry : register.entrySet()) {
			        System.out.println(entry.getKey() + ":" + entry.getValue());
			        if (entry.getKey().startsWith("attendee_")) {
			        	int attendeeId = Integer.parseInt(entry.getKey().substring(9));
			        	Attendee attendee = event.getAttendee(attendeeId);
			        	attendee.setAttended(entry.getValue().equals("on"));
			        }
			    }
				event.save();
				model.addAttribute("flashMessage","Recorded Register");	
			}
			else {
				model.addAttribute("flashMessage","Internal error cannot find event.");
			}
			
			returnPage = setupCalendar(model);
		}
		return returnPage;
		
	}

	
	
	
	@GetMapping("/copyEvent")
	public String copyEvent(@RequestParam (name="eventId") int eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {			
			Event event = Event.findById(eventId);						
			if (event!=null) {
				Event newEvent = new Event(event);				
				model.addAttribute("clubSession",newEvent);								
				model.addAttribute("clubs",Club.findAll());				
				model.addAttribute("locations", Resource.findByType(Resource.Type.ROOM));							
				model.addAttribute("staff", Resource.findByType(Resource.Type.STAFF));				
				model.addAttribute("equipment", Resource.findByType(Resource.Type.EQUIPMENT));							
				model.addAttribute("menus", MenuGroup.findAll());
				returnPage = "createevent";				
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				returnPage = setupCalendar(model);
			}
		}
		return returnPage;			
	}
	
	
	
	/*
	@GetMapping("/copyEventAllWeek")
	public String copyEventAllWeek(@RequestParam (name="next", defaultValue="false") boolean next, @RequestParam (name="eventId") int eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Event event = Event.findById(eventId);						
			if (event != null) {
				LocalDateTime startDateTime = event.getStartDateTime();
				LocalDateTime endDateTime = event.getEndDateTime();
		
				if (next) {
					startDateTime = startDateTime.plusDays(1);
					endDateTime = endDateTime.plusDays(1);
					while (startDateTime.getDayOfWeek().getValue() != 1) {
						startDateTime = startDateTime.plusDays(1);
						endDateTime = endDateTime.plusDays(1);
					}
				}
				else {
					while (startDateTime.getDayOfWeek().getValue() != 1) {
						startDateTime = startDateTime.minusDays(1);
						endDateTime = endDateTime.minusDays(1);
					}
				}
				int copiedEvents = 0;
				while (startDateTime.getDayOfWeek().getValue() >= 1 && startDateTime.getDayOfWeek().getValue() <= 5) {
					if (startDateTime.isAfter(LocalDateTime.now())) {
						if (!startDateTime.equals(event.getStartDateTime())) {
							Event newEvent = new Event(event);
							newEvent.setStartDateTime(startDateTime);
							newEvent.setEndDateTime(endDateTime);
							newEvent.save();
							copiedEvents++;
						}
					}
					startDateTime = startDateTime.plusDays(1);
					endDateTime = endDateTime.plusDays(1);
				}
				String message = "Copied " + Integer.toString(copiedEvents) + " sessions";
				model.addAttribute("flashMessage",message);					
			}
			else {
				model.addAttribute("flashMessage","Link out of date");					
			}
			returnPage = setupCalendar(model);
		}
		return returnPage;
	}
	
	@GetMapping("/copyEventAllMonth")
	public String copyEventAllMonth(@RequestParam (name="next", defaultValue="false") boolean next, @RequestParam (name="eventId") int eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Event event = Event.findById(eventId);						
			if (event != null) {
				LocalDateTime startDateTime = event.getStartDateTime();
				LocalDateTime endDateTime = event.getEndDateTime();
				int currentMonth = startDateTime.getMonthValue();
				if (next) {
					while (startDateTime.getMonthValue() == currentMonth) {
						startDateTime = startDateTime.plusDays(1);
						endDateTime = endDateTime.plusDays(1);
					}
					currentMonth = startDateTime.getMonthValue();						
				}
				else {
					startDateTime = startDateTime.minusDays(startDateTime.getDayOfMonth()-1);
					endDateTime = endDateTime.minusDays(startDateTime.getDayOfMonth()-1);
				}					
				int copiedEvents = 0;
				while (startDateTime.getMonthValue() == currentMonth) {
					if (startDateTime.isAfter(LocalDateTime.now())) {
						if (startDateTime.getDayOfWeek().getValue() >= 1 && startDateTime.getDayOfWeek().getValue() <= 5) {
							if (!startDateTime.equals(event.getStartDateTime())) {
								Event newEvent = new Event(event);
								newEvent.setStartDateTime(startDateTime);
								newEvent.setEndDateTime(endDateTime);
								newEvent.save();
								copiedEvents++;
							}
						}
					}
					startDateTime = startDateTime.plusDays(1);
					endDateTime = endDateTime.plusDays(1);
				}
				String message = "Copied " + Integer.toString(copiedEvents) + " sessions";
				model.addAttribute("flashMessage",message);
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
			}
			returnPage = setupCalendar(model);
		}
		return returnPage;	
	}
	*/

	@GetMapping("/cancelEvent")
	public String cancelEvent(@RequestParam (name="eventId") Integer eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
				Event.deleteById(eventId);
				model.addAttribute("flashMessage","Session cancelled.");			
				returnPage = setupCalendar(model);						
		}
		return returnPage;
	}	
	
	


	@PostMapping("/addEvent")
	public String addEvent(@RequestParam(name = "club") int clubId,
			@RequestParam(name = "location") int location,
			@RequestParam(name = "startDate") LocalDate startDate,
			@RequestParam(name = "startTime") LocalTime startTime, 
			@RequestParam(name = "endTime") LocalTime endTime, 
			@RequestParam(name = "maxAttendees") int maxAttendees,
			@RequestParam(name = "staff") List<Integer> staff,	
			@RequestParam(name = "menu", required=false) List<Integer> menuGroups,			
			@RequestParam(name = "equipment") List<Integer> equipment,
			@RequestParam(name = "equipmentQuantity") List<Integer> equipmentQuantity,
			@RequestParam(name = "hiddenPerAttendee") List<Boolean> perAttendee,
			@RequestParam(name = "eventId") int eventId,
			
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Event event = null;
			LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
			LocalDateTime endDateTime = LocalDateTime.of(startDate, endTime);
			
			if (eventId != 0 ) {
				event = Event.findById(eventId);				
				event.clearResources();
				event.clearMenu();
				
				event.setStartDateTime(startDateTime);
				event.setEndDateTime(endDateTime);				
				event.setMaxAttendees(maxAttendees);
			}
			else {
				event = new Event(AggregateReference.to(clubId),  startDateTime, endDateTime, maxAttendees);	
			}
							
			for (Integer staffMember : staff) {
				EventResource er = new EventResource(AggregateReference.to(staffMember), 1, false);
				event.addResource(er);
			}
			
			logger.info("Selected equipment = {}",equipment);
			logger.info("Selected equipmentQuantity = {}",equipmentQuantity);
			logger.info("Selected perAttendee = {}",perAttendee);

			int counter = 0;				
			for (Integer item : equipment) {					
				if ( item.intValue() != 0) {
					int quantity = equipmentQuantity.get(counter).intValue();
					boolean bPerAttendee = perAttendee.get(counter).booleanValue();
					
					EventResource er = new EventResource(AggregateReference.to(item), quantity, bPerAttendee);
					
					logger.info("Selected perAtEventResource= {}",er);
					event.addResource(er);
				}
				counter++;
			}			
			EventResource er = new EventResource(AggregateReference.to(location), 1, false);
			event.addResource(er);
			if (menuGroups != null)
			{
				for (Integer menu : menuGroups) {
				
					EventMenu newMenu = new EventMenu(AggregateReference.to(menu));
					event.addEventMenu(newMenu);						
				}
			}
			
			List <ResourceStatus> resourceStatus = event.getResourceStatus();
			logger.info("ResourceStatus = {}", resourceStatus);
			boolean allResourcesOk = true;
			Iterator<ResourceStatus> rsIterator = resourceStatus.iterator();
			while (allResourcesOk && rsIterator.hasNext()) {
				allResourcesOk = rsIterator.next().isSufficient();
			}
			
			
			if (allResourcesOk) {
				event.save();	
				returnPage = setupCalendar(model);
			}
			else {
				if (event.getEventId() != 0) {
					model.addAttribute("editing",true);
				}
				model.addAttribute("clubSession",event);								
				model.addAttribute("clubs",Club.findAll());				
				model.addAttribute("locations", Resource.findByType(Resource.Type.ROOM));							
				model.addAttribute("staff", Resource.findByType(Resource.Type.STAFF));				
				model.addAttribute("equipment", Resource.findByType(Resource.Type.EQUIPMENT));							
				model.addAttribute("menus", MenuGroup.findAll());
				model.addAttribute("resourceStatus", resourceStatus);		
				this.setInDialogue(true,model);				
				returnPage = "createevent";				
			}
			
							
		}
		return returnPage;
	}

	@GetMapping("/createEvent")
	public String createEvent(Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Event event = new Event();	
			model.addAttribute("clubSession",event);								
			model.addAttribute("clubs",Club.findAll());				
			model.addAttribute("locations", Resource.findByType(Resource.Type.ROOM));							
			model.addAttribute("staff", Resource.findByType(Resource.Type.STAFF));				
			model.addAttribute("equipment", Resource.findByType(Resource.Type.EQUIPMENT));							
			model.addAttribute("menus", MenuGroup.findAll());			
			this.setInDialogue(true,model);
			returnPage = "createevent";
		} 
		return returnPage;
	}

	@GetMapping("/editEvent")
	public String editEvent(@RequestParam (name="eventId") Integer eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Event event = Event.findById(eventId);			
			if (event!=null) {
				model.addAttribute("editing",true);				
				model.addAttribute("clubSession",event);								
				model.addAttribute("clubs",Club.findAll());				
				model.addAttribute("locations", Resource.findByType(Resource.Type.ROOM));							
				model.addAttribute("staff", Resource.findByType(Resource.Type.STAFF));				
				model.addAttribute("equipment", Resource.findByType(Resource.Type.EQUIPMENT));							
				model.addAttribute("menus", MenuGroup.findAll());
				returnPage = "createevent";
			}
			else {
				model.addAttribute("flashMessage","Session does not exist");
				this.setInDialogue(true,model);
				returnPage = setupCalendar(model);				
			}
		} 
		return returnPage;
	}	
	
	@GetMapping("/createClub")
	public String createClub(Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {					
			this.setInDialogue(true,model);
			returnPage = "createclub";
		}
		return returnPage;
	}
	
	@PostMapping("/addClub")
	public String addClub(@RequestParam(name = "title") String title,
			@RequestParam(name = "description") String description, 
			@RequestParam(name = "basePrice") int basePrice, 
			@RequestParam(name = "yearR", defaultValue="false") boolean yearRCanAttend,
			@RequestParam(name = "year1", defaultValue="false") boolean yearOneCanAttend,
			@RequestParam(name = "year2", defaultValue="false") boolean yearTwoCanAttend,
			@RequestParam(name = "year3", defaultValue="false") boolean yearThreeCanAttend,
			@RequestParam(name = "year4", defaultValue="false") boolean yearFourCanAttend,
			@RequestParam(name = "year5", defaultValue="false") boolean yearFiveCanAttend,
			@RequestParam(name = "year6", defaultValue="false") boolean yearSixCanAttend, Model model) {		
		this.setInDialogue(false,model);
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Club club = new Club(title, description, basePrice, yearRCanAttend, yearOneCanAttend, yearTwoCanAttend, yearThreeCanAttend, yearFourCanAttend, yearFiveCanAttend, yearSixCanAttend); 
			club.save();			
			model.addAttribute("flashMessage","Created Club.");			

			returnPage = setupCalendar(model);
		}
		return returnPage;

	}	

	@GetMapping("/adminViewSession")
	public String viewEvent(@RequestParam (name="eventId") Integer eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Event event = Event.findById(eventId);			
			
			if (event != null) {				
				model.addAttribute("eventToView",event);
				
				List<Resource> staff = Resource.findByType(Resource.Type.STAFF);
				model.addAttribute("staff", staff);
				
				logger.info("Event stadd are {}", event.getStaff());
				
				returnPage = "viewevent";
			}
			else {
				model.addAttribute("flashMessage","Session does not exist");
				this.setInDialogue(true,model);
				returnPage = setupCalendar(model);				
			}
		}
		return returnPage;
	}
}
