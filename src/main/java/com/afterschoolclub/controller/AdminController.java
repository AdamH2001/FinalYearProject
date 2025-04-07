package com.afterschoolclub.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.data.Attendee;
import com.afterschoolclub.data.AttendeeIncident;
import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.EventMenu;
import com.afterschoolclub.data.EventResource;
import com.afterschoolclub.data.Filter;
import com.afterschoolclub.data.Holiday;
import com.afterschoolclub.data.Incident;
import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.RecurrenceSpecification;
import com.afterschoolclub.data.Resource;
import com.afterschoolclub.data.Resource.Type;
import com.afterschoolclub.service.ClubPicService;
import com.afterschoolclub.service.DisplayHelperService;
import com.afterschoolclub.service.PaypalService;
import com.paypal.base.rest.PayPalRESTException;
import com.afterschoolclub.data.ResourceStatus;
import com.afterschoolclub.data.State;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.User; 
  
@Controller
@SessionAttributes({"sessionBean"})
public class AdminController {

	
	@Autowired	
    private DisplayHelperService displayHelper;
	
	@Autowired
    private ClubPicService clubPicService;
	
	@Autowired	
    private MainController mainController;

	@Autowired	
    private PaypalService paypalService;

	
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
	
	
    @GetMapping("/adminViewIncidents")
    public String updateAdminFilters(Model model) 
    {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			setInDialogue(false,model);
			List<Event> allIncidentEvents = Event.findAllWithIncidents();
			model.addAttribute("incidentEvents", allIncidentEvents);	
			this.setInDialogue(false,model);							
			returnPage = "adminIncidents";
		}		
		return returnPage;
    }   
    
    
    @PostMapping("/updateAdminFilters")
    public String updateAdminFilters(
            @RequestParam(name="onlyMine", required=false) Boolean onlyMine,
            @RequestParam(name="adminFilter") Integer adminFilter,
            @RequestParam(name="filterClub", required=true) int filterClub,            
    		Model model) 
    {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			setInDialogue(false,model);
			sessionBean.getFilter().setOnlyMineFilter(onlyMine!=null);
			sessionBean.getFilter().setAdminFilter(Filter.AdminFilter.valueOf(adminFilter));
    		sessionBean.getFilter().setFilterClubId(filterClub);
			
				    		    
			returnPage = setupCalendar(model);
		}		
		return returnPage;
    }   


	@PostMapping("/addIncident")
	public String addIncident(
			@RequestParam(name = "attendeeId") List<Integer> allAttendees,
			@RequestParam(name = "attendeeNotes") List<String> allAttendeeNotes,			
			@RequestParam(name = "incidentSummary") String summary,
			@RequestParam(name = "eventId") int eventId,
			@RequestParam(name = "incidentId") int incidentId,								
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Event event = Event.findById(eventId);						
			if (event!=null) {
				Incident incident = null;			
				if (incidentId == 0) { // Need to create a new incident
					incident = new Incident();
					event.addIncident(incident);
				}
				else { // We are updating an existing incident
					incident = event.getIncident(incidentId);					
					incident.resetAttendees();
				}				
				incident.setSummary(summary);				
				for (int i = 0; i < allAttendees.size(); i++) {
					int attendeeId = allAttendees.get(i).intValue();
					if (attendeeId != 0) {
						String attendeeNotes = allAttendeeNotes.get(i);					
						incident.addAttendeeIncident(new AttendeeIncident(AggregateReference.to(attendeeId), attendeeNotes));
					}
				}
				event.save();
				model.addAttribute("flashMessage", "Incident has been added");
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
			}
			if (incidentId != 0) {
				returnPage = "redirect:/adminViewIncidents";
			}
			else {
				returnPage = setupCalendar(model);
			}
		}
		return returnPage;
	}    
	
	
	@GetMapping("/manageResources")
	public String manageResources(Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Resource.cleanUpInactiveResources();			
			List<Resource> allEquipment = Resource.findActiveByType(Type.EQUIPMENT);
			List<Resource> allLocations = Resource.findActiveByType(Type.LOCATION);
			List<User> allStaff = User.findActiveStaff();
			model.addAttribute("allEquipment",allEquipment);
			model.addAttribute("allLocations",allLocations);
			model.addAttribute("allStaff",allStaff);
			this.setInDialogue(true,model);							
			returnPage = "manageResources";					
		}
		return returnPage;
	}    	
	
	
	@GetMapping("/manageRefreshments")
	public String manageRefreshments(Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Resource.cleanUpInactiveResources();			
			Iterable<MenuOption> allMenuOptions = MenuOption.findByState(State.ACTIVE);
			model.addAttribute("allMenuOptions",allMenuOptions);

			Iterable<MenuGroup> allMenuGroups = MenuGroup.findByState(State.ACTIVE);
			model.addAttribute("allMenuGroups",allMenuGroups);
			
			this.setInDialogue(true,model);							
			returnPage = "manageRefreshments";					
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
				model.addAttribute("incident", new Incident());
				model.addAttribute("isViewing", false);
				model.addAttribute("isEditing", false);
				model.addAttribute("isCreating", true);
				model.addAttribute("event",event);				
				model.addAttribute("incident", new Incident());
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
	
	@GetMapping("/adminViewIncident")
	public String viewIncident(@RequestParam (name="eventId") int eventId, @RequestParam (name="incidentId") int incidentId,  Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Event event = Event.findById(eventId);
			if (event != null) {
				Incident incident = event.getIncident(incidentId);
				if (incident !=null) {				
					for (Attendee attendee: event.getAttendees()) {
						List<Student> studList = Student.findByAttendeeId(attendee.getAttendeeId());
						
						for (Student student: studList) {
							logger.info("student = {}", student);
							attendee.setStudent(student);
						}					
					}
					model.addAttribute("incident", new Incident());
					model.addAttribute("isViewing", true);
					model.addAttribute("isEditing", false);
					model.addAttribute("isCreating", false);
					model.addAttribute("event",event);				
					model.addAttribute("incident", incident);
					this.setInDialogue(false,model);
					
					returnPage = "createincident";		
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					returnPage = "redirect:/adminViewIncidents";				
				}				
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				returnPage = "redirect:adminViewIncidents";
			}
		}
		return returnPage;
	}	
	
	@GetMapping("/adminEditIncident")
	public String editIncident(@RequestParam (name="eventId") int eventId, @RequestParam (name="incidentId") int incidentId,  Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Event event = Event.findById(eventId);
			if (event != null) {
				Incident incident = event.getIncident(incidentId);
				if (incident != null ) {
					for (Attendee attendee: event.getAttendees()) {
						List<Student> studList = Student.findByAttendeeId(attendee.getAttendeeId());
						
						for (Student student: studList) {
							logger.info("student = {}", student);
							attendee.setStudent(student);
						}					
					}
					model.addAttribute("incident", new Incident());
					model.addAttribute("isViewing", false);
					model.addAttribute("isEditing", true);
					model.addAttribute("isCreating", false);
					model.addAttribute("event",event);				
					model.addAttribute("incident", incident);
					this.setInDialogue(true,model);
					
					returnPage = "createincident";								
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					returnPage = "redirect:adminViewIncidents";						
				}
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				returnPage = "redirect:adminViewIncidents";
			}
		}
		return returnPage;
	}		
	
	@GetMapping("/adminDeleteIncident")
	public String adminDeleteIncident(@RequestParam (name="eventId") int eventId, @RequestParam (name="incidentId") int incidentId,  Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Event event = Event.findById(eventId);
			if (event != null) {
				event.removeIncident(incidentId);
				event.save();
				returnPage = "redirect:/adminViewIncidents";									
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				returnPage = "redirect:adminViewIncidents";	
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
				for (Attendee attendee: event.getAttendees()) {
					String value = register.get("attendee_".concat(String.valueOf(attendee.getAttendeeId())));
					if (value.equals("Present")) {
							attendee.setAttended(Attendee.Registration.PRESENT);
					}
					else if (value.equals("Absent")) {
						attendee.setAttended(Attendee.Registration.ABSENT);
					}
					else {
						attendee.setAttended(Attendee.Registration.NOTRECORDED);
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
				newEvent.initialiseStartEndTimes();
				model.addAttribute("scheduling",true);							
				model.addAttribute("editing",false);
				model.addAttribute("viewing",false);
				model.addAttribute("takingRegister",false);
				
				
				model.addAttribute("clubSession",newEvent);								
				model.addAttribute("clubs",Club.findAll());				
				model.addAttribute("locations", Resource.findActiveByType(Resource.Type.LOCATION));							
				model.addAttribute("staff", Resource.findActiveByType(Resource.Type.STAFF));				
				model.addAttribute("equipment", Resource.findActiveByType(Resource.Type.EQUIPMENT));							
				model.addAttribute("menus", MenuGroup.findByState(State.ACTIVE));
				setInDialogue(true, model);
				returnPage = "adminsession";				
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				returnPage = setupCalendar(model);
			}
		}
		return returnPage;			
	}
	
	
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
			@RequestParam(name = "recurringEndDate", required=false) LocalDate recurringEndDate,
			@RequestParam(name = "MonRecurring", required=false) Boolean MonRecurring,
			@RequestParam(name = "TueRecurring", required=false) Boolean TueRecurring,
			@RequestParam(name = "WedRecurring", required=false) Boolean WedRecurring,
			@RequestParam(name = "ThurRecurring", required=false) Boolean ThurRecurring,
			@RequestParam(name = "FriRecurring", required=false) Boolean FriRecurring,
			@RequestParam(name = "SatRecurring", required=false) Boolean SatRecurring,
			@RequestParam(name = "SunRecurring", required=false) Boolean SunRecurring,
			@RequestParam(name = "termTimeOnly", required=false) Boolean termTimeOnly,			 
			@RequestParam(name = "staff") List<Integer> staff,	
			@RequestParam(name = "menu", required=false) List<Integer> menuGroups,			
			@RequestParam(name = "equipment") List<Integer> equipment,
			@RequestParam(name = "equipmentQuantity") List<Integer> equipmentQuantity,
			@RequestParam(name = "hiddenPerAttendee") List<Boolean> perAttendee,
			@RequestParam(name = "eventId") int eventId,
			@RequestParam(name = "parentNotes") String parentNotes,
			@RequestParam(name = "organiserNotes") String organiserNotes,			
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
			LocalDateTime endDateTime = LocalDateTime.of(startDate, endTime);
			List<Event> allEvents = new ArrayList<Event>();
			RecurrenceSpecification rs = null;
			
			if (eventId != 0 ) {
				Event event = Event.findById(eventId);				
				event.clearResources();
				event.clearMenu();
				
				event.setStartDateTime(startDateTime);
				event.setEndDateTime(endDateTime);				
				event.setMaxAttendees(maxAttendees);
				event.setParentNotes(parentNotes);
				event.setAdministratorNotes(organiserNotes);
				allEvents.add(event);
			}
			else {
				List<Holiday> allHolidays = Holiday.findAll();
				Event event = new Event(AggregateReference.to(clubId),  startDateTime, endDateTime, maxAttendees);
				event.setParentNotes(parentNotes);
				event.setAdministratorNotes(organiserNotes);

				
				
				
				rs = new RecurrenceSpecification(startDateTime.toLocalDate(),  recurringEndDate, MonRecurring, TueRecurring, WedRecurring, ThurRecurring, FriRecurring, SatRecurring, SunRecurring, termTimeOnly);				
				rs.save(); // Need to save so get set the aggregate Id for each event

				event.setRecurrenceSpecification(rs);
				
				if (!event.isRecurring()) {
					allEvents.add(event);
				}			
				
				
				logger.info("Recurrence Specfication Id = {}",rs.getRecurrenceSpecificationId());

				
				int copiedEvents = 0;
				LocalDate nextDate = startDateTime.toLocalDate(); //.plusDays(1);
								
				while (event.isRecurring() && nextDate.compareTo(recurringEndDate) <= 0) {
					Boolean copy; 
					switch (nextDate.getDayOfWeek()) {
					case MONDAY:
						copy = MonRecurring;
						break;
					case TUESDAY:
						copy = TueRecurring;
						break;
						
					case WEDNESDAY:
						copy = WedRecurring;
						break;
						
					case THURSDAY:
						copy = ThurRecurring;
						break;
						
					case FRIDAY:
						copy = FriRecurring;
						break;

					case SATURDAY:
						copy = SatRecurring;
						break;

					case SUNDAY:
						copy = SunRecurring;
						break;

					default:
						copy = Boolean.FALSE;
						break;
					}
					boolean isRecurringDay = (copy == null) ? false:copy.booleanValue();
					boolean copyTermTimeOnly = (termTimeOnly == null) ? false : termTimeOnly.booleanValue();
						
					if (isRecurringDay && (!copyTermTimeOnly || !Holiday.isDateInHolidays(nextDate, allHolidays))) {						
						Event newEvent = new Event(event);
						
						newEvent.setStartDateTime(nextDate.atTime(startDateTime.toLocalTime()));
						newEvent.setEndDateTime(nextDate.atTime(endDateTime.toLocalTime()));
						allEvents.add(newEvent);
						copiedEvents++;						
					}
					nextDate = nextDate.plusDays(1);					
				}								
			}
			
			boolean allResourcesOk = true;
			List <ResourceStatus> allResourceChallenges = new ArrayList<ResourceStatus>();

			
			for (Event event: allEvents) {

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
						if (quantity > 0) {
							boolean bPerAttendee = perAttendee.get(counter).booleanValue();
							
							EventResource er = new EventResource(AggregateReference.to(item), quantity, bPerAttendee);
							
							logger.info("Selected perAtEventResource= {}",er);
							event.addResource(er);
						}
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
				Iterator<ResourceStatus> rsIterator = resourceStatus.iterator();
				while (rsIterator.hasNext()) {
					ResourceStatus nextStatus = rsIterator.next();
					if (!nextStatus.isSufficient()) {																	
						allResourcesOk = false;
						allResourceChallenges.add(nextStatus);
						
					}		
				}
			}
			
			
			if (allResourcesOk) {
				Event.saveAll(allEvents);				
				returnPage = setupCalendar(model);
			}
			else {
								
				if (allEvents.get(0).getEventId() != 0) {
					model.addAttribute("editing",true);
					model.addAttribute("scheduling",false);		
					model.addAttribute("takingRegister",false);					
					model.addAttribute("viewing",false);					
				}
				else {
					model.addAttribute("scheduling",true);					
					model.addAttribute("editing",false);
					model.addAttribute("takingRegister",false);					
					model.addAttribute("viewing",false);					
					
					rs.delete();
				}
					
				model.addAttribute("clubSession",allEvents.get(0));								
				model.addAttribute("clubs",Club.findAll());				
				model.addAttribute("locations", Resource.findActiveByType(Resource.Type.LOCATION));							
				model.addAttribute("staff", Resource.findActiveByType(Resource.Type.STAFF));				
				model.addAttribute("equipment", Resource.findActiveByType(Resource.Type.EQUIPMENT));							
				model.addAttribute("menus", MenuGroup.findAll());
				model.addAttribute("resourceStatus", allResourceChallenges);		
				this.setInDialogue(true,model);				
				returnPage = "adminsession";				
			}
			
							
		}
		return returnPage;
	}

	@GetMapping("/createEvent")
	public String createEvent(Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Event event = new Event();	
			model.addAttribute("scheduling",true);	
			model.addAttribute("editing",false);
			model.addAttribute("viewing",false);
			model.addAttribute("takingRegister",false);			
			
			model.addAttribute("clubSession",event);								
			model.addAttribute("clubs",Club.findAll());				
			model.addAttribute("locations", Resource.findActiveByType(Resource.Type.LOCATION));							
			model.addAttribute("staff", Resource.findActiveByType(Resource.Type.STAFF));				
			model.addAttribute("equipment", Resource.findActiveByType(Resource.Type.EQUIPMENT));							
			model.addAttribute("menus", MenuGroup.findByState(State.ACTIVE));			
			this.setInDialogue(true,model);
			returnPage = "adminsession";
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
				model.addAttribute("scheduling",false);					
				model.addAttribute("viewing",false);
				model.addAttribute("takingRegister",false);				
				
				model.addAttribute("clubSession",event);								
				model.addAttribute("clubs",Club.findAll());				
				model.addAttribute("locations", Resource.findActiveByType(Resource.Type.LOCATION));							
				model.addAttribute("staff", Resource.findActiveByType(Resource.Type.STAFF));				
				model.addAttribute("equipment", Resource.findActiveByType(Resource.Type.EQUIPMENT));							
				model.addAttribute("menus", MenuGroup.findByState(State.ACTIVE));			
				
				
				model.addAttribute("resourceStatus", event.getResourceStatus());		

				setInDialogue(true, model);
				
				returnPage = "adminsession";
			}
			else {
				model.addAttribute("flashMessage","Session does not exist");
				this.setInDialogue(true,model);
				returnPage = setupCalendar(model);				
			}
		} 
		return returnPage;
	}	
	
	@GetMapping("/adminViewSession")
	public String adminViewSession(@RequestParam (name="eventId") Integer eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Event event = Event.findById(eventId);			
			if (event!=null) {
				model.addAttribute("viewing",true);		
				model.addAttribute("scheduling",false);	
				model.addAttribute("editing",false);
				model.addAttribute("takingRegister",false);				
								
				model.addAttribute("clubSession",event);								
				model.addAttribute("clubs",Club.findAll());				
				model.addAttribute("locations", Resource.findByType(Resource.Type.LOCATION));							
				model.addAttribute("staff", Resource.findByType(Resource.Type.STAFF));				
				model.addAttribute("equipment", Resource.findByType(Resource.Type.EQUIPMENT));							
				model.addAttribute("menus", MenuGroup.findAll());
				model.addAttribute("displayHelper", displayHelper);			
				model.addAttribute("resourceStatus", event.getResourceStatus());		
				returnPage = "adminsession";
			}
			else {
				model.addAttribute("flashMessage","Session does not exist");
				this.setInDialogue(true,model);
				returnPage = setupCalendar(model);				
			}
		} 
		return returnPage;
	}	
	
	@GetMapping("/takeRegister")
	public String takeRegister(@RequestParam (name="eventId") Integer eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Event event = Event.findById(eventId);			
			if (event!=null) {
				model.addAttribute("viewing",true);		
				model.addAttribute("scheduling",false);	
				model.addAttribute("editing",false);
				model.addAttribute("takingRegister",true);
								
				model.addAttribute("clubSession",event);								
				model.addAttribute("clubs",Club.findAll());				
				model.addAttribute("locations", Resource.findActiveByType(Resource.Type.LOCATION));							
				model.addAttribute("staff", Resource.findActiveByType(Resource.Type.STAFF));				
				model.addAttribute("equipment", Resource.findActiveByType(Resource.Type.EQUIPMENT));							
				model.addAttribute("menus", MenuGroup.findAll());
				model.addAttribute("displayHelper", displayHelper);			
				setInDialogue(true, model);
				
				returnPage = "adminsession";
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
			Club club = new Club();
			model.addAttribute("club", club);
			model.addAttribute("isEditing", false);	
			model.addAttribute("tempFilename", clubPicService.getTempfilename());
			
			this.setInDialogue(true,model);
			returnPage = "club";
		}
		return returnPage;
	}
	
	
	@GetMapping("/editClub")
	public String editClub(@RequestParam(name = "clubId") int clubId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {		
			Club club = Club.findById(clubId);
			model.addAttribute("club", club);	
			model.addAttribute("isEditing", true);
			model.addAttribute("tempFilename", clubPicService.getTempfilename());
			
			this.setInDialogue(true,model);
			returnPage = "club";
		}
		return returnPage;
	}
	
	
	
	@PostMapping("/addClub")
	public String addClub(
			@RequestParam(name = "clubId") int clubId,
			@RequestParam(name = "title") String title,
			@RequestParam(name = "description") String description,
			@RequestParam(name = "keywords") String keywords,
			@RequestParam(name = "basePrice") int basePrice, 
			@RequestParam(name = "yearR", defaultValue="false") boolean yearRCanAttend,
			@RequestParam(name = "year1", defaultValue="false") boolean yearOneCanAttend,
			@RequestParam(name = "year2", defaultValue="false") boolean yearTwoCanAttend,
			@RequestParam(name = "year3", defaultValue="false") boolean yearThreeCanAttend,
			@RequestParam(name = "year4", defaultValue="false") boolean yearFourCanAttend,
			@RequestParam(name = "year5", defaultValue="false") boolean yearFiveCanAttend,
			@RequestParam(name = "year6", defaultValue="false") boolean yearSixCanAttend,
			@RequestParam(name = "acceptsVouchers", defaultValue="false") boolean acceptsVouchers,			
			@RequestParam (name = "tempFilename")	String tempFilename, 
			Model model) {		
		this.setInDialogue(false,model);
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Club club;
			if (clubId == 0) {
				club = new Club(title, description, basePrice, yearRCanAttend, yearOneCanAttend, yearTwoCanAttend, yearThreeCanAttend, yearFourCanAttend, yearFiveCanAttend, yearSixCanAttend, keywords);
				club.setAcceptsVouchers(acceptsVouchers);
				club.save();
				model.addAttribute("flashMessage","Created Club.");
			}
			else {
				club = Club.findById(clubId);
				club.setTitle(title);
				club.setDescription(description);
				club.setBasePrice(basePrice);
				club.setYearRCanAttend(yearRCanAttend);
				club.setYear1CanAttend(yearOneCanAttend);
				club.setYear2CanAttend(yearTwoCanAttend);
				club.setYear3CanAttend(yearThreeCanAttend);
				club.setYear4CanAttend(yearFourCanAttend);
				club.setYear5CanAttend(yearFiveCanAttend);
				club.setYear6CanAttend(yearSixCanAttend);
				club.setKeywords(keywords);
				club.setAcceptsVouchers(acceptsVouchers);				
				club.save();
				model.addAttribute("flashMessage","Updated Club.");	
			}
			clubPicService.renameImage(tempFilename, club);
			returnPage = sessionBean.getRedirectUrl();
		}
		return returnPage;

	}
	
	@GetMapping("/clubRevenue")
	public String viewTransactions(Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			model.addAttribute("clubs",Club.findAll());			
			model.addAttribute("totalRevenue",ParentalTransaction.getTotalRevenueBetween(sessionBean.getFinanceStartDate(), sessionBean.getFinanceEndDate()));				
			
			sessionBean.setReturnTransactions(); //TODO set diffeent page
			this.setInDialogue(false,model);
			returnPage= "clubRevenue";
		}
		return returnPage;
		
		
	}
	
	@GetMapping("/parentFinances")
	public String parentFinances(Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			model.addAttribute("users",User.findParents());
			
			model.addAttribute("totalOverdraftLimit",Parent.getTotalOverdraftLmit());				
			
			sessionBean.setReturnTransactions(); //TODO set diffeent page
			this.setInDialogue(false,model);
			returnPage= "parentFinance";
		}
		return returnPage;
		
		
	}
	
	
	
	@GetMapping("/clubRevenueBack")
	public String transactionBack(Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			sessionBean.setFinanceStartDate(sessionBean.getFinanceStartDate().minusYears(1));
			returnPage = "redirect:./clubRevenue";
		}
		return returnPage;			
	}
	
	@GetMapping("/clubRevenueForward")
	public String transactionForward(Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {		
			sessionBean.setFinanceStartDate(sessionBean.getFinanceStartDate().plusYears(1));
			returnPage = "redirect:./clubRevenue";
		}
		return returnPage;	
	}	

	
	@GetMapping("/adminViewTransactions")
	public String viewTransactions(	@RequestParam(name = "userId") int userId,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			LocalDate start = sessionBean.getTransactionStartDate();
			LocalDate end = start.plusMonths(1);
			User user = User.findById(userId);
			Parent parent = user.getParent();
			

			List<ParentalTransaction> transactions = parent.getTransactions(start,end);
			
			int openingBalance = parent.getBalanceOn(start);
			int closingBalance = parent.getBalanceOn(end);
			int openingVoucherBalance = parent.getVoucherBalanceOn(start);
			int closingVoucherBalance = parent.getVoucherBalanceOn(end);			
			
			model.addAttribute("openingBalance",openingBalance);
			model.addAttribute("closingBalance",closingBalance);
			model.addAttribute("openingVoucherBalance",openingVoucherBalance);
			model.addAttribute("closingVoucherBalance",closingVoucherBalance);			
			model.addAttribute("transactions",transactions);
			model.addAttribute("user",user);
			
			sessionBean.setReturnTransactions(); //TODO set return url

			this.setInDialogue(false,model);
			returnPage= "viewtransactions";
		}
		return returnPage;
		
		
	}
	
	@GetMapping("/deleteUser")
	public String deleteUser(	@RequestParam(name = "userId") int userId,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			User user = User.findById(userId);
			user.setState(State.INACTIVE);
			user.update();			
			returnPage= "redirect:parentFinances";
		}
		return returnPage;				
	}	
	
	@PostMapping("/updateOverdraft")
	public String updateOverdraft(@RequestParam(name = "userId") int userId,
			@RequestParam(name = "overdraftLimit") int overdraftLimit,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			User user = User.findById(userId);
			Parent parent = user.getParent();
			parent.setOverdraftLimit(overdraftLimit);
			parent.update();			
			returnPage= "redirect:parentFinances";
		}
		return returnPage;
	}		
	
	@PostMapping("/registerVoucher")
	public String registerVoucher(@RequestParam(name = "userId") int userId,
			@RequestParam(name = "voucherAmount") int voucherAmount,
			@RequestParam(name = "voucherReference") String voucherReference,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {			
			
			User user = User.findById(userId);
			Parent parent = user.getParent();									
			ParentalTransaction pt = new ParentalTransaction(voucherAmount, LocalDateTime.now(), ParentalTransaction.Type.DEPOSIT, "Voucher Registered");
			pt.setPaymentReference(voucherReference);
			pt.setBalanceType(ParentalTransaction.BalanceType.VOUCHER);			

			parent.addTransaction(pt);			
			user.save();
			
			
			returnPage= "redirect:parentFinances";
		}
		return returnPage;
	}		
	
	@GetMapping("/refundUser")
	public String refundUser(@RequestParam(name = "userId") int userId,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {					
			User user = User.findById(userId);
			Parent parent = user.getParent();
			int balance = parent.getBalance();
			int remainingRefund = balance;
			
			List<ParentalTransaction> allTopUps = parent.getCashTopUps();
			
			Iterator<ParentalTransaction> itr = allTopUps.iterator();
			
			while (remainingRefund > 0 && itr.hasNext()) {
				ParentalTransaction pt = itr.next();
				
				int refundedAmount =0;
				try {
					refundedAmount = paypalService.refundSale(pt.getPaymentReference(), remainingRefund);
				}
				catch (PayPalRESTException e) {
					e.printStackTrace(); // TODFO could observe timeout but may have actually refunded
					refundedAmount = 0;
				}
				remainingRefund -= refundedAmount;
				logger.info("Refunded {}", refundedAmount);
				
				if (refundedAmount > 0) {
					ParentalTransaction withdrawTrans = new ParentalTransaction(refundedAmount, LocalDateTime.now(), ParentalTransaction.Type.WITHDRAWAL, "Withdrawn Cash");
					parent.addTransaction(withdrawTrans);					
				}
			}
			if (remainingRefund != balance) {
				user.save();
			}
			returnPage= "redirect:parentFinances";
		}
		return returnPage;
	}	
	
	
	
	
}