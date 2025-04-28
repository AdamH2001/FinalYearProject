package com.afterschoolclub.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.data.Attendee;
import com.afterschoolclub.data.AttendeeMenuChoice;
import com.afterschoolclub.data.Session;
import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.StudentClass;
import com.afterschoolclub.data.User;
import com.afterschoolclub.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException; 
import org.springframework.transaction.annotation.Transactional;

/**
 * Controller Class that implements all the end points that are specific to Parents  
 * e.g. Book Session  
 */ 

@Controller
@SessionAttributes({"sessionBean"})
public class ParentController {

	/**
	 * Reference to main controller 
	 */
	@Autowired	
    private MainController mainController;
	
	/**
	 * Utility service to take paypal payments
	 */
	@Autowired	
    private PaypalService paypalService;
	
	/**
	 * Utility logger to record info and warning messages
	 */
	static Logger logger = LoggerFactory.getLogger(ParentController.class);

		
    /**
     * 
     */
    private final SessionBean sessionBean;
	
    /**
     * Spring constructor
     * @param sessionBean
     */
    @Autowired
    public ParentController(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }    
    
    
    
	/**
	 * Uility method to set in dialgoue mode. If is true 
	 * then disables links on navigation base
	 * @param inDialogue - boolean true or false
	 * @param model - holder of context data from view 
	 */
	public void setInDialogue(boolean inDialogue, Model model)
	{
		sessionBean.setInDialogue(inDialogue);
	    model.addAttribute("sessionBean", sessionBean);	    
		return;
	}
	
	/**
	 * Uility method to redirect to calendar page
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	public String setupCalendar(Model model)
	{
		return mainController.setupCalendar(model);		
		
	}
	
    
	/**
	 * Uility function to validate logged on user is a parent
	 * If is rturns null otherwise redirects user to a page with error recorded
	 * 
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	public String validateIsParent(Model model)
	{
		String returnPage = null;
		model.addAttribute("sessionBean", sessionBean);
		
		if (sessionBean.getLoggedOnUser() != null) {
			if (!sessionBean.getLoggedOnUser().isParent()) {
				sessionBean.setFlashMessage("You need to be a parent to perform this action.");	
				setInDialogue(false,model);
				returnPage = sessionBean.getRedirectUrl();
			}
		}
		else {
			sessionBean.setFlashMessage("Please login to perform this action.");
			setInDialogue(false,model);
			returnPage = sessionBean.getRedirectUrl();
		}
		return returnPage;		
	}
	
	/**
	 * End point to add a student
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
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
			returnPage = "student";		
		}
		return returnPage;
	}



    /**
     * End point to update the current filters and refresh the view
     * @param attending
     * @param available
     * @param unavailable
     * @param missed
     * @param attended
     * @param filterClub
     * @param model - holder of context data from view 
     * @return
     */
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
    
	/**
	 * End point to view transactions 
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/viewTransactions")
	public String viewTransactions(Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			LocalDate start = sessionBean.getTransactionStartDate();
			LocalDate end = start.plusMonths(1);
			Parent parent = sessionBean.getLoggedOnParent();
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
						
			sessionBean.setReturnUrl("./viewTransactions");

			this.setInDialogue(false,model);
			returnPage= "viewtransactions";
		}
		return returnPage;
		
		
	}

	
	/**
	 * End point to persist the cancellation of a booking and perform the appropriate refunds
	 * @param sessionId - primary key for the session
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/deregisterForSession")
	public String deregisterForSession(@RequestParam (name="sessionId") int sessionId, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {		
			Session session = Session.findById(sessionId);								
			Student selectedStudent = sessionBean.getSelectedStudent();
			int cost = selectedStudent.getCostOfSession(session);
			selectedStudent.deregister(sessionId);				
			String message = "Cancelled booking for ".concat(session.getClub().getTitle()).concat(" for ").concat(selectedStudent.getFirstName());
			sessionBean.getLoggedOnParent().recordRefundForClub(cost, session.getClub(), message);			
			sessionBean.getLoggedOnUser().save();			
			sessionBean.setFlashMessagePreserve("Cancelled booking for ".concat(selectedStudent.getFirstName()).concat(" - account refunded."));
			returnPage = setupCalendar(model);			
		}
		return returnPage;
			
	}

	/**
	 * Utility function to set up model for view a session booking
	 * @param sessionId - primary key for the session
	 * @param editOptions - boolean indicating whether we are editing options
	 * @param viewOnly - indicator whether we are only viewing the options
	 * @param model - holder of context data from view 
	 * @return
	 */
	public String setUpSessionOptions(Integer sessionId, boolean editOptions, boolean viewOnly,  Model model)  {
		
		return setUpSessionOptions(Session.findById(sessionId), editOptions, viewOnly, model);
	}
	
