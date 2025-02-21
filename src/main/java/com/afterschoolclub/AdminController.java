package com.afterschoolclub;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.Incident;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.repository.EventRepository;
import com.afterschoolclub.data.repository.StudentRepository; 
  
@Controller
@SessionAttributes({"sessionBean"})
public class AdminController {

	@Autowired	
    private MainController mainController;
	
    private final SessionBean sessionBean;	
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
	private StudentRepository studentRepository;
    
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
			sessionBean.setOnlyMineFilter(onlyMine!=null);
			sessionBean.setAdminFilter(SessionBean.AdminFilter.valueOf(adminFilter));	    		    
			returnPage = setupCalendar(model);
		}		
		return returnPage;
    }   


	@PostMapping("/addIncident")
	public String addIncident(@RequestParam(name = "attendee") int attendeeId,
			@RequestParam(name = "summary") String summary, int eventId, Model model) {
		String returnPage = validateIsAdmin(model);
		if (returnPage == null) {
			Optional<Event> eventOptional = eventRepository.findById(eventId);
			if (eventOptional.isPresent()) {	
				Event event = eventOptional.get();
				Attendee attendee = event.getAttendee(attendeeId);
				Incident newIncident = new Incident(AggregateReference.to(event.getEventId()), summary);
				attendee.addIncident(newIncident);
				eventRepository.save(event);
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
			Optional<Event> event = eventRepository.findById(eventId);
			if (event.isPresent()) {
				for (Attendee attendee: event.get().getAttendees()) {
					List<Student> studList = studentRepository.findByAttendeeId(attendee.getAttendeeId());
					
					for (Student student: studList) {
						logger.info("student = {}", student);
						attendee.setStudent(student);
					}
					
				}
				model.addAttribute("event",event.get());
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
			Optional<Event> event = eventRepository.findById(eventId);
			if (event.isPresent()) {
				for (Attendee attendee: event.get().getAttendees()) {
					List<Student> studList = studentRepository.findByAttendeeId(attendee.getAttendeeId());
					
					for (Student student: studList) {
						logger.info("student = {}", student);
						attendee.setStudent(student);
					}					
				}
				model.addAttribute("event",event.get());
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
			String eventId = register.get("eventId");
			Optional<Event> eventOptional = eventRepository.findById(Integer.parseInt(eventId));
			if (eventOptional.isPresent()) {
				Event event = eventOptional.get();			
				for (Map.Entry<String, String> entry : register.entrySet()) {
			        System.out.println(entry.getKey() + ":" + entry.getValue());
			        if (entry.getKey().startsWith("attendee_")) {
			        	int attendeeId = Integer.parseInt(entry.getKey().substring(9));
			        	Attendee attendee = event.getAttendee(attendeeId);
			        	attendee.setAttended(entry.getValue().equals("on"));
			        }
			    }
				eventRepository.save(event);
				model.addAttribute("flashMessage","Recorded Register");	
			}
			else {
				model.addAttribute("flashMessage","Internal error cannot find event.");
			}
			
			returnPage = setupCalendar(model);
		}
		return returnPage;
		
	}
		
}
