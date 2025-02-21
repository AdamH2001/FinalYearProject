package com.afterschoolclub;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.MedicalNote;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.StudentClass;
import com.afterschoolclub.data.User;
import com.afterschoolclub.data.repository.ClassRepository;
import com.afterschoolclub.data.repository.EventRepository;
import com.afterschoolclub.data.repository.ParentalTransactionRepository;
import com.afterschoolclub.data.repository.UserRepository; 
  
@Controller
@SessionAttributes({"sessionBean"})
public class ParentController {

	@Autowired	
    private MainController mainController;
	
	@Autowired	
    private ClassRepository classRepository;
	
	@Autowired	
    private UserRepository userRepository;
	
    @Autowired
    private EventRepository eventRepository;
	
	@Autowired	
    private ParentalTransactionRepository parentalTransactionRepository;	
	
	static Logger logger = LoggerFactory.getLogger(ParentController.class);

		
    private final SessionBean sessionBean;
	
    @Autowired
    public ParentController(SessionBean sessionBean) {
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
	
    
	public String validateIsParent(Model model)
	{
		String returnPage = null;
		model.addAttribute("sessionBean", sessionBean);
		
		if (sessionBean.getLoggedOnUser() != null) {
			if (!sessionBean.getLoggedOnUser().isParent()) {
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
	
	@GetMapping("/createStudent")
	public String createStudent(Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {
			List<StudentClass> classNames = classRepository.findAll();
			model.addAttribute("classNames",classNames);
			this.setInDialogue(true,model);
			returnPage = "createstudent";		
		}
		return returnPage;
	}



	
	@PostMapping("/addStudent")
	public String addStudent(@RequestParam(name = "firstName") String firstName,
			@RequestParam(name = "surname") String surname, @RequestParam(name = "className") int className,
			@RequestParam(name = "dateOfBirth") LocalDate dateOfBirth,
			@RequestParam(name = "allergyNote") String allergyNote,
			@RequestParam(name = "healthNote") String healthNote, @RequestParam(name = "dietNote") String dietNote,
			@RequestParam(name = "careNote") String careNote,
			@RequestParam(name = "medicationNote") String medicationNote,
			@RequestParam(name = "otherNote") String otherNote,
			@RequestParam(name = "consentToShare", defaultValue = "false") boolean consentToShare, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {
			Student student = new Student(firstName, surname, dateOfBirth, consentToShare);
			student.setClassId(AggregateReference.to(className));		
			student.addMedicalNote(new MedicalNote(MedicalNote.Type.ALLERGY,allergyNote));
			student.addMedicalNote(new MedicalNote(MedicalNote.Type.HEALTH,healthNote));
			student.addMedicalNote(new MedicalNote(MedicalNote.Type.DIET, dietNote));
			student.addMedicalNote(new MedicalNote(MedicalNote.Type.CAREPLAN,careNote));
			student.addMedicalNote(new MedicalNote(MedicalNote.Type.MEDICATION,medicationNote));
			student.addMedicalNote(new MedicalNote(MedicalNote.Type.OTHER,otherNote));
			sessionBean.getLoggedOnParent().addStudent(student);
			userRepository.save(sessionBean.getLoggedOnUser());
			returnPage = setupCalendar(model);
		}
		return returnPage;
	}


    @PostMapping("/updateFilters")
    public String updateFilters(
            @RequestParam(name="attending", required=false, defaultValue="false") Boolean attending,
            @RequestParam(name="available", required=false, defaultValue="false") Boolean available,
            @RequestParam(name="unavailable", required=false, defaultValue="false") Boolean unavailable,
            @RequestParam(name="missed", required=false, defaultValue="false") Boolean missed,
            @RequestParam(name="attended", required=false, defaultValue="false") Boolean attended,
            
    		Model model) 
    {
    	String returnPage = validateIsParent(model);
    	if (returnPage == null) {
    		this.setInDialogue(false,model);
    		sessionBean.setDisplayingAttending(attending);
    		sessionBean.setDisplayingAvailable(available);
    		sessionBean.setDisplayingUnavailable(unavailable);
    		sessionBean.setDisplayingMissed(missed);
    		sessionBean.setDisplayingAttended(attended);
    		returnPage = setupCalendar(model);
    	}
		return returnPage;		
    }    
    
	@GetMapping("/viewTransactions")
	public String viewTransactions(Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			LocalDate start = sessionBean.getTransactionStartDate();
			LocalDate end = start.plusMonths(1);
			Parent loggedOnParent = sessionBean.getLoggedOnParent();
			List<ParentalTransaction> transactions = parentalTransactionRepository.getMonthlyTransactions(loggedOnParent.getParentId(),start,end);
			Integer openingBalance = parentalTransactionRepository.getBalance(loggedOnParent.getParentId(), start);
			String openingBalanceStr;
			NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);
			if (openingBalance == null) {
				openingBalanceStr = n.format(0);
			}
			else {
				openingBalanceStr = n.format(openingBalance.intValue()/100.0);
			}
			 				 
			Integer closingBalance = parentalTransactionRepository.getBalance(loggedOnParent.getParentId(), end);
			String closingBalanceStr = "";
			if (closingBalance == null) {
				closingBalanceStr = n.format(0);
			}
			else {
				closingBalanceStr = n.format(closingBalance.intValue()/100.0);
			}
			model.addAttribute("openingBalance",openingBalanceStr);
			model.addAttribute("closingBalance",closingBalanceStr);
			model.addAttribute("transactions",transactions);
			this.setInDialogue(true,model);
			returnPage= "viewtransactions";
		}
		return returnPage;
		
		
	}
	
	@GetMapping("/transactionBack")
	public String transactionBack(Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {		
			this.setInDialogue(true,model);			
			sessionBean.setTransactionStartDate(sessionBean.getTransactionStartDate().minusMonths(1));
			returnPage = "redirect:./viewTransactions";
		}
		return returnPage;			
	}
	
	@GetMapping("/transactionForward")
	public String transactionForward(Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {		
			this.setInDialogue(true,model);			
			sessionBean.setTransactionStartDate(sessionBean.getTransactionStartDate().plusMonths(1));
			returnPage = "redirect:./viewTransactions";
		}
		return returnPage;	
	}
	
	@GetMapping("/deregisterForEvent")
	public String deregisterForEvent(@RequestParam (name="eventId") int eventId, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {		
			Optional<Event> events = eventRepository.findById(eventId);
			Event event = events.get();
			Student selectedStudent = sessionBean.getSelectedStudent();
			int cost = selectedStudent.getCostOfEvent(event);
			selectedStudent.deregister(eventId);			
			sessionBean.getLoggedOnParent().addTransaction(new ParentalTransaction(cost,LocalDateTime.now(), ParentalTransaction.Type.REFUND, event.getClub().getTitle()));
			userRepository.save(sessionBean.getLoggedOnUser());			
			model.addAttribute("flashMessage","Cancelled booking for ".concat(selectedStudent.getFirstName()).concat(" and account refunded."));
			returnPage = setupCalendar(model);			
		}
		return returnPage;
			
	}
	
	public String setUpSessionOptions(Integer eventId, boolean editOptions, boolean viewOnly,  Model model)  {
		String returnPage;
		Optional<Event> event = eventRepository.findById(eventId);
		if (event.isPresent()) {					
			Event eventToView = event.get();
			model.addAttribute("eventToView",eventToView);
			List<User> staff = userRepository.findStaffByEventId(eventToView.getEventId());
			model.addAttribute("staff", staff);
			model.addAttribute("parent", sessionBean.getLoggedOnParent());
					
			logger.info("Event staff are {}", eventToView.getStaff());

			model.addAttribute("editOptions", editOptions);
			model.addAttribute("viewOnly", viewOnly);						
			model.addAttribute("displayHelper", new DisplayHelper());			
			this.setInDialogue(true,model);

			returnPage = "sessionOptions";
		}
		else {
			model.addAttribute("flashMessage","Event does not exist");
			returnPage = setupCalendar(model);				
		}
		return returnPage;

	}
	
	@GetMapping("/editEventOptions")
	public String editEventOptions(@RequestParam (name="eventId") Integer eventId, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			returnPage = setUpSessionOptions(eventId, true, false, model);				
		}
		return returnPage;
	}	
	
	@GetMapping("/registerForEvent")
	public String registerForEvent(@RequestParam (name="eventId") Integer eventId, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			returnPage = setUpSessionOptions(eventId, false, false, model);							
		}	
		return returnPage;
	}
	
	@GetMapping("/parentViewSession")
	public String parentViewSession(@RequestParam (name="eventId") Integer eventId, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			returnPage = setUpSessionOptions(eventId, false, true, model);							
		}	
		return returnPage;
	}	
		
}