	/** 
	 * Utility function to set up model for view a session booking
	 * @param session - take a session object 
	 * @param editOptions - boolean indicating whether we are editing options
	 * @param viewOnly - indicator whether we are only viewing the options
	 * @param model - holder of context data from view 
	 * @return
	 */
	public String setUpSessionOptions(Session session, boolean editOptions, boolean viewOnly,  Model model)  {			
		String returnPage;
		
		if (session != null) {			
			model.addAttribute("clubSession",session);
			List<User> staff = User.findStaffBySessionId(session.getSessionId());
			model.addAttribute("staff", staff);
			model.addAttribute("parent", sessionBean.getLoggedOnParent());
			
			
			
			List<Session> recurringSessions = Session.findRecurringSessions(session.getRecurrenceSpecificationId().getId());
			
			model.addAttribute("bookingEndDate", session.getStartDate());
			model.addAttribute("termTimeOnly", session.getRecurrenceSpecification().isTermTimeOnly());			
			model.addAttribute("MonRecurring",session.getRecurrenceSpecification().isOccursMonday());
			model.addAttribute("TueRecurring",session.getRecurrenceSpecification().isOccursTuesday());			
			model.addAttribute("WedRecurring",session.getRecurrenceSpecification().isOccursWednesday());
			model.addAttribute("ThurRecurring",session.getRecurrenceSpecification().isOccursThursday());
			model.addAttribute("FriRecurring",session.getRecurrenceSpecification().isOccursFriday());
			model.addAttribute("SatRecurring",session.getRecurrenceSpecification().isOccursSaturday());
			model.addAttribute("SunRecurring",session.getRecurrenceSpecification().isOccursSunday());
					
			List<String> selectedStudents = new ArrayList<String>();
			
			List <Student> allChildren; 
			if (sessionBean.isLoggedOn()) {
				allChildren = sessionBean.getLoggedOnParent().getStudents();
				for (Student student :allChildren) {
					if (session.registered(student)) {
						selectedStudents.add(student.getIdAsString());
					}
				}
			}
			else {
				allChildren = new ArrayList<Student>();
			}
			
			
			if (!editOptions && !viewOnly) {
				selectedStudents.add(sessionBean.getSelectedStudent().getIdAsString());
			}
			
			model.addAttribute("selectedStudents", selectedStudents);			
					
			logger.info("Session staff are {}", session.getStaff());

			model.addAttribute("editOptions", editOptions);
			model.addAttribute("recurringSessions", recurringSessions);
			
			model.addAttribute("viewOnly", viewOnly);						
			this.setInDialogue(true,model);

			returnPage = "parentSession";
		}
		else {
			sessionBean.setFlashMessage("Session does not exist");
			returnPage = setupCalendar(model);				
		}
		return returnPage;

	}
	
	/**
	 * End point to edit booking options
	 * @param sessionId - primary key for the session
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/editSessionOptions")
	public String editSessionOptions(@RequestParam (name="sessionId") Integer sessionId, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			returnPage = setUpSessionOptions(sessionId, true, false, model);				
		}
		return returnPage;
	}	
	
	/**
	 * End point to book a session
	 * @param sessionId - primary key for the session
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/registerForSession")
	public String registerForSession(@RequestParam (name="sessionId") Integer sessionId, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			returnPage = setUpSessionOptions(sessionId, false, false, model);							
		}	
		return returnPage;
	}
	
	/**
	 * End point to view a session 
	 * @param sessionId - primary key for the session
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/parentViewSession")
	public String parentViewSession(@RequestParam (name="sessionId") Integer sessionId, Model model) {

		return setUpSessionOptions(sessionId, false, true, model);		
	}	
		

    /**
     * End point to redirect to PayPal screen to capture payment 
     * @param amount
     * @param model - holder of context data from view 
     * @return
     */
    @PostMapping("/paymentcreate")
    public RedirectView createPayment(
            @RequestParam("amount") String amount,
            Model model) {
		
		RedirectView returnView = null;
		String returnPage = validateIsParent(model);		
		if (returnPage == null) {	
	        try {
	      //      String cancelUrl = "http://localhost:8080/AfterSchoolClub/paymentcancel";
	       //     String successUrl = "http://localhost:8080/AfterSchoolClub/paymentsuccess";
	            
	            String cancelUrl = sessionBean.getHomePage().concat("/paymentcancel");
	            String successUrl = sessionBean.getHomePage().concat("/paymentsuccess");
	            
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
		        returnView = new RedirectView("paymenterror");
	        }
		}
		else {
			returnView = new RedirectView (returnPage);
		}
		return returnView;
    }

    /**
     * End point is user successfully tops up account
     * This creates the transactions and stores reference to PayPal transaction
     * @param paymentId
     * @param payerId
     * @param model - holder of context data from view 
     * @return
     */
    @Transactional 
    @GetMapping("/paymentsuccess")
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
	        		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	        		//LocalDateTime paymentDateTime = LocalDateTime.parse(payment.getCreateTime(), formatter);
	        		
	        		LocalDateTime paymentDateTime = LocalDateTime.now();
	        		for (Transaction transaction : transactions) {						
						String amount = transaction.getAmount().getTotal();
						int amountInPence  = (int)Double.parseDouble(amount) * 100;
						String transId = transaction.getRelatedResources().get(0).getSale().getId();
						
						ParentalTransaction pt = new ParentalTransaction(amountInPence, paymentDateTime,ParentalTransaction.Type.DEPOSIT, "Paypal");
						pt.setPaymentReference(transId);
						pt.setParent(sessionBean.getLoggedOnParent());
						pt.save();
					}				
					sessionBean.setFlashMessage("Payment Successful");					
	            }
	        } catch (PayPalRESTException e) {	   
	        	if (e.getDetails() != null) {
	        		sessionBean.setFlashMessage(e.getDetails().getMessage());
	        	}
	        	else {
	        		sessionBean.setFlashMessage("Internal Payment Error");
	        	}
	        	logger.error("Error occurred:: ", e);	        	
	        }
	        returnPage = setupCalendar(model);
    	}
    	return returnPage;
    }

 
    
    /**
     * End point if user cancels payment whilst in PayPal
     * @param model - holder of context data from view 
     * @return the next page for the user
     */
    @GetMapping("/paymentcancel")
    public String paymentCancel(Model model) {
		String returnPage = validateIsParent(model);
		sessionBean.setFlashMessage("Payment Cancelled");		
		if (returnPage == null) {	
			this.setInDialogue(false,model);			
			returnPage = setupCalendar(model);
		}
		return returnPage;
    }

    /**
     * End point to for a payment error from PayPal
     * @param model - holder of context data from view 
     * @return the next page for the user
     */
    @GetMapping("/paymenterror")
    public String paymentError(Model model) {
		String returnPage = validateIsParent(model);
		sessionBean.setFlashMessage("Payment Error");
		if (returnPage == null) {	
			this.setInDialogue(false,model);			
			returnPage = setupCalendar(model);
		}
		return returnPage;
    }
    
	/**
	 * End point to top up the users balance from PayPal
	 * @param model - holder of context data from view 
	 * @return the next page for the user the next page for the user
	 */
	@GetMapping("/topUpBalance")
	public String topUpBalance(Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {			
			this.setInDialogue(true,model);	
			returnPage = "topup";
		}
		return returnPage;			
	}
		
	
	/**
	 * End point to cancel the booking for a session
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/cancelRegisterForSession")
	public String cancelRegisterForSession(Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			User tmpUser = User.findById(sessionBean.getLoggedOnUser().getUserId());  // Reload user so no failed bookings already present.
			sessionBean.setLoggedOnUser(tmpUser);

			returnPage = setupCalendar(model);
		}
		return returnPage;		
	}
	
	/**
	 * End point to register for a session
	 * @param allParams
	 * @param MonRecurring
	 * @param TueRecurring
	 * @param WedRecurring
	 * @param ThurRecurring
	 * @param FriRecurring
	 * @param SatRecurring
	 * @param SunRecurring
	 * @param termTimeOnly
	 * @param bookingEndDate
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@PostMapping("/confirmRegisterForSession")
	public String confirmRegisterForSession(@RequestParam Map<String, String> allParams,
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
			int sessionId = Integer.parseInt(allParams.getOrDefault("sessionId", "0"));

			User tmpUser = User.findById(sessionBean.getLoggedOnUser().getUserId());  // Reload user so no failed bookings already present.
			sessionBean.setLoggedOnUser(tmpUser);
				

			Parent loggedOnParent = tmpUser.getParent(); 
			Session session = Session.findById(sessionId);

			if (bookingEndDate == null) {
				bookingEndDate =  session.getStartDateTime().toLocalDate();
			}
			boolean recurringBooking = session.getStartDateTime().toLocalDate().compareTo(bookingEndDate) != 0;

			List<Session> allSessions = null;

			if (recurringBooking) {
				allSessions = Session.findRecurringSessions(session.getRecurrenceSpecificationId().getId());
			} else {
				allSessions = new ArrayList<Session>();
				allSessions.add(session);
			}

			int totalCost = 0;

			List<Student> students = loggedOnParent.getStudents();

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
				for (Session specificSession : allSessions) {
					boolean tryToBook = !recurringBooking 
							|| (specificSession.getStartDateTime().isAfter(LocalDateTime.now())
							&& !specificSession.getStartDateTime().toLocalDate().isAfter(bookingEndDate) 
							&& specificSession.getStartDateTime().compareTo(session.getStartDateTime()) >=0);
	
					if (tryToBook && recurringBooking) {
						switch (specificSession.getStartDateTime().getDayOfWeek()) {
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
						if (specificSession.getSessionId() == session.getSessionId()) {
							session = specificSession;
						}
						for (Student student : bookedStudents) {
							if (!specificSession.canAttend(student)) {
								allErrorMessages.add("Booking failed due to ".concat(student.getFirstName())
										.concat(" being ineligible"));
							}
							else if (!student.isAttendingSession(specificSession)) {
								totalCost += specificSession.getClub().getBasePrice();
								Attendee attendee = new Attendee(AggregateReference.to(specificSession.getSessionId()),
										student.getStudentId());
								student.addAttendee(attendee);

								List<MenuGroup> menuGroups = specificSession.getMenuGroups();
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
								String message = String.format("Booking failed due to %s already attending session on %s.", student.getFirstName(), specificSession.getStartDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));								
								allErrorMessages.add(message);
							}
						}
						if ((specificSession.getNumberAttendees() + bookedStudents.size()) > specificSession.getMaxAttendees()) {
							allErrorMessages.add("Booking failed due to maximum attendees exceeded for session on "
									.concat(specificSession.getStartDate()));
						}
					}
				}
			}

			if (!loggedOnParent.canAfford(totalCost, session.getClub())) {
				allErrorMessages.add("Not enough funds to attend this session, please top up your account.");
			}

			if (allErrorMessages.size() == 0) {
				 
				Iterator<Student> itr = bookedStudents.iterator();
				Student student = itr.next();
				String studentNames = student.getFirstName();
				
				while (itr.hasNext()) {
					student = itr.next();
					
					if (itr.hasNext()) {
						studentNames += ", ";
					}
					else {
						studentNames += " and ";
					}
					studentNames += student.getFirstName();
				}
				
				 
				if (totalCost > 0) {
					
					String description = session.getClub().getTitle().concat(" for ").concat(studentNames);
					loggedOnParent.recordPaymentForClub(totalCost, session.getClub(), description); 
				}

				tmpUser.save();						
				
				String message = String.format("Booked %d session(s) at %s for %s", sessionCount, session.getClub().getTitle(), studentNames); 						
				sessionBean.setFlashMessagePreserve(message);
				sessionBean.setLoggedOnUser(tmpUser);
				
				returnPage = setupCalendar(model);
			} else {
				sessionBean.setFlashMessages(allErrorMessages);
				returnPage = setUpSessionOptions(session, false, false, model);				
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
	

	/**
	 * End point to save the updated details for the booking for a session. 
	 * Ensures parent has enough funds to pay for it otherwise returns an error 
	 * 
	 * @param allParams
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@PostMapping("/confirmUpdateOptionsForSession")
	public String confirmUpdateOptionsForSession(@RequestParam Map<String,String> allParams, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {	
			int sessionId = Integer.parseInt(allParams.getOrDefault("sessionId", "0"));
			User tmpUser = User.findById(sessionBean.getLoggedOnUser().getUserId());
			
			Parent loggedOnParent = tmpUser.getParent();
			Session session = Session.findById(sessionId);

			
			int totalCost = 0;
			int totalOriginalCost = 0;
			
			List<Student> students = loggedOnParent.getStudents();
			for (Student student : students) {
				int id = student.getStudentId();
				String queryParamStudentAttending = "student-".concat(String.valueOf(id)).concat("-Attending"); 
				if (allParams.getOrDefault(queryParamStudentAttending, "off").equals("on")) {
					totalOriginalCost += student.getCostOfSession(session);
					
					totalCost += session.getClub().getBasePrice();
					Attendee attendee = student.getAttendee(session);
					attendee.clearAttendeeMenuChoices();	
					
					List <MenuGroup> menuGroups = session.getMenuGroups();
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
						
			if (costDifference<=0 || loggedOnParent.canAfford(costDifference, session.getClub())) {
				if ( costDifference > 0) {
					loggedOnParent.recordPaymentForClub(costDifference, session.getSessionClub(), "Option changes for ".concat(session.getClub().getTitle()));
				}
				else if ( costDifference < 0) {
					loggedOnParent.recordRefundForClub(costDifference*-1, session.getSessionClub(), "Option changes for ".concat(session.getClub().getTitle()));									
				}				
				tmpUser.save();				
				sessionBean.setFlashMessagePreserve("Updated Options for ".concat(session.getClub().getTitle()));				
				sessionBean.setLoggedOnUser(tmpUser);
				
			}
			else {
				sessionBean.setFlashMessage("Not enough funds to attend this session - please top up your account.");
			}		
			returnPage = setupCalendar(model); 
		}				

		return returnPage;
	}	
	
	/**
	 * End point Updates the current selected student. This will cause 
	 * default actions and 	 * information in header to reflect the newly selected student
	 * 
	 * @param studentId - student identifier for the newly selected student
	 * @param model - holder of context data from view 
	 * @return redirect to the page the user was on when selected the new student
	 */
	@PostMapping("/changeStudentField")
	public String changeStudentField(@RequestParam (name="students") int studentId, Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {				
			sessionBean.setSelectedStudent(sessionBean.getLoggedOnParent().getStudentFromId(studentId));			
			logger.info("Selected Student = {} for id {}",sessionBean.getSelectedStudent(), studentId);
			returnPage = sessionBean.getRedirectUrl();			
		}
		return returnPage;
	}	

    /**
     * End point for parent to view all 
     * the incidents for the currently selected student  
     * @param model - holder of context data from view 
     * @return the template to view the incidents
     */
	
    @GetMapping("/viewIncidents")
    public String viewIncidents(Model model) 
    {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {
			Student currentStudent = sessionBean.getSelectedStudent();
			if (currentStudent != null) {				
				List<Session> allIncidentSessions = Session.findAllWithIncidentsForStudent(currentStudent.getStudentId());
				model.addAttribute("incidentSessions", allIncidentSessions);	
				this.setInDialogue(false,model);	
				sessionBean.setReturnUrl("./viewIncidents");
				returnPage = "studentIncidents";				
			}
			else {
				returnPage = setupCalendar(model);
			}
		}		
		return returnPage;
    }   

	
	/**
	 * End point to refund the current logged user their total 
	 * cash balance  
	 * @param model - holder of context data from view 
	 * @return return redirect back to the userAccounts view 
	 */
	@GetMapping("/refund")
	public String refund(Model model) {
		String returnPage = validateIsParent(model);
		if (returnPage == null) {											
			if (sessionBean.getLoggedOnUser().refund()) {
				sessionBean.setFlashMessage("Successfully refunded.");								
			}
			else {
				sessionBean.setFlashMessage("Failed to refund balance.");												
			}
			returnPage= sessionBean.getRedirectUrl();	
		}
		return returnPage;
	}	
	
	
}
