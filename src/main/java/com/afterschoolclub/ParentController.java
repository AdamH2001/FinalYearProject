package com.afterschoolclub;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import org.springframework.web.servlet.view.RedirectView;

import com.afterschoolclub.data.Attendee;
import com.afterschoolclub.data.AttendeeMenuChoice;
import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.MedicalNote;
import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.StudentClass;
import com.afterschoolclub.data.User;
import com.afterschoolclub.data.repository.ClassRepository;
import com.afterschoolclub.data.repository.EventRepository;
import com.afterschoolclub.data.repository.ParentalTransactionRepository;
import com.afterschoolclub.data.repository.UserRepository;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException; 
  
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
	
	@Autowired	
    private PaypalService paypalService;
	
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
			model.addAttribute("flashMessage","Added New Child.");						
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
    		sessionBean.getFilter().setDisplayingAttending(attending);
    		sessionBean.getFilter().setDisplayingAvailable(available);
    		sessionBean.getFilter().setDisplayingUnavailable(unavailable);
    		sessionBean.getFilter().setDisplayingMissed(missed);
    		sessionBean.getFilter().setDisplayingAttended(attended);
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
			model.addAttribute("flashMessage","Session does not exist");
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
		

    @PostMapping("/paymentcreate")
    public RedirectView createPayment(
            @RequestParam("amount") String amount,
            Model model) {
		
		RedirectView returnView = null;
		String returnPage = validateIsParent(model);		
		if (returnPage == null) {	
	        try {
	            String cancelUrl = "http://localhost:8080/AfterSchoolClub/paymentcancel";
	            String successUrl = "http://localhost:8080/AfterSchoolClub/reviewpayment";
	            Payment payment = paypalService.createPayment(
	                    Integer.valueOf(amount),
	                    "GBP",
	                    "Paypal",
	                    "sale",
	                    "AfterSchool Club TopUp",
	                    cancelUrl,
	                    successUrl,
	                    paypalService.getDefaultWebProfileId());
	            
	            
	            for (Links links: payment.getLinks()) {
	                if (links.getRel().equals("approval_url")) {
	                	returnView = new RedirectView(links.getHref());
	                }
	            }
	        } catch (PayPalRESTException e) {
	        	logger.error("Error occurred:: ", e);
		        returnView = new RedirectView("/paymenterror");
	        }
		}
		else {
			returnView = new RedirectView (returnPage);
		}
		return returnView;
    }

    @PostMapping("/paymentsuccess")
    public String paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {
			try {
	            Payment payment = paypalService.executePayment(paymentId, payerId);
	            if (payment.getState().equals("approved")) {
	            	String id = payment.getId();
	            	logger.info("Payment id = {} @ {}" , id, payment.getCreateTime());	            	
	            	List <Transaction> transactions = payment.getTransactions();
	        		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	        		LocalDateTime paymentDateTime = LocalDateTime.parse(payment.getCreateTime(), formatter);
	        		for (Transaction transaction : transactions) {						
						String amount = transaction.getAmount().getTotal();
						int amountInPence  = (int)Double.parseDouble(amount) * 100;		        		
						sessionBean.getLoggedOnParent().addTransaction(new ParentalTransaction(amountInPence, paymentDateTime,ParentalTransaction.Type.DEPOSIT, "Paypal"));
	
		        		//TODO need to add paypal reference in transaction 
		        				        		
						logger.info("Amount = {}", amount);
					}
					userRepository.save(sessionBean.getLoggedOnUser());					
					model.addAttribute("flashMessage","Payment Successful");					
	            }
	        } catch (PayPalRESTException e) {	        	
				model.addAttribute("flashMessage",e.getDetails().getMessage());
	        	logger.error("Error occurred:: ", e);	        	
	        }
	        returnPage = setupCalendar(model);
    	}
    	return returnPage;
    }

    @GetMapping("/reviewpayment")
    public String reviewPayment(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            Model model) {
    	
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			try {
		        Payment payment = paypalService.getPayment(paymentId);   
				model.addAttribute("paymentId",paymentId);
				model.addAttribute("payerId",payerId);
	        	List <Transaction> transactions = payment.getTransactions();	        
	        	int amountInPence = 0;	    				
				for (Transaction transaction : transactions) {					
					String amount = transaction.getAmount().getTotal();
					amountInPence  += (int)Double.parseDouble(amount) * 100;
	        		
				}
				NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);				
				model.addAttribute("amount",  n.format(amountInPence / 100.0));				
	        	logger.info("Amount in pence {}", amountInPence);
				this.setInDialogue(true,model);
				returnPage = "reviewPayment";
    		}  catch (PayPalRESTException e) {	        
				model.addAttribute("flashMessage",e.getDetails().getMessage());
	        	logger.error("Error occurred:: ", e);
	        	returnPage = setupCalendar(model);	        	 	        	
	        }	      
    	}
    	return returnPage;
    }
    
    
    @GetMapping("/paymentcancel")
    public String paymentCancel(Model model) {
		String returnPage = validateIsParent(model);
		model.addAttribute("flashMessage","Payment Cancelled");		
		if (returnPage == null) {	
			this.setInDialogue(false,model);			
			returnPage = setupCalendar(model);
		}
		return returnPage;
    }

    @GetMapping("/paymenterror")
    public String paymentError(Model model) {
		String returnPage = validateIsParent(model);
		model.addAttribute("flashMessage","Payment Error");
		if (returnPage == null) {	
			this.setInDialogue(false,model);			
			returnPage = setupCalendar(model);
		}
		return returnPage;
    }
    
	@GetMapping("/topUpBalance")
	public String topUpBalance(Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {			
			this.setInDialogue(true,model);	
			returnPage = "topup";
		}
		return returnPage;			
	}
		
	
	@GetMapping("/createMedicalNote")
	public String createMedicalNote(@RequestParam (name="studentId") int studentId, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			Student student = sessionBean.getLoggedOnParent().getStudentFromId(studentId);
			model.addAttribute("student",student);
			this.setInDialogue(true,model);

			returnPage = "createmedicalnote";
		}
		return returnPage;

	}	
	
	
	
	@PostMapping("/confirmRegisterForEvent")
	public String confirmRegisterForEvent(@RequestParam Map<String,String> allParams, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			int eventId = Integer.parseInt(allParams.getOrDefault("eventId", "0"));
			
			User tmpUser = userRepository.findById(sessionBean.getLoggedOnUser().getUserId()).get();
			
			Parent loggedOnParent = tmpUser.getParent();
			Optional<Event> events = eventRepository.findById(eventId);
			Event event = events.get();
			
			int totalCost = 0;
			int studentCount = 0;
			Student inEligibleStudent = null;
			
			Set<Student> students = loggedOnParent.getStudents();
			for (Student student : students) {
				int id = student.getStudentId();
				String queryParamStudentAttending = "student-".concat(String.valueOf(id)).concat("-Attending"); 
				if (allParams.getOrDefault(queryParamStudentAttending, "off").equals("on")) {
					studentCount++;
					totalCost += event.getClub().getBasePrice();
					Attendee attendee = new Attendee(AggregateReference.to(eventId), student.getStudentId());
					student.addAttendee(attendee);
					if (!event.canAttend(student)) {
						inEligibleStudent = student;
					}
						
					
					List <MenuGroup> menuGroups = event.getMenuGroups();
					for (MenuGroup menuGroup: menuGroups) {							
						String optionQueryParam = "student-".concat(String.valueOf(student.getStudentId())).concat("-").concat(menuGroup.getName().replace(' ', '-'));
						String optionValue = allParams.getOrDefault(optionQueryParam, "None");
						if (!optionValue.equals("None")) {
							int menuOptionId = Integer.parseInt(optionValue);
							AttendeeMenuChoice amc = new AttendeeMenuChoice(AggregateReference.to(menuOptionId));
							attendee.addAttendeeMenuChoice(amc);
							MenuOption menuOption = menuGroup.getMenuOption(menuOptionId);
							totalCost += menuOption.getAdditionalCost();									
						}														

					}						
				}
			}				
			if (inEligibleStudent != null) {
				model.addAttribute("flashMessage", "Booking failed due to ".concat(inEligibleStudent.getFirstName()).concat(" being ineligible"));
				return setupCalendar(model);
				
			}
			else if ((event.getNumberAttendees() + studentCount) <= event.getMaxAttendees()) {
				if (loggedOnParent.getBalance() >= totalCost) {
					if (studentCount > 0) {
						if ( totalCost > 0) {
							loggedOnParent.addTransaction(new ParentalTransaction(-totalCost,LocalDateTime.now(),ParentalTransaction.Type.PAYMENT, event.getClub().getTitle()));
						}
						
						userRepository.save(tmpUser);
						model.addAttribute("flashMessage", "Booked ".concat(event.getClub().getTitle()));
						sessionBean.setLoggedOnUser(tmpUser);
						
						Student selectedStudent = sessionBean.getSelectedStudent();
						sessionBean.setSelectedStudent(loggedOnParent.getStudentFromId(selectedStudent.getStudentId()));
						
					}
					else {
						model.addAttribute("flashMessage", "No students selected for booking.");
					}
				}
				else {
					model.addAttribute("flashMessage", "Not enough funds to attend this session. Please top up your account.");
				}
			}
			else {
				model.addAttribute("flashMessage", "Booking failed due to maximum attendees exceeded.");
			}
			returnPage = setupCalendar(model);
		}
		return returnPage;

	}
	

	@PostMapping("/confirmUpdateOptionsForSession")
	public String confirmUpdateOptionsForSession(@RequestParam Map<String,String> allParams, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			int eventId = Integer.parseInt(allParams.getOrDefault("eventId", "0"));
			User tmpUser = userRepository.findById(sessionBean.getLoggedOnUser().getUserId()).get();
			
			Parent loggedOnParent = tmpUser.getParent();
			Optional<Event> events = eventRepository.findById(eventId);
			Event event = events.get();
			
			int totalCost = 0;
			int totalOriginalCost = 0;
			
			Set<Student> students = loggedOnParent.getStudents();
			for (Student student : students) {
				int id = student.getStudentId();
				String queryParamStudentAttending = "student-".concat(String.valueOf(id)).concat("-Attending"); 
				if (allParams.getOrDefault(queryParamStudentAttending, "off").equals("on")) {
					totalOriginalCost += student.getCostOfEvent(event);
					
					totalCost += event.getClub().getBasePrice();
					Attendee attendee = student.getAttendee(event);
					attendee.clearAttendeeMenuChoices();	
					
					List <MenuGroup> menuGroups = event.getMenuGroups();
					for (MenuGroup menuGroup: menuGroups) {							
						String optionQueryParam = "student-".concat(String.valueOf(student.getStudentId())).concat("-").concat(menuGroup.getName().replace(' ', '-'));
						String optionValue = allParams.getOrDefault(optionQueryParam, "None");
						if (!optionValue.equals("None")) {
							int menuOptionId = Integer.parseInt(optionValue);
							AttendeeMenuChoice amc = new AttendeeMenuChoice(AggregateReference.to(menuOptionId));
							attendee.addAttendeeMenuChoice(amc);
							MenuOption menuOption = menuGroup.getMenuOption(menuOptionId);
							totalCost += menuOption.getAdditionalCost();									
						}														

					}						
				}
			}				
			int costDifference = totalCost - totalOriginalCost;
			
			if (loggedOnParent.getBalance() >= costDifference) {
				if ( costDifference > 0) {
					loggedOnParent.addTransaction(new ParentalTransaction(-costDifference,LocalDateTime.now(),ParentalTransaction.Type.PAYMENT, "Option changes for ".concat(event.getClub().getTitle())));
				}
				else if ( costDifference < 0) {
					loggedOnParent.addTransaction(new ParentalTransaction(-costDifference,LocalDateTime.now(),ParentalTransaction.Type.REFUND, "Option changes for ".concat(event.getClub().getTitle())));
				}
				userRepository.save(tmpUser);
				
				model.addAttribute("flashMessage", "Updated Options for ".concat(event.getClub().getTitle()));
				sessionBean.setLoggedOnUser(tmpUser);
				
				Student selectedStudent = sessionBean.getSelectedStudent();
				sessionBean.setSelectedStudent(loggedOnParent.getStudentFromId(selectedStudent.getStudentId()));
			}
			else {
				model.addAttribute("flashMessage", "Not enough funds to attend this event. Please top up your account.");
			}		
			returnPage = setupCalendar(model); 
		}				

		return returnPage;
	}	
	
	@PostMapping("/changeStudentField")
	public String changeStudentField(@RequestParam (name="students") int studentId, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {				
			sessionBean.setSelectedStudent(sessionBean.getLoggedOnParent().getStudentFromId(studentId));			
			logger.info("Selected Student = {} for id {}",sessionBean.getSelectedStudent(), studentId);
			returnPage = setupCalendar(model);			
		}
		return returnPage;
	}	

	@PostMapping("/saveUserDetails")
	public String saveUserDetails(@RequestParam(name = "firstName") String firstName,
			@RequestParam(name = "surname") String surname, @RequestParam(name = "email") String email,
			@RequestParam(name = "telephoneNum") String telephoneNum, @RequestParam(name = "altContactName") String altContactName, @RequestParam (name = "altTelephoneNum") String altTelephoneNum, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			Parent loggedOnParent = sessionBean.getLoggedOnParent();
			sessionBean.getLoggedOnUser().setFirstName(firstName);
			sessionBean.getLoggedOnUser().setSurname(surname);
			loggedOnParent.setTelephoneNum(telephoneNum);
			loggedOnParent.setAltContactName(altContactName);
			loggedOnParent.setAltTelephoneNum(altTelephoneNum);
			userRepository.save(sessionBean.getLoggedOnUser());			
			model.addAttribute("flashMessage","Profile has been updated");
			returnPage = setupCalendar(model);
		}
		return returnPage;
	}
	
	
	
}
