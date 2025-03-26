package com.afterschoolclub.controller;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

import com.afterschoolclub.SessionBean;
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
import com.afterschoolclub.service.DisplayHelperService;
import com.afterschoolclub.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException; 
  
@Controller
@SessionAttributes({"sessionBean"})
public class ParentController {

	@Autowired	
    private DisplayHelperService displayHelper;
	
	@Autowired	
    private MainController mainController;
	
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
			Iterable<StudentClass> classNames = StudentClass.findAll();
			model.addAttribute("classNames",classNames);
			
			Student s = new Student();
			model.addAttribute("student",s);
			
			model.addAttribute("isEditing",false);
			
			this.setInDialogue(true,model);
			returnPage = "createstudent";		
		}
		return returnPage;
	}

	@GetMapping("/editStudent")
	public String createStudent(@RequestParam(name = "studentId") int studentId, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {
			Student s = Student.findById(studentId);
			model.addAttribute("student",s);			
			Iterable<StudentClass> classNames = StudentClass.findAll();
			model.addAttribute("classNames",classNames);
			model.addAttribute("isEditing",true);			
			this.setInDialogue(true,model);
			returnPage = "createstudent";		
		}
		return returnPage;
	}


	
	@PostMapping("/addStudent")
	public String addStudent(
			@RequestParam(name = "studentId") int studentId,
			@RequestParam(name = "firstName") String firstName,
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
			Student student = null;
			if (studentId == 0) {
				student = new Student();
				sessionBean.getLoggedOnParent().addStudent(student);				
			}
			else {
				student = sessionBean.getLoggedOnParent().getStudentFromId(studentId);
			}
			student.setFirstName(firstName);
			student.setSurname(surname);
			student.setDateOfBirth(dateOfBirth);
			student.setConsentToShare(consentToShare);
			student.setClassId(AggregateReference.to(className));		
			student.setAllergyNoteText(allergyNote);
			student.setHealthNoteText(healthNote);
			student.setDietNoteText(dietNote);
			student.setCarePlanNoteText(careNote);
			student.setMedicationNoteText(medicationNote);
			student.setOtherNoteText(otherNote);
			student.updateTimestamp();
			
			sessionBean.getLoggedOnUser().save();
			if (studentId == 0) {
				model.addAttribute("flashMessage","Added New Child.");
			}
			else {
				model.addAttribute("flashMessage","Updated Child Details.");
			}
				
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
            @RequestParam(name="filterClub", required=true) int filterClub,

    		Model model) 
    {

		this.setInDialogue(false,model);
		sessionBean.getFilter().setDisplayingAttending(attending);
		sessionBean.getFilter().setDisplayingAvailable(available);
		sessionBean.getFilter().setDisplayingUnavailable(unavailable);
		sessionBean.getFilter().setDisplayingMissed(missed);
		sessionBean.getFilter().setDisplayingAttended(attended);
		sessionBean.getFilter().setFilterClubId(filterClub);
		return setupCalendar(model);
    			
    }    
    
	@GetMapping("/viewTransactions")
	public String viewTransactions(Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			LocalDate start = sessionBean.getTransactionStartDate();
			LocalDate end = start.plusMonths(1);
			Parent loggedOnParent = sessionBean.getLoggedOnParent();
			List<ParentalTransaction> transactions = loggedOnParent.getTransactions(start,end);
			
			String openingBalanceStr = loggedOnParent.getFormattedBalanceOn(start);
			String closingBalanceStr = loggedOnParent.getFormattedBalanceOn(end);
			
			model.addAttribute("openingBalance",openingBalanceStr);
			model.addAttribute("closingBalance",closingBalanceStr);
			model.addAttribute("transactions",transactions);
			this.setInDialogue(false,model);
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
			Event event = Event.findById(eventId);								
			Student selectedStudent = sessionBean.getSelectedStudent();
			int cost = selectedStudent.getCostOfEvent(event);
			selectedStudent.deregister(eventId);			
			sessionBean.getLoggedOnParent().addTransaction(new ParentalTransaction(cost,LocalDateTime.now(), ParentalTransaction.Type.REFUND, event.getClub().getTitle()));
			sessionBean.getLoggedOnUser().save();			
			model.addAttribute("flashMessage","Cancelled booking for ".concat(selectedStudent.getFirstName()).concat(" and account refunded."));
			returnPage = setupCalendar(model);			
		}
		return returnPage;
			
	}

	public String setUpSessionOptions(Integer eventId, boolean editOptions, boolean viewOnly,  Model model)  {
		
		return setUpSessionOptions(Event.findById(eventId), editOptions, viewOnly, model);
	}
	
	public String setUpSessionOptions(Event event, boolean editOptions, boolean viewOnly,  Model model)  {			
		String returnPage;
		
		if (event != null) {			
			model.addAttribute("clubSession",event);
			List<User> staff = User.findStaffByEventId(event.getEventId());
			model.addAttribute("staff", staff);
			model.addAttribute("parent", sessionBean.getLoggedOnParent());
			
			
			
			List<Event> recurringEvents = Event.findRecurringEvents(event.getRecurrenceSpecificationId().getId());
			
			model.addAttribute("bookingEndDate", event.getStartDate());
			model.addAttribute("termTimeOnly", event.getRecurrenceSpecification().isTermTimeOnly());			
			model.addAttribute("MonRecurring",event.getRecurrenceSpecification().isOccursMonday());
			model.addAttribute("TueRecurring",event.getRecurrenceSpecification().isOccursTuesday());			
			model.addAttribute("WedRecurring",event.getRecurrenceSpecification().isOccursWednesday());
			model.addAttribute("ThurRecurring",event.getRecurrenceSpecification().isOccursThursday());
			model.addAttribute("FriRecurring",event.getRecurrenceSpecification().isOccursFriday());
			model.addAttribute("SatRecurring",event.getRecurrenceSpecification().isOccursSaturday());
			model.addAttribute("SunRecurring",event.getRecurrenceSpecification().isOccursSunday());
					
			List<String> selectedStudents = new ArrayList<String>();
			
			Set <Student> allChildren; 
			if (sessionBean.isLoggedOn()) {
				allChildren = sessionBean.getLoggedOnParent().getStudents();
				for (Student student :allChildren) {
					if (event.registered(student)) {
						selectedStudents.add(student.getIdAsString());
					}
				}
			}
			else {
				allChildren = new HashSet<Student>();
			}
			
			
			if (!editOptions && !viewOnly) {
				selectedStudents.add(sessionBean.getSelectedStudent().getIdAsString());
			}
			
			model.addAttribute("selectedStudents", selectedStudents);			
					
			logger.info("Event staff are {}", event.getStaff());

			model.addAttribute("editOptions", editOptions);
			model.addAttribute("recurringEvents", recurringEvents);
			
			model.addAttribute("viewOnly", viewOnly);						
			model.addAttribute("displayHelper", displayHelper);			
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

		return setUpSessionOptions(eventId, false, true, model);		
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
	        		sessionBean.getLoggedOnUser().save();					
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
	
	@GetMapping("/cancelRegisterForEvent")
	public String cancelRegisterForEvent(Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			User tmpUser = User.findById(sessionBean.getLoggedOnUser().getUserId());  // Reload user so no failed bookings already present.
			sessionBean.setLoggedOnUser(tmpUser);

			returnPage = setupCalendar(model);
		}
		return returnPage;		
	}
	
	@PostMapping("/confirmRegisterForEvent")
	public String confirmRegisterForEvent(@RequestParam Map<String, String> allParams,
			@RequestParam(name = "MonRecurring", required = false) Boolean MonRecurring,
			@RequestParam(name = "TueRecurring", required = false) Boolean TueRecurring,
			@RequestParam(name = "WedRecurring", required = false) Boolean WedRecurring,
			@RequestParam(name = "ThurRecurring", required = false) Boolean ThurRecurring,
			@RequestParam(name = "FriRecurring", required = false) Boolean FriRecurring,
			@RequestParam(name = "SatRecurring", required = false) Boolean SatRecurring,
			@RequestParam(name = "SunRecurring", required = false) Boolean SunRecurring,
			@RequestParam(name = "termTimeOnly", required = false) Boolean termTimeOnly,
			@RequestParam(name = "bookingEndDate", required = false) LocalDate bookingEndDate,

			Model model) {

		String returnPage = validateIsParent(model);
		if (returnPage == null) {
			int eventId = Integer.parseInt(allParams.getOrDefault("eventId", "0"));

			User tmpUser = User.findById(sessionBean.getLoggedOnUser().getUserId());  // Reload user so no failed bookings already present.
			sessionBean.setLoggedOnUser(tmpUser);
				

			Parent loggedOnParent = tmpUser.getParent(); 
			Event event = Event.findById(eventId);

			if (bookingEndDate == null) {
				bookingEndDate =  event.getStartDateTime().toLocalDate();
			}
			boolean recurringBooking = event.getStartDateTime().toLocalDate().compareTo(bookingEndDate) != 0;

			List<Event> allEvents = null;

			if (recurringBooking) {
				allEvents = Event.findRecurringEvents(event.getRecurrenceSpecificationId().getId());
			} else {
				allEvents = new ArrayList<Event>();
				allEvents.add(event);
			}

			int totalCost = 0;

			Set<Student> students = loggedOnParent.getStudents();

			List <Student> bookedStudents = new ArrayList<Student>();
			List<String> allErrorMessages = new ArrayList<String>();

			
			int sessionCount = 0;
			
			
			for (Student student : students) {
				int id = student.getStudentId();
				String queryParamStudentAttending = "student-".concat(String.valueOf(id))
						.concat("-Attending");
				if (allParams.getOrDefault(queryParamStudentAttending, "off").equals("on")) {
					bookedStudents.add(student);
				}
			}
			
			
			if (bookedStudents.size() == 0) {
				allErrorMessages.add("No students selected for booking.");	
			}
			else {
				for (Event specificEvent : allEvents) {
					boolean tryToBook = !recurringBooking 
							|| (specificEvent.getStartDateTime().isAfter(LocalDateTime.now())
							&& !specificEvent.getStartDateTime().toLocalDate().isAfter(bookingEndDate) 
							&& specificEvent.getStartDateTime().compareTo(event.getStartDateTime()) >=0);
	
					if (tryToBook && recurringBooking) {
						switch (specificEvent.getStartDateTime().getDayOfWeek()) {
						case MONDAY:
							tryToBook = (MonRecurring == null) ? false : MonRecurring.booleanValue();
							break;
						case TUESDAY:
							tryToBook = (TueRecurring == null) ? false : TueRecurring.booleanValue();
							break;
						case WEDNESDAY:
							tryToBook = (WedRecurring == null) ? false : WedRecurring.booleanValue();
							break;
						case THURSDAY:
							tryToBook = (ThurRecurring == null) ? false : ThurRecurring.booleanValue();
							break;
						case FRIDAY:
							tryToBook = (FriRecurring == null) ? false : FriRecurring.booleanValue();
							break;
						case SATURDAY:
							tryToBook = (SatRecurring == null) ? false : SatRecurring.booleanValue();
							break;
						case SUNDAY:
							tryToBook = (SunRecurring == null) ? false : SunRecurring.booleanValue();
							break;
						default:
							tryToBook = false;
							break;
						}
					}
	
					if (tryToBook) {
						sessionCount++;
						if (specificEvent.getEventId() == event.getEventId()) {
							event = specificEvent;
						}
						for (Student student : bookedStudents) {
							if (!specificEvent.canAttend(student)) {
								allErrorMessages.add("Booking failed due to ".concat(student.getFirstName())
										.concat(" being ineligible"));
							}
							else if (!student.isAttendingEvent(specificEvent)) {
								totalCost += specificEvent.getClub().getBasePrice();
								Attendee attendee = new Attendee(AggregateReference.to(specificEvent.getEventId()),
										student.getStudentId());
								student.addAttendee(attendee);

								List<MenuGroup> menuGroups = specificEvent.getMenuGroups();
								for (MenuGroup menuGroup : menuGroups) {
									String optionQueryParam = "student-".concat(String.valueOf(student.getStudentId()))
											.concat("-").concat(menuGroup.getName().replace(' ', '-'));
									String optionValue = allParams.getOrDefault(optionQueryParam, "None");
									if (!optionValue.equals("None")) {
										int menuOptionId = Integer.parseInt(optionValue);
										AttendeeMenuChoice amc = new AttendeeMenuChoice(
												AggregateReference.to(menuGroup.getMenuGroupOptionId(menuOptionId)));
										attendee.addAttendeeMenuChoice(amc);
										MenuOption menuOption = menuGroup.getMenuOption(menuOptionId);
										totalCost += menuOption.getAdditionalCost();
									}
								}	
							}
							else {
								String message = String.format("Booking failed due to %s already attending session on %s.", student.getFirstName(), specificEvent.getStartDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));								
								allErrorMessages.add(message);
							}
						}
						if ((specificEvent.getNumberAttendees() + bookedStudents.size()) > specificEvent.getMaxAttendees()) {
							allErrorMessages.add("Booking failed due to maximum attendees exceeded for session on "
									.concat(specificEvent.getStartDate()));
						}
					}
				}
			}

			if (loggedOnParent.getBalance() < totalCost) {
				allErrorMessages.add("Not enough funds to attend this session. Please top up your account.");
			}

			if (allErrorMessages.size() == 0) {
				if (totalCost > 0) {
					loggedOnParent.addTransaction(new ParentalTransaction(-totalCost, LocalDateTime.now(),
							ParentalTransaction.Type.PAYMENT, event.getClub().getTitle()));
				}

				tmpUser.save();
				String studentNames = "";
				int i = 0;			
				for (Student student : bookedStudents) {
					i++;							
					studentNames += student.getFirstName();
					
					if (i == (bookedStudents.size() - 1)) {
						studentNames += " and ";
					}
					else if (i != bookedStudents.size()) {
						studentNames += ",";
					}											
				}				
				
				String message = String.format("Booked %d session(s) at %s for %s", sessionCount, event.getClub().getTitle(), studentNames); 
						
				model.addAttribute("flashMessage", message);

				Student selectedStudent = sessionBean.getSelectedStudent();
				sessionBean.setSelectedStudent(loggedOnParent.getStudentFromId(selectedStudent.getStudentId()));
				returnPage = setupCalendar(model);
			} else {
				model.addAttribute("flashMessages", allErrorMessages);
				returnPage = setUpSessionOptions(event, false, false, model);
				
				model.addAttribute("bookingEndDate", bookingEndDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
				model.addAttribute("termTimeOnly", termTimeOnly == null ? false : termTimeOnly.booleanValue());			
				model.addAttribute("MonRecurring",MonRecurring == null ? false : MonRecurring.booleanValue());
				model.addAttribute("TueRecurring", TueRecurring == null ? false : TueRecurring.booleanValue());			
				model.addAttribute("WedRecurring", WedRecurring == null ? false : WedRecurring.booleanValue());
				model.addAttribute("ThurRecurring", ThurRecurring == null ? false : ThurRecurring.booleanValue());
				model.addAttribute("FriRecurring", FriRecurring == null ? false : FriRecurring.booleanValue());
				model.addAttribute("SatRecurring", SatRecurring == null ? false : SatRecurring.booleanValue());
				model.addAttribute("SunRecurring", SunRecurring == null ? false : SunRecurring.booleanValue());		
				
				
				List<String> selectedStudents = new ArrayList<String>();
				for (Student student : bookedStudents) {
					selectedStudents.add(student.getIdAsString());					
				}
				model.addAttribute("selectedStudents", selectedStudents);
				
				
				
			}

		}
		return returnPage;

	}
	

	@PostMapping("/confirmUpdateOptionsForSession")
	public String confirmUpdateOptionsForSession(@RequestParam Map<String,String> allParams, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			int eventId = Integer.parseInt(allParams.getOrDefault("eventId", "0"));
			User tmpUser = User.findById(sessionBean.getLoggedOnUser().getUserId());
			
			Parent loggedOnParent = tmpUser.getParent();
			Event event = Event.findById(eventId);

			
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
							AttendeeMenuChoice amc = new AttendeeMenuChoice(AggregateReference.to(menuGroup.getMenuGroupOptionId(menuOptionId)));
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
				tmpUser.save();
				
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
			sessionBean.getLoggedOnUser().setTelephoneNum(telephoneNum);
			loggedOnParent.setAltContactName(altContactName);
			loggedOnParent.setAltTelephoneNum(altTelephoneNum);
			sessionBean.getLoggedOnUser().save();			
			model.addAttribute("flashMessage","Profile has been updated");
			returnPage = setupCalendar(model);
		}
		return returnPage;
	}
	
	
	
}
