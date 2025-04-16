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
import org.thymeleaf.context.Context;

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.data.Attendee;
import com.afterschoolclub.data.AttendeeIncident;
import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.Session;
import com.afterschoolclub.data.SessionMenu;
import com.afterschoolclub.data.SessionResource;
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
import com.afterschoolclub.service.EmailService;
import jakarta.mail.MessagingException;

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
	private EmailService mailService;	

	
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
				sessionBean.setFlashMessage("You need to be a administrator to perform this action.");	
				setInDialogue(false,model);
				returnPage = sessionBean.getRedirectUrl();
				//returnPage = setupCalendar(model);								
			}
		}
		else {
			sessionBean.setFlashMessage("Please login to perform this action.");
			setInDialogue(false,model);
			//returnPage = "redirect:/";		
			returnPage = sessionBean.getRedirectUrl();
		}
		return returnPage;		
	}
	
	
    @GetMapping("/adminViewIncidents")
    public String updateAdminFilters(Model model) 
    {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			setInDialogue(false,model);
			List<Session> allIncidentSessions = Session.findAllWithIncidents();
			model.addAttribute("incidentSessions", allIncidentSessions);	
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
			@RequestParam(name = "sessionId") int sessionId,
			@RequestParam(name = "incidentId") int incidentId,								
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Session session = Session.findById(sessionId);						
			if (session!=null) {
				Incident incident = null;			
				if (incidentId == 0) { // Need to create a new incident
					incident = new Incident();
					session.addIncident(incident);
				}
				else { // We are updating an existing incident
					incident = session.getIncident(incidentId);					
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
				session.save();
				sessionBean.setFlashMessage("Incident has been added");
			}
			else {
				sessionBean.setFlashMessage("Link out of date");
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
	public String createIncident(@RequestParam (name="sessionId") int sessionId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Session session = Session.findById(sessionId);
			if (session != null) {
				for (Attendee attendee: session.getAttendees()) {
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
				model.addAttribute("session",session);				
				model.addAttribute("incident", new Incident());
				this.setInDialogue(true,model);				
				returnPage = "createincident";			
			}
			else {
				sessionBean.setFlashMessage("Link out of date");
				returnPage = setupCalendar(model);	
			}
		}
		return returnPage;
	}	
	
	@GetMapping("/adminViewIncident")
	public String viewIncident(@RequestParam (name="sessionId") int sessionId, @RequestParam (name="incidentId") int incidentId,  Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Session session = Session.findById(sessionId);
			if (session != null) {
				Incident incident = session.getIncident(incidentId);
				if (incident !=null) {				
					for (Attendee attendee: session.getAttendees()) {
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
					model.addAttribute("session",session);				
					model.addAttribute("incident", incident);
					this.setInDialogue(false,model);
					
					returnPage = "createincident";		
				}
				else {
					sessionBean.setFlashMessage("Link out of date");
					returnPage = "redirect:/adminViewIncidents";				
				}				
			}
			else {
				sessionBean.setFlashMessage("Link out of date");
				returnPage = "redirect:adminViewIncidents";
			}
		}
		return returnPage;
	}	
	
	@GetMapping("/adminEditIncident")
	public String editIncident(@RequestParam (name="sessionId") int sessionId, @RequestParam (name="incidentId") int incidentId,  Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Session session = Session.findById(sessionId);
			if (session != null) {
				Incident incident = session.getIncident(incidentId);
				if (incident != null ) {
					for (Attendee attendee: session.getAttendees()) {
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
					model.addAttribute("session",session);				
					model.addAttribute("incident", incident);
					this.setInDialogue(true,model);
					
					returnPage = "createincident";								
				}
				else {
					sessionBean.setFlashMessage("Link out of date");
					returnPage = "redirect:adminViewIncidents";						
				}
			}
			else {
				sessionBean.setFlashMessage("Link out of date");
				returnPage = "redirect:adminViewIncidents";
			}
		}
		return returnPage;
	}		
	
	@GetMapping("/adminDeleteIncident")
	public String adminDeleteIncident(@RequestParam (name="sessionId") int sessionId, @RequestParam (name="incidentId") int incidentId,  Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Session session = Session.findById(sessionId);
			if (session != null) {
				session.removeIncident(incidentId);
				session.save();
				returnPage = "redirect:/adminViewIncidents";									
			}
			else {
				sessionBean.setFlashMessage("Link out of date");
				returnPage = "redirect:adminViewIncidents";	
			}
		}
		return returnPage;
	}		
	
	
	
	
	
	
	@PostMapping("/addRegister")
	public String addRegister(@RequestParam Map<String,String> register, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {			
			Session session = Session.findById(Integer.parseInt(register.get("sessionId")));
			if (session!=null) {
				for (Attendee attendee: session.getAttendees()) {
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
				session.save();
				sessionBean.setFlashMessage("Recorded Register");	
			}
			else {
				sessionBean.setFlashMessage("Internal error cannot find session.");
			}
			
			returnPage = setupCalendar(model);
		}
		return returnPage;
		
	}

	
	
	
	@GetMapping("/copySession")
	public String copySession(@RequestParam (name="sessionId") int sessionId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {			
			Session session = Session.findById(sessionId);						
			if (session!=null) {
				Session newSession = new Session(session);
				
				logger.info("Copied session {}", newSession);
				
				newSession.initialiseStartEndTimes();
				model.addAttribute("scheduling",true);							
				model.addAttribute("editing",false);
				model.addAttribute("viewing",false);
				model.addAttribute("takingRegister",false);
				
				
				model.addAttribute("clubSession",newSession);								
				model.addAttribute("clubs",Club.findAll());				
				model.addAttribute("locations", Resource.findActiveByType(Resource.Type.LOCATION));							
				model.addAttribute("staff", Resource.findActiveByType(Resource.Type.STAFF));				
				model.addAttribute("equipment", Resource.findActiveByType(Resource.Type.EQUIPMENT));							
				model.addAttribute("menus", MenuGroup.findByState(State.ACTIVE));
				setInDialogue(true, model);
				returnPage = "adminsession";				
			}
			else {
				sessionBean.setFlashMessage("Link out of date");
				returnPage = setupCalendar(model);
			}
		}
		return returnPage;			
	}
	
	
	@GetMapping("/cancelSession")
	public String cancelSession(@RequestParam (name="sessionId") Integer sessionId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
				Session.deleteById(sessionId);
				sessionBean.setFlashMessage("Session cancelled.");			
				returnPage = setupCalendar(model);						
		}
		return returnPage;
	}	
	
	


	@PostMapping("/addSession")
	public String addSession(@RequestParam(name = "club") int clubId,
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
			@RequestParam(name = "sessionId") int sessionId,
			@RequestParam(name = "parentNotes") String parentNotes,
			@RequestParam(name = "organiserNotes") String organiserNotes,			
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
			LocalDateTime endDateTime = LocalDateTime.of(startDate, endTime);
			List<Session> allSessions = new ArrayList<Session>();
			RecurrenceSpecification rs = null;
			
			if (sessionId != 0 ) {
				Session session = Session.findById(sessionId);				
				session.clearResources();
				session.clearMenu();
				
				session.setStartDateTime(startDateTime);
				session.setEndDateTime(endDateTime);				
				session.setMaxAttendees(maxAttendees);
				session.setParentNotes(parentNotes);
				session.setAdministratorNotes(organiserNotes);
				allSessions.add(session);
			}
			else {
				List<Holiday> allHolidays = Holiday.findAll();
				Session session = new Session(AggregateReference.to(clubId),  startDateTime, endDateTime, maxAttendees);
				session.setParentNotes(parentNotes);
				session.setAdministratorNotes(organiserNotes);

				
				
				
				rs = new RecurrenceSpecification(startDateTime.toLocalDate(),  recurringEndDate, MonRecurring, TueRecurring, WedRecurring, ThurRecurring, FriRecurring, SatRecurring, SunRecurring, termTimeOnly);				
				rs.save(); // Need to save so get set the aggregate Id for each session

				session.setRecurrenceSpecification(rs);
				
				if (!session.isRecurring()) {
					allSessions.add(session);
				}			
				
				
				logger.info("Recurrence Specfication Id = {}",rs.getRecurrenceSpecificationId());

				
				int copiedSessions = 0;
				LocalDate nextDate = startDateTime.toLocalDate(); //.plusDays(1);
								
				while (session.isRecurring() && nextDate.compareTo(recurringEndDate) <= 0) {
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
						Session newSession = new Session(session);
						
						newSession.setStartDateTime(nextDate.atTime(startDateTime.toLocalTime()));
						newSession.setEndDateTime(nextDate.atTime(endDateTime.toLocalTime()));
						allSessions.add(newSession);
						copiedSessions++;						
					}
					nextDate = nextDate.plusDays(1);					
				}								
			}
			
			boolean allResourcesOk = true;
			List <ResourceStatus> allResourceChallenges = new ArrayList<ResourceStatus>();

			
			for (Session session: allSessions) {

				for (Integer staffMember : staff) {
					SessionResource er = new SessionResource(AggregateReference.to(staffMember), 1, false);
					session.addResource(er);
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
							
							SessionResource er = new SessionResource(AggregateReference.to(item), quantity, bPerAttendee);
							
							logger.info("Selected perAtSessionResource= {}",er);
							session.addResource(er);
						}
					}
					counter++;
				}			
				SessionResource er = new SessionResource(AggregateReference.to(location), 1, false);
				session.addResource(er);
				if (menuGroups != null)
				{
					for (Integer menu : menuGroups) {
					
						SessionMenu newMenu = new SessionMenu(AggregateReference.to(menu));
						session.addSessionMenu(newMenu);						
					}
				}
				
				List <ResourceStatus> resourceStatus = session.getResourceStatus();
				
				
				
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
				Session.saveAll(allSessions);				
				returnPage = setupCalendar(model);
			}
			else {
								
				if (allSessions.get(0).getSessionId() != 0) {
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
					
				model.addAttribute("clubSession",allSessions.get(0));								
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

	@GetMapping("/createSession")
	public String createSession(Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Session session = new Session();	
			model.addAttribute("scheduling",true);	
			model.addAttribute("editing",false);
			model.addAttribute("viewing",false);
			model.addAttribute("takingRegister",false);			
			
			model.addAttribute("clubSession",session);								
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

	@GetMapping("/editSession")
	public String editSession(@RequestParam (name="sessionId") Integer sessionId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Session session = Session.findById(sessionId);			
			if (session!=null) {
				model.addAttribute("editing",true);	
				model.addAttribute("scheduling",false);					
				model.addAttribute("viewing",false);
				model.addAttribute("takingRegister",false);				
				
				model.addAttribute("clubSession",session);								
				model.addAttribute("clubs",Club.findAll());				
				model.addAttribute("locations", Resource.findActiveByType(Resource.Type.LOCATION));							
				model.addAttribute("staff", Resource.findActiveByType(Resource.Type.STAFF));				
				model.addAttribute("equipment", Resource.findActiveByType(Resource.Type.EQUIPMENT));							
				model.addAttribute("menus", MenuGroup.findByState(State.ACTIVE));			
				
				
				model.addAttribute("resourceStatus", session.getResourceStatus());		

				setInDialogue(true, model);
				
				returnPage = "adminsession";
			}
			else {
				sessionBean.setFlashMessage("Session does not exist");
				this.setInDialogue(true,model);
				returnPage = setupCalendar(model);				
			}
		} 
		return returnPage;
	}	
	
	@GetMapping("/adminViewSession")
	public String adminViewSession(@RequestParam (name="sessionId") Integer sessionId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Session session = Session.findById(sessionId);			
			if (session!=null) {
				model.addAttribute("viewing",true);		
				model.addAttribute("scheduling",false);	
				model.addAttribute("editing",false);
				model.addAttribute("takingRegister",false);				
								
				model.addAttribute("clubSession",session);								
				model.addAttribute("clubs",Club.findAll());				
				model.addAttribute("locations", Resource.findByType(Resource.Type.LOCATION));							
				model.addAttribute("staff", Resource.findByType(Resource.Type.STAFF));				
				model.addAttribute("equipment", Resource.findByType(Resource.Type.EQUIPMENT));							
				model.addAttribute("menus", MenuGroup.findAll());
				model.addAttribute("displayHelper", displayHelper);			
				model.addAttribute("resourceStatus", session.getResourceStatus());		
				returnPage = "adminsession";
			}
			else {
				sessionBean.setFlashMessage("Session does not exist");
				this.setInDialogue(true,model);
				returnPage = setupCalendar(model);				
			}
		} 
		return returnPage;
	}	
	
	@GetMapping("/takeRegister")
	public String takeRegister(@RequestParam (name="sessionId") Integer sessionId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			Session session = Session.findById(sessionId);			
			if (session!=null) {
				model.addAttribute("viewing",true);		
				model.addAttribute("scheduling",false);	
				model.addAttribute("editing",false);
				model.addAttribute("takingRegister",true);
								
				model.addAttribute("clubSession",session);								
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
				sessionBean.setFlashMessage("Session does not exist");
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
				sessionBean.setFlashMessage("Created Club.");
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
				sessionBean.setFlashMessage("Updated Club.");	
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
			
			sessionBean.setReturnClubRevenue();
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
			model.addAttribute("validateUsers",User.findToBeAdminValidated());

			
			
			model.addAttribute("totalOverdraftLimit",Parent.getTotalOverdraftLmit());				
			
			sessionBean.setReturnParentFinances(); 
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
			
			sessionBean.setReturnUrl("./adminViewTransactions?userId=".concat(String.valueOf(userId))); 

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
			user.refund();
			
			//TODO send an email
			
			sessionBean.setFlashMessage("User successfully deleted.");
			returnPage= "redirect:parentFinances";
		}
		return returnPage;				
	}	
	
	@GetMapping("/validateUser")
	public String validateUser(	@RequestParam(name = "userId") int userId,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			User user = User.findById(userId);
			user.setAdminVerified(true);
			user.update();		
			
			//TODO send email to user
			
			sessionBean.setFlashMessage("User approved and notified.");
			returnPage= sessionBean.getRedirectUrl();
		}
		return returnPage;				
	}	
	
	@GetMapping("/rejectUser")
	public String rejectUser(	@RequestParam(name = "userId") int userId,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			User user = User.findById(userId);
			user.setAdminVerified(true);
			
			//TODO send email to user
			
			
			user.delete();		
			sessionBean.setFlashMessage("User rejected and notified.");
			returnPage= sessionBean.getRedirectUrl();
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
			sessionBean.setFlashMessage("Overdraft successfully updated.");
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

			// TODO check reference not already used			
			
			parent.addTransaction(pt);			
			user.save();
			sessionBean.setFlashMessage("Voucher successfully registered.");
			
			returnPage= "redirect:parentFinances";
		}
		return returnPage;
	}		
	
	/**
	 * Refund a user their total cash balance 
	 * @param userId - the user identifier of the user to be refunded
	 * @param model
	 * @return return redirect back to the parentFinances view 
	 */
	@GetMapping("/refundUser")
	public String refundUser(@RequestParam(name = "userId") int userId,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {					
			User user = User.findById(userId);			
			if (user.refund()) {
				sessionBean.setFlashMessage("User successfully refunded.");								
			}
			else {
				sessionBean.setFlashMessage("Failed to refund user.");												
			}
			returnPage= "redirect:parentFinances";
		}
		return returnPage;
	}	
	
	
	@GetMapping("/emailUserInDebt")
	public String emailUserInDebt(@RequestParam(name = "userId") Integer userId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			User user = User.findById(userId);
			if (user!=null) {
				Parent parent = user.getParent();
				Context context = new Context();
				context.setVariable("user", user);
				context.setVariable("parent", parent);
				context.setVariable("sessionBean", sessionBean);
				
				try {//TODO use config for email etc... 
					mailService.sendTemplateEmail(user.getEmail(), "afterschooladmin@hattonsplace.co.uk",
							"AfterSchool Club Payment Reminder", "emailUserInDebt", context);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				sessionBean.setFlashMessage("Email sent");
			}
			else {
				sessionBean.setFlashMessage("Invalid Link");
			}
			returnPage = sessionBean.getRedirectUrl();
		}
		
		return returnPage;

	}
	
	@GetMapping("/emailAllUsersInDebt")
	public String emailAllUsersInDebt(Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			List<User> allUsersInDebt = User.findInDebt();
			for (User user:allUsersInDebt) {
				Parent parent = user.getParent();
				Context context = new Context();
				context.setVariable("user", user);
				context.setVariable("parent", parent);
				context.setVariable("sessionBean", sessionBean);
				
				try {//TODO use config for email etc... 
					mailService.sendTemplateEmail(user.getEmail(), "afterschooladmin@hattonsplace.co.uk",
							"AfterSchool Club Payment Reminder", "emailUserInDebt", context);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				sessionBean.setFlashMessage("Email(s) sent");
			}						
			returnPage = sessionBean.getRedirectUrl();
		}
		
		return returnPage;

	}		
	
	
	@GetMapping("/deleteClub")
	public String deleteClub(@RequestParam(name = "clubId") Integer clubId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Club club = Club.findById(clubId);
			if (club != null) {
				club.setState(State.INACTIVE);
				club.save();
				sessionBean.setFlashMessage("Club deleted");				
			}
			returnPage = sessionBean.getRedirectUrl();
		}	
		return returnPage;

	}
	
	
	@GetMapping("/showFinanceSummary")
	public String emailUserInDebt(@RequestParam(name = "show") boolean show, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			sessionBean.setFinanceSummaryVisible(show);
			returnPage = sessionBean.getRedirectUrl();
		}		
		return returnPage;

	}		
	
}