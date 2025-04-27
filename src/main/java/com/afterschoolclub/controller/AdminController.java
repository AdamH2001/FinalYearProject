package com.afterschoolclub.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import com.afterschoolclub.data.Email;
import com.afterschoolclub.data.Session;
import com.afterschoolclub.data.SessionResource;
import com.afterschoolclub.data.Filter;
import com.afterschoolclub.data.Incident;
import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.RecurrenceSpecification;
import com.afterschoolclub.data.Resource;
import com.afterschoolclub.data.Resource.Type;
import com.afterschoolclub.service.ClubPicService;
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
					incident.setSessionId(AggregateReference.to(session.getSessionId()));
				}
				else { // We are updating an existing incident
					incident = session.getIncident(incidentId);					
					incident.resetAttendees();
				}		
					
				incident.setSummary(summary);
				Context context = new Context();
				context.setVariable("sessionBean", sessionBean);
				context.setVariable("session", session);
				
				List<Email> allEmails = new ArrayList<Email>();
				
				for (int i = 0; i < allAttendees.size(); i++) {					
					int attendeeId = allAttendees.get(i).intValue();
					
					if (attendeeId != 0) {
						Attendee attendee = session.getAttendee(attendeeId);
						String attendeeNotes = allAttendeeNotes.get(i);
						
						if (incidentId == 0) { // New incident ensure we send an email to parents of all involved parties
							Student student = attendee.getStudent();				
							User user = student.getUser();
							context.setVariable("user", user);
							context.setVariable("student", student);
							context.setVariable("incidentNotes", attendeeNotes);
							
							Email email = new Email(user.getEmail(), sessionBean.getContactEmail(), "AfterSchool Club Incident",  mailService.getHTML("email/incident", context));
							allEmails.add(email);
						}
						incident.addAttendeeIncident(new AttendeeIncident(AggregateReference.to(attendeeId), attendeeNotes));
					}
				}
				incident.save();							
				try {
					mailService.sendAllHTMLEmails(allEmails);
					sessionBean.setFlashMessage("Incident has been recorded and all parents notified");
				}
				catch (MessagingException e) {
					e.printStackTrace();
					sessionBean.setFlashMessage("Incident has been recorded but failed to notify parents");			
					
				}				
				returnPage = setupCalendar(model);					
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
				model.addAttribute("incident", new Incident());
				model.addAttribute("isViewing", false);
				model.addAttribute("isEditing", false);
				model.addAttribute("isCreating", true);
				model.addAttribute("incidentSession",session);				
				model.addAttribute("incident", new Incident());
				this.setInDialogue(true,model);				
				returnPage = "incident";			
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
					model.addAttribute("incident", new Incident());
					model.addAttribute("isViewing", true);
					model.addAttribute("isEditing", false);
					model.addAttribute("isCreating", false);
					model.addAttribute("incidentSession",session);				
					model.addAttribute("incident", incident);
					this.setInDialogue(false,model);					
					returnPage = "incident";		
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
					model.addAttribute("incident", new Incident());
					model.addAttribute("isViewing", false);
					model.addAttribute("isEditing", true);
					model.addAttribute("isCreating", false);
					model.addAttribute("incidentSession",session);				
					model.addAttribute("incident", incident);
					this.setInDialogue(true,model);
					
					returnPage = "incident";								
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
				model.addAttribute("clubs",Club.findActive());				
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
	@Transactional
	public String cancelSession(@RequestParam (name="sessionId") Integer sessionId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
				Session session = Session.findById(sessionId);			
				List<User> allUsers = session.getAllUsers();
				List<Email> allEmails = new ArrayList<Email>();
				
				Context context = new Context();			
				for (User user:allUsers) {
					context.setVariable("user", user);
					context.setVariable("${cancelledSession", session);
					
					context.setVariable("sessionBean", sessionBean);
					
					Email email = new Email(user.getEmail(), sessionBean.getContactEmail(), "AfterSchool Club Session Cancelled",  mailService.getHTML("email/sessionCancelled", context));
					allEmails.add(email);
					
				}
				session.cancel();
				try {
					mailService.sendAllHTMLEmails(allEmails);
					if (allEmails.size() > 0) {
						sessionBean.setFlashMessage("Session cancelled and all attendeees refunded and notified.");
					}
					else {
						sessionBean.setFlashMessage("Session cancelled");
					}
						
				}
				catch (MessagingException e) {
					e.printStackTrace();
					sessionBean.setFlashMessage("Session cancelled and all attendeees refunded but failed to notify attendees.");			
					
				}				
				returnPage = setupCalendar(model);						
		}
		return returnPage;
	}	
	
	


/**
 * Administration function to create or update a club session. Receives all 
 * the requirements for the session from the form the user has completed. 
 *  
 * @param clubId - primary key of club that session needs to be created for 
 * @param location - primary key of location where the session will occur
 * @param startDate - date the session should start
 * @param startTime - time the session should start
 * @param endTime - end time for the session
 * @param maxAttendees - max number of attendees allowed
 * @param recurringEndDate - - end date for the last recurring session
 * @param MonRecurring - if recurring should it occur on a Monday
 * @param TueRecurring - if recurring should it occur on a Tuesday
 * @param WedRecurring - if recurring should it occur on a Wednesday
 * @param ThurRecurring - if recurring should it occur on a Thursday
 * @param FriRecurring- if recurring should it occur on a Friday 
 * @param SatRecurring- if recurring should it occur on a Saturday
 * @param SunRecurring- if recurring should it occur on a Sunday
 * @param termTimeOnly- if recurring should it only occur during term time
 * @param staff - array of all the primary keys of all the staff required to 
 * 			support the session
 * @param menuGroups - array of all the primary keys of the different menus
 * 			 available for selection 
 * @param equipment - array of all the primary keys for equipment
 * @param equipmentQuantity - array of all the quantities for equipment
 * @param perAttendee - array specifies if quantity is per attendee or not
 * @param sessionId - the primary key for the session if we are editing the 
 * 		session
 * @param parentNotes - any notes for the parents 
 * @param organiserNotes - any notes for the organiser
 * @param model - holder of context data from view 
 * @return the page user should see after updating the session
 */

@PostMapping("/addSession")
@Transactional
public String addSession(@RequestParam(name = "club") int clubId,
		@RequestParam(name = "location") int location,
		@RequestParam(name = "startDate") LocalDate startDate,
		@RequestParam(name = "startTime") LocalTime startTime,
		@RequestParam(name = "endTime") LocalTime endTime,
		@RequestParam(name = "maxAttendees") int maxAttendees,
		@RequestParam(name = "recurringEndDate", required=false) 
			LocalDate recurringEndDate,
		@RequestParam(name = "MonRecurring", required=false) 
			Boolean MonRecurring,
		@RequestParam(name = "TueRecurring", required=false) 
			Boolean TueRecurring,
		@RequestParam(name = "WedRecurring", required=false) 
			Boolean WedRecurring,
		@RequestParam(name = "ThurRecurring", required=false) 
			Boolean ThurRecurring,
		@RequestParam(name = "FriRecurring", required=false) 
			Boolean FriRecurring,
		@RequestParam(name = "SatRecurring", required=false) 
			Boolean SatRecurring,
		@RequestParam(name = "SunRecurring", required=false) 
			Boolean SunRecurring,
		@RequestParam(name = "termTimeOnly", required=false) 
			Boolean termTimeOnly,			 
		@RequestParam(name = "staff") List<Integer> staff,	
		@RequestParam(name = "menu", required=false) 
			List<Integer> menuGroups,			
		@RequestParam(name = "equipment") List<Integer> equipment,
		@RequestParam(name = "equipmentQuantity") 
			List<Integer> equipmentQuantity,
		@RequestParam(name = "hiddenPerAttendee") 
			List<Boolean> perAttendee,
		@RequestParam(name = "sessionId") int sessionId,
		@RequestParam(name = "parentNotes") 
			String parentNotes,
		@RequestParam(name = "organiserNotes") 
			String organiserNotes,
		Model model) {
	
	// Ensure user is an administrator to perform this operation
	String returnPage = validateIsAdmin(model);
	if (returnPage == null) {
		LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
		LocalDateTime endDateTime = LocalDateTime.of(startDate, endTime);
		List<Session> allSessions = null; 
		RecurrenceSpecification rs = null;
		
		Session session = null;
		if (sessionId != 0 ) {
			// We are editing an already existing session so reset the
			// equipment, menu etc..
			 
			session = Session.findById(sessionId);				
			session.clearResources();
			session.clearMenu();				
			session.setStartDateTime(startDateTime);
			session.setEndDateTime(endDateTime);				
			session.setMaxAttendees(maxAttendees);
			allSessions = new ArrayList<Session>();
			
			// when editing we are only editing the single session not the 
			// recurring group				
			allSessions.add(session);
		}
		else {
			// We are creating a new session so setup 
			// recurrence specification 				
			rs = new RecurrenceSpecification(startDateTime.toLocalDate(),  
					recurringEndDate, MonRecurring, TueRecurring, 
					WedRecurring, ThurRecurring, FriRecurring, 
					SatRecurring, SunRecurring,	termTimeOnly);
			
			// Need to save so get set the aggregate Id for each session
			rs.save(); 			
			session = new Session(AggregateReference.to(clubId),  startDateTime, 
					endDateTime, maxAttendees);
			session.setRecurrenceSpecification(rs);
		}

		// set all the other attributes in the session object 
		session.setParentNotes(parentNotes);
		session.setAdministratorNotes(organiserNotes);
		session.addStaff(staff);
		session.addEquipment(equipment, equipmentQuantity, perAttendee);
		session.setMenuGroups(menuGroups);		
		session.addResource(new 
				SessionResource(AggregateReference.to(location), 
				1, false));
		
		if (allSessions == null) {
			// get a list of all the recurring sessions using session as a 
			// template 
			allSessions = rs.getAllRecurringSessions(session);	
		}		
		
		List <ResourceStatus> allResourceChallenges = 
				new ArrayList<ResourceStatus>();
		
		// Iterate over each session collecting all the resource challenges
			for (Session proposedSession: allSessions) {
				List <ResourceStatus> proposedSessionChallenges = 
						proposedSession.getResourceChallenges();
				allResourceChallenges.addAll(proposedSessionChallenges);
			}	
			 
			if (allResourceChallenges.size() == 0) {				
				// If no resource challenges save the session 
			Session.saveAll(allSessions);	
			if (sessionId == 0) {
				String message = String.format("Added %d session(s)",
						allSessions.size());
				sessionBean.setFlashMessagePreserve(message);
			}
			else {
				sessionBean.setFlashMessage("Updated Session");
			}
			
			// return back to calendar page
			returnPage = setupCalendar(model);
		}
		else {
			// otherwise there are resource challenges and need to inform 
			// the user of these set all information back in context to 
			// send back to view 
			
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
			model.addAttribute("clubs",Club.findActive());
			model.addAttribute("locations",
					Resource.findActiveByType(Resource.Type.LOCATION));	
			model.addAttribute("staff", 
					Resource.findActiveByType(Resource.Type.STAFF));
			model.addAttribute("equipment", 
					Resource.findActiveByType(Resource.Type.EQUIPMENT));
			model.addAttribute("menus", 
					MenuGroup.findAll());
			model.addAttribute("resourceStatus", 
					allResourceChallenges);
			this.setInDialogue(true,model);
			
			// return back to session page
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
			model.addAttribute("clubs",Club.findActive());				
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
			Club club = null;			
			Club existingClub = Club.findByTitle(title);
			if (existingClub != null && existingClub.getClubId() != clubId) {
				sessionBean.setFlashMessage("Club with this title already exists - please choose another title.");
				clubPicService.deleteTempImage(tempFilename);
			}
			else if (clubId == 0) {				
				club = new Club(title, description, basePrice, yearRCanAttend, yearOneCanAttend, yearTwoCanAttend, yearThreeCanAttend, yearFourCanAttend, yearFiveCanAttend, yearSixCanAttend, keywords);
				club.setAcceptsVouchers(acceptsVouchers);
				club.save();
				clubPicService.renameImage(tempFilename, club);				
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
				clubPicService.renameImage(tempFilename, club);				
				sessionBean.setFlashMessage("Updated Club.");	
			}
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
			
			sessionBean.setReturnUrl("./clubRevenue");
			this.setInDialogue(false,model);
			returnPage= "clubRevenue";
		}
		return returnPage;
		
		
	}
	
	@GetMapping("/userAccounts")
	public String userAccounts(@RequestParam(name = "newAccounts", required=false, defaultValue="2") int newAaccountsTab,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			model.addAttribute("users",User.findParents());
			model.addAttribute("validateUsers",User.findParentByStateVerified(State.ACTIVE, false));
			model.addAttribute("totalOverdraftLimit",Parent.getTotalOverdraftLmit());	
			
			if (newAaccountsTab == 1) {
				sessionBean.setNewAccountsTab(true);
			} 
			else if (newAaccountsTab == 0) {
				sessionBean.setNewAccountsTab(false);				
			}
			
			sessionBean.setReturnUrl("./userAccounts"); 
			this.setInDialogue(false,model);
			returnPage= "userAccounts";
		}
		return returnPage;
	}

	@GetMapping("/students")
	public String students(@RequestParam(name = "newAccounts", required=false, defaultValue="2") int newAaccountsTab,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {	
			model.addAttribute("students",Student.findByStateVerified(State.ACTIVE, true));
			model.addAttribute("validateStudents",Student.findByStateVerified(State.ACTIVE, false));
			
			if (newAaccountsTab == 1) {
				sessionBean.setNewStudentsTab(true);
			} 
			else if (newAaccountsTab == 0) {
				sessionBean.setNewStudentsTab(false);				
			}
			
			sessionBean.setReturnUrl("./students"); 
			this.setInDialogue(false,model);
			returnPage= "students";
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
			sessionBean.setNewAccountsTab(false);
			this.setInDialogue(false,model);
			returnPage= "viewtransactions";
		}
		return returnPage;
		
		
	}
	
	@Transactional
	@GetMapping("/deleteUser")
	public String deleteUser(	@RequestParam(name = "userId") int userId,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			User user = User.findById(userId);
			user.setState(State.INACTIVE);			
			user.update();		
			user.refund();
			
			Context context = new Context();
			context.setVariable("user", user);
			context.setVariable("sessionBean", sessionBean);	
			Email email = new Email(user.getEmail(), sessionBean.getContactEmail(), "AfterSchool Club Account Cancelled", mailService.getHTML("email/accountCancelled", context));

			try {
				mailService.sendHTMLEmail(email);
				sessionBean.setFlashMessage("User successfully deleted.");
			} catch (Exception e) {
				e.printStackTrace();
				sessionBean.setFlashMessage("Failed to notify user.");
			}						
			returnPage= "redirect:userAccounts";
		}
		return returnPage;				
	}	
	
	@GetMapping("/deleteStudent")
	public String deleteStudent(@RequestParam(name = "studentId") int studentId,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			Student student = Student.findById(studentId);
			student.setState(State.INACTIVE);			
			student.update();		
			
			User user = student.getUser();
			
			Context context = new Context();
			context.setVariable("user", user);
			context.setVariable("student", student);			
			context.setVariable("sessionBean", sessionBean);	
			Email email = new Email(user.getEmail(), sessionBean.getContactEmail(), "AfterSchool Club Account Cancelled", mailService.getHTML("email/studentDeleted", context));

			try {
				mailService.sendHTMLEmail(email);
				sessionBean.setFlashMessagePreserve(String.format("%s successfully deleted and parent notified", student.getFullName()));
			} catch (Exception e) {
				e.printStackTrace();
				sessionBean.setFlashMessage("Failed to notify user.");
			}						
			returnPage= "redirect:students";
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
			sessionBean.setNewAccountsTab(true);
			
			Context context = new Context();
			context.setVariable("user", user);
			String link = String.format("%s/validateEmail?userId=%d&validationKey=%d", sessionBean.getHomePage(), user.getUserId(), user.getValidationKey());
			context.setVariable("link", link);
			
			context.setVariable("sessionBean", sessionBean);
			
			Email email = new Email(user.getEmail(), sessionBean.getContactEmail(),"AfterSchool Club Account Validated",  mailService.getHTML("email/validatedUser", context));
			
			try {
				mailService.sendHTMLEmail(email);
				sessionBean.setFlashMessagePreserve(String.format("%s approved and notified", user.getFullName()));
				
			} catch (Exception e) {
				e.printStackTrace();
				sessionBean.setFlashMessagePreserve(String.format("Failed to notify %s", user.getFullName()));
			}

			
			returnPage= sessionBean.getRedirectUrl();
		}
		return returnPage;				
	}	
	
	@GetMapping("/validateStudent")
	public String validateStudent(	@RequestParam(name = "studentId") int studentId,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			Student student = Student.findById(studentId);
			student.setAdminVerified(true);
			student.update();		
			sessionBean.setNewStudentsTab(true);
			
			User user = student.getUser();
			Context context = new Context();
			context.setVariable("user", user);
			context.setVariable("student", student);			
			context.setVariable("sessionBean", sessionBean);
			
			Email email = new Email(user.getEmail(), sessionBean.getContactEmail(),"AfterSchool Club Student Validated",  mailService.getHTML("email/validatedStudent", context));
			
			try {
				mailService.sendHTMLEmail(email);
				sessionBean.setFlashMessagePreserve(String.format("%s approved and parent notified", student.getFullName()));
				
			} catch (Exception e) {
				e.printStackTrace();
				sessionBean.setFlashMessagePreserve(String.format("Failed to notify parent of %s", student.getFullName()));
			}

			
			returnPage= sessionBean.getRedirectUrl();
		}
		return returnPage;				
	}		
	
	@GetMapping("/rejectStudent")
	public String rejectStudent(	@RequestParam(name = "studentId") int studentId,
			Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			Student student = Student.findById(studentId);			
						
			sessionBean.setNewStudentsTab(true);
			
			User user = student.getUser();
			
			Context context = new Context();
			context.setVariable("user", user);
			context.setVariable("student", student);			
			
			context.setVariable("sessionBean", sessionBean);
			
			Email email = new Email(user.getEmail(), sessionBean.getContactEmail(),"AfterSchool Club Student Not Approved",  mailService.getHTML("email/rejectedStudent", context));
			
			student.delete();		
			sessionBean.setNewAccountsTab(true);
			
			try {
				mailService.sendHTMLEmail(email);
				sessionBean.setFlashMessagePreserve(String.format("%s rejected and parent notified", user.getFullName()));
				
			} catch (Exception e) {
				e.printStackTrace();
				sessionBean.setFlashMessagePreserve(String.format("Failed to notify parent of %s", user.getFullName()));
			}			
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
			
			Context context = new Context();
			context.setVariable("user", user);
			context.setVariable("sessionBean", sessionBean);
			
			Email email = new Email(user.getEmail(), sessionBean.getContactEmail(),"AfterSchool Club Account Not Approved",  mailService.getHTML("email/rejectedUser", context));
			user.delete();		
			sessionBean.setNewAccountsTab(true);
			
			try {
				mailService.sendHTMLEmail(email);
				sessionBean.setFlashMessagePreserve(String.format("%s rejected and notified", user.getFullName()));
				
			} catch (Exception e) {
				e.printStackTrace();
				sessionBean.setFlashMessagePreserve(String.format("Failed to notify %s", user.getFullName()));
			}			
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
			returnPage= "redirect:userAccounts";
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
			ParentalTransaction oldPt = ParentalTransaction.findVoucherByReferenceId(voucherReference);
			if (oldPt == null) {
				User user = User.findById(userId);
				Parent parent = user.getParent();									
				ParentalTransaction pt = new ParentalTransaction(voucherAmount, LocalDateTime.now(), ParentalTransaction.Type.DEPOSIT, "Voucher Registered");
				pt.setPaymentReference(voucherReference);
				pt.setBalanceType(ParentalTransaction.BalanceType.VOUCHER);
				pt.setParent(parent);
				pt.save();							
				sessionBean.setFlashMessage("Voucher successfully registered.");				
			}
			else {
				sessionBean.setFlashMessage("Voucher has already been used.");
			}
			returnPage= "redirect:userAccounts";
		}
		return returnPage;
	}		
	
	/**
	 * Refund a user their total cash balance 
	 * @param userId - the user identifier of the user to be refunded
	 * @param model
	 * @return return redirect back to the userAccounts view 
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
			returnPage= "redirect:userAccounts";
		}
		return returnPage;
	}	
	
	
	@GetMapping("/emailUserInDebt")
	public String emailUserInDebt(@RequestParam(name = "userId") Integer userId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			User user = User.findById(userId);
			if (user!=null) {
				Context context = new Context();
				context.setVariable("user", user);
				context.setVariable("sessionBean", sessionBean);	
				
				Email email = new Email(user.getEmail(), sessionBean.getContactEmail(), "AfterSchool Club Payment Reminder",  mailService.getHTML("email/userInDebt", context));
				try {
					mailService.sendHTMLEmail(email);							
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
			List<Email> allEmails = new ArrayList<Email>();
			Context context = new Context();			
			for (User user:allUsersInDebt) {
				context.setVariable("user", user);
				context.setVariable("sessionBean", sessionBean);
				
				Email email = new Email(user.getEmail(), sessionBean.getContactEmail(), "AfterSchool Club Payment Reminder",  mailService.getHTML("email/userInDebt", context));
				allEmails.add(email);				
			}	
			try {
				mailService.sendAllHTMLEmails(allEmails);
				sessionBean.setFlashMessage("Email(s) sent");
			}
			catch (MessagingException e) {
				e.printStackTrace();
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
	public String showFinanceSummary(@RequestParam(name = "show") boolean show, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {				
			sessionBean.setFinanceSummaryVisible(show);
			returnPage = sessionBean.getRedirectUrl();
		}		
		return returnPage;

	}		
	
}