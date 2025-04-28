package com.afterschoolclub.controller;
import jakarta.mail.MessagingException;

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.Email;
import com.afterschoolclub.data.Session;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.Resource;
import com.afterschoolclub.data.State;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.StudentClass;
import com.afterschoolclub.data.User;
import com.afterschoolclub.data.SessionDay;
import com.afterschoolclub.data.Holiday;
import com.afterschoolclub.data.Incident;
import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.RecurrenceSpecification;

import com.afterschoolclub.data.repository.ClassRepository;
import com.afterschoolclub.data.repository.ClubRepository;
import com.afterschoolclub.data.repository.SessionRepository;
import com.afterschoolclub.data.repository.HolidayRepository;
import com.afterschoolclub.data.repository.IncidentRepository;
import com.afterschoolclub.data.repository.MenuGroupRepository;
import com.afterschoolclub.data.repository.MenuOptionRepository;
import com.afterschoolclub.data.repository.ParentalTransactionRepository;
import com.afterschoolclub.data.repository.RecurrenceSpecificationRepository;
import com.afterschoolclub.data.repository.ResourceRepository;
import com.afterschoolclub.data.repository.StudentRepository;
import com.afterschoolclub.data.repository.UserRepository;
import com.afterschoolclub.service.ClubPicService;
import com.afterschoolclub.service.EmailService;
import com.afterschoolclub.service.PaypalService;
import com.afterschoolclub.service.ProfilePicService;

import java.time.LocalDate;
import java.util.List;
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
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.context.Context;


/**
 * Controller Class that implements all the end points that are common across parents  
 * and administrators e.g. logon, change password, view clubs 
 */


@Controller
@SessionAttributes({"sessionBean"})

public class MainController {
	
	/**
	 * Utility service for sending emails
	 */
	@Autowired
	private EmailService mailService;
	
	/**
	 * Utility service for managing user profile pictures 
	 */
	@Autowired
	private ProfilePicService profilePicService;	
	
	/**
	 * Utility service for managing club pictures 
	 */
	@Autowired
	private ClubPicService clubPicService;	
	
	/**
	 * Utility logger for outputting info messages
	 */
	static Logger logger = LoggerFactory.getLogger(MainController.class);
	
    /**
     * Bean to manage session state acorss the view and difference controllers
     */
    private final SessionBean sessionBean;	
	

	/**
	 * Constructor for the main controller. This initialises all the repository objects in the 
	 * data classes. Unfortuantely cannot use dependency injection to do this.  
	 * @param userRepository
	 * @param sessionRepository
	 * @param menuGroupRepository
	 * @param resourceRepository
	 * @param classRepository
	 * @param studentRepository
	 * @param transactionRepository
	 * @param clubRepository
	 * @param recurrenceSpecificationRepository
	 * @param holidayRepository
	 * @param menuOptionRepository
	 * @param incidentRepository
	 * @param profilePicService
	 * @param clubPicService
	 * @param paypalService
	 * @param sessionBean
	 */
	public MainController(UserRepository userRepository, SessionRepository sessionRepository,
			MenuGroupRepository menuGroupRepository, ResourceRepository resourceRepository,
			ClassRepository classRepository, StudentRepository studentRepository, ParentalTransactionRepository transactionRepository, 
			ClubRepository clubRepository, RecurrenceSpecificationRepository recurrenceSpecificationRepository, 
			HolidayRepository holidayRepository, MenuOptionRepository menuOptionRepository, IncidentRepository incidentRepository, ProfilePicService profilePicService, 
			ClubPicService clubPicService, PaypalService paypalService, SessionBean sessionBean) {
		super();		
		Club.repository = clubRepository;
		Session.repository = sessionRepository;
		Resource.repository = resourceRepository;
		MenuGroup.repository = menuGroupRepository;
		Student.repository = studentRepository;
		ParentalTransaction.repository = transactionRepository;
		User.repository = userRepository;
        User.profilePicService = profilePicService;
        User.paypalService = paypalService;
        Club.clubPicService = clubPicService;
        Incident.repository = incidentRepository;

        
		StudentClass.repository = classRepository;
		RecurrenceSpecification.repository = recurrenceSpecificationRepository;
		MenuOption.repository = menuOptionRepository;
		Holiday.repository = holidayRepository;
		
        this.sessionBean = sessionBean;
		
	}
	
	/**
	 * Utility function to validate the user is loggedOn
	 * Returns null if logged on otherwise returns a redirection to user and
	 * sets appropraite error message
	 * @param model - holder of context data from view 
	 * @return
	 */
	public String validateIsLoggedOn(Model model)
	{
		String returnPage = null;
		model.addAttribute("sessionBean", sessionBean);
		
		if (!sessionBean.isLoggedOn()) {			
			sessionBean.setFlashMessage("Please login to perform this action.");
			setInDialogue(false,model);			//
			returnPage = sessionBean.getRedirectUrl();

		}
		return returnPage;		
	}
	
	
	/**
	 * Utility function to set in dialogue mode and disable links in navigation bars
	 * @param inDialogue
	 * @param model - holder of context data from view  - holder of context data from view 
	 */
	public void setInDialogue(boolean inDialogue, Model model)
	{
		sessionBean.setInDialogue(inDialogue);
	    model.addAttribute("sessionBean", sessionBean);	    
		return;
	}
	



	/**
	 * End point to validate an email ensuring the validation key is correct for this user
	 * @param userId - primary key for the user
	 * @param validationKey
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/validateEmail")
	public String validateEmail(@RequestParam(name = "userId") Integer userId,
			@RequestParam(name = "validationKey") Integer validationKey, Model model) {
		User user = User.findById(userId);
		if (user!=null) {
			if (user.getValidationKey() == validationKey) {
				user.setEmailVerified(true);
				user.update();
				Context context = new Context();
				context.setVariable("user", user);
				context.setVariable("sessionBean", sessionBean);
				
				Email email = new Email(user.getEmail(), sessionBean.getContactEmail(), "Email Verified For After School Club",  mailService.getHTML("email/emailVerified", context));
				
				try {
					mailService.sendHTMLEmail(email);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				sessionBean.setFlashMessage("Email is now verified");
			}
		}
		this.setInDialogue(false,model);
		return sessionBean.getRedirectUrl();

	}
	
	/**
	 * End point to return form to change password following a forgotten password
	 * @param userId - primary key for the user
	 * @param validationKey
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/alterPassword")
	public String alterPassword(@RequestParam(name = "userId") Integer userId,
			@RequestParam(name = "validationKey") Integer validationKey, Model model) {
		String returnPage = sessionBean.getRedirectUrl();
		this.setInDialogue(false,model);
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser == null) {
			User user = User.findById(userId);
			if (user!=null) {
				if (user.getValidationKey() == validationKey) {
					model.addAttribute("formAction","./updatePasswordWithKey");
					model.addAttribute("userId",userId);
					model.addAttribute("validationKey",validationKey);
					this.setInDialogue(true,model);
					returnPage = "changepassword";
				}
				else {
					sessionBean.setFlashMessage("Link out of date");					
				}
			}
			else {
				sessionBean.setFlashMessage("Link out of date");				
			}			
		}
		else {
			sessionBean.setFlashMessage("Must be logged out to perform this action");
			returnPage = setupCalendar(model);			
		}
		return returnPage;
	}
	
	

	/**
	 * End point to log the user in validting the username and password, active or inactive, verified email  and validation in the registration process
	 * @param username
	 * @param password
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@PostMapping("/processlogin")
	public String processLogin(@RequestParam(name = "username") String username,
			@RequestParam(name = "password") String password, Model model) {
		String returnPage = sessionBean.getRedirectUrl();
		this.setInDialogue(false,model);
		if (!sessionBean.isLoggedOn()) {			
			User existingUser = User.findByEmail(username);			
			if (existingUser == null) {
				sessionBean.setFlashMessage("Email or Password Incorrect.");
			} else {
				if (existingUser.isPasswordValid(password)) {
					if (existingUser.isEmailVerified()) {
						if (existingUser.isAdminVerified()) {
							if (existingUser.getState() == State.ACTIVE) {
								sessionBean.setLoggedOnUser(existingUser);	
								returnPage = setupCalendar(model);
							}
							else {
								sessionBean.setFlashMessage("Account is no longer active - contact administrator if you think this is an error.");
								returnPage = sessionBean.getRedirectUrl();									
							}
						}
						else {
							sessionBean.setFlashMessage("Account has not been verified by administrators.");
							returnPage = sessionBean.getRedirectUrl();							
						}
					} else {
						sessionBean.setFlashMessage("Email has not been verified.");
						returnPage = sessionBean.getRedirectUrl();
					}	
				} else {
					sessionBean.setFlashMessage("Email or Password Incorrect.");
					returnPage = sessionBean.getRedirectUrl();
				}
			}
		}
		else {
			sessionBean.setFlashMessage("Already logged on.");
			returnPage = sessionBean.getRedirectUrl();
		}
		return returnPage;
	}

	/**
	 * End point to move back one month in calendar view
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/calendarBack")
	public String calendarBack(Model model) {
		sessionBean.setTimetableStartDate(sessionBean.getTimetableStartDate().minusMonths(1)) ;						
		return setupCalendar(model);
	}

	/**
	 * End point to move forward one month in calendar view
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/calendarForward")
	public String calendarForward(Model model) {
		sessionBean.setTimetableStartDate(sessionBean.getTimetableStartDate().plusMonths(1)) ;						
		return setupCalendar(model);		
	}
	

	/**
	 * End point to go return the home page
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/")
	public String home(Model model) {
		String returnPage = "home";
		this.setInDialogue(false,model);	  
		List<String> allClubPics = clubPicService.getAllURLs();
		sessionBean.setReturnUrl("./");
		model.addAttribute("allImages", allClubPics);
	    return returnPage;
	}
	
	/**
	 * End point to view privacy statement
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/privacypolicy")
	public String privacypolicy(Model model) {
		sessionBean.setReturnUrl("./privacypolicy");
		this.setInDialogue(false,model);	  
	    return "privacypolicy";
	}
	
	/**
	 * End point if policy is missing
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/missingPolicy")
	public String missingPolicy(Model model) {
		this.setInDialogue(false,model);	  
	    return "missingPolicy";
	}
	
	
	/**
	 * End point to view terms and conditions
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/termsandconditions")
	public String termsandconditions(Model model) {
		sessionBean.setReturnUrl("./policiesandprocedures"); 
		sessionBean.setReturnUrl("./termsandconditions");
		this.setInDialogue(false,model);	  
	    return "termsandconditions";
	}
	
	/**
	 * End point to view policies and procedures
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/policiesandprocedures")
	public String policiesandprocedures(Model model) {
		sessionBean.setReturnUrl("./policiesandprocedures"); 
		this.setInDialogue(false,model);	  
	    return "policiesandprocedures";
	}
	
	
	

	/**
	 * End point to log out and remove and session state
	 * @param model - holder of context data from view 
	 * @param status
	 * @return the next page for the user
	 */
	@GetMapping("/logout")
	public String home(Model model, SessionStatus status) {
		sessionBean.reset();
		status.setComplete();
		return "redirect:/";
	}

	
	/**
	 * Utility function to set up the data to view on the calendar
	 * @param model - holder of context data from view 
	 * @return the calendar page for the user
	 */
	public String initialiseCalendar(Model model) {		
		LocalDate calendarMonth = sessionBean.getTimetableStartDate();
		LocalDate calendarDay = null;
		LocalDate calendarNextMonth = null;
			
		calendarDay = calendarMonth.withDayOfMonth(1);
		calendarNextMonth = calendarDay.plusMonths(1);
		List<Session> sessions = Session.findForMonth(calendarDay);
		
		int numCalendarWeeks = (calendarDay.lengthOfMonth() + calendarDay.getDayOfWeek().getValue() - 1);
		if (numCalendarWeeks % 7 > 0)
			numCalendarWeeks = numCalendarWeeks / 7 + 1;
		else
			numCalendarWeeks = numCalendarWeeks / 7;
				
		SessionDay[][] calendar = new SessionDay[numCalendarWeeks][7];
		List<Holiday> allHolidays = Holiday.findAll();
		for (int i = 0; i < calendar.length; i++) {
			for (int j = 0; j < calendar[i].length; j++) {
				if ((calendarDay.getDayOfWeek().getValue() == (j + 1))
						&& (calendarDay.isBefore(calendarNextMonth))) {
					SessionDay sessionDay = new SessionDay(calendarDay, allHolidays, sessions, sessionBean.getLoggedOnUser(), sessionBean.getSelectedStudent(), sessionBean.getFilter());					
					calendar[i][j] = sessionDay;
					calendarDay = calendarDay.plusDays(1);
				} else {
					calendar[i][j] = null;
				}
			}
		}		
		model.addAttribute("calendar", calendar);
		return "calendar";
	}
	
	/**
	 * Utility function to redirect to the calendar view
	 * @param model - holder of context data from view 
	 * @return
	 */
	public String setupCalendar(Model model) {		
		return "redirect:/calendar";
	}
	
	
	/**
	 * End point to persist new password
	 * @param password
	 * @param conPassword
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam( name = "password") String password, @RequestParam( name = "conPassword") String conPassword, Model model) {
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
			if (password.equals(conPassword)) {
				sessionBean.getLoggedOnUser().setPassword(password);
				sessionBean.getLoggedOnUser().update();
				sessionBean.setFlashMessage("Password has been changed");
				returnPage = setupCalendar(model);				
			}
			else {
				sessionBean.setFlashMessage("Passwords do not match");
				model.addAttribute("formAction","./updatePassword");
				this.setInDialogue(true,model);
				returnPage = "changepassword";
			}
		}
		return returnPage;
	}
	
	/**
	 * End point to update the password after user has forgotten it
	 * Need to ensure validation key is aligned with what we sent out
	 * @param userId - primary key for the user
	 * @param validationKey
	 * @param password
	 * @param conPassword
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@PostMapping("/updatePasswordWithKey")
	public String updatePasswordWithKey(@RequestParam( name = "userId") Integer userId, @RequestParam( name = "validationKey") Integer validationKey, @RequestParam( name = "password") String password, @RequestParam( name = "conPassword") String conPassword, Model model) {
		String returnPage = null;
		this.setInDialogue(false,model);
		
		if (sessionBean.getLoggedOnUser() == null) {
			User user = User.findById(userId);
			if (user!=null) {
				if (password.equals(conPassword)) {
					if (user.getValidationKey() == validationKey.intValue()) {
						user.setPassword(password);
						user.setValidationKey();
						user.update();
						sessionBean.setFlashMessage("Password has been changed");
					}
					else {
						sessionBean.setFlashMessage("Link out of date");
					}
					returnPage = sessionBean.getRedirectUrl();
				}
				else {
					sessionBean.setFlashMessage("Passwords do not match");
					model.addAttribute("formAction","./updatePasswordWithKey");
					model.addAttribute("userId",userId);
					model.addAttribute("validationKey",validationKey);
					this.setInDialogue(true,model);
					returnPage =  "changepassword";
				}
			}
			else {
				sessionBean.setFlashMessage("Link out of date");
				returnPage = sessionBean.getRedirectUrl();
			}
		}
		else {
			sessionBean.setFlashMessage("Must be logged out to perform this action");
			returnPage = sessionBean.getRedirectUrl();
		}
		return returnPage;
	}
	
	/**
	 * End point to return form to change password
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/changePassword")
	public String changePassword(Model model) {
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
			model.addAttribute("formAction","./updatePassword");
			this.setInDialogue(true,model);
			returnPage = "changepassword";
		}
		return returnPage;
			
	}

	/**
	 * End point to view the calendar view
	 * @param filterClub
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/calendar")
	public String calendar(      
            @RequestParam(name="filterClub", required=false,  defaultValue="0") int filterClub,
            Model model) {

		if (filterClub != 0) {
			sessionBean.getFilter().setFilterClubId(filterClub);
		}
		this.setInDialogue(false,model);
		sessionBean.setCalendarView(true);
		sessionBean.setReturnUrl("./calendar");
		return initialiseCalendar(model);			
	}
	
	
	/**
	 * End point to return a form if forgotten password
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/forgotPassword")
	public String forgotPassword(Model model) {
		String returnPage = null;
		if (sessionBean.getLoggedOnUser() == null) {
			this.setInDialogue(true,model);
			returnPage = "forgottenpassword";
		}
		else {
			sessionBean.setFlashMessage("Must be logged out to perform this action");
			returnPage = setupCalendar(model);			
		}
		return returnPage;
	}
	
	/**
	 * End point to update  password
	 * @param email new email
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam (name="email") String email, Model model) {
		String returnPage  = sessionBean.getRedirectUrl();
		if (sessionBean.getLoggedOnUser() == null) {
			User existingUser = User.findByEmail(email);
			if (existingUser != null) {
				// Send email
				existingUser.setValidationKey();
				existingUser.update();
				Context context = new Context();
				context.setVariable("user", existingUser);
				context.setVariable("sessionBean", sessionBean);
				
				
				String link = String.format("%s/alterPassword?userId=%d&validationKey=%d", sessionBean.getHomePage(), existingUser.getUserId(), existingUser.getValidationKey());
				context.setVariable("link", link);

				Email emailToSend = new Email(existingUser.getEmail(), sessionBean.getContactEmail(), "Reset Your Password For Afterschool Club",  mailService.getHTML("email/forgottenPassword", context));
				
				try {
					mailService.sendHTMLEmail(emailToSend);
					sessionBean.setFlashMessage("Forgotten password email sent.");				

				} catch (MessagingException e) {
					sessionBean.setFlashMessage("Failed to send email.");
					e.printStackTrace();
				}				
			}
			else {
				sessionBean.setFlashMessage("User with that email address does not exist.");				
			}
		}
		else {
			sessionBean.setFlashMessage("Must be logged out to perform this action");
			returnPage = setupCalendar(model);			
		}
				
		return returnPage;
	}
	

	/**
	 * End point to view the clubs
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/viewClubs") // complete
	public String viewClubs(Model model) {
		this.setInDialogue(false,model);	  
		List<Club> allClubs = Club.findActive();
		model.addAttribute("allClubs",allClubs);
		sessionBean.setReturnUrl("./viewClubs");
		return "viewClubs";	
	}	

	
	
	/**
	 * End point to retreive form to edit a user
	 * @param model - holder of context data from view 
	 * @param userId - primary key for the user
	 * @return the next page for the user
	 */
	@GetMapping("/editUserDetails") 
	public String editUserDetails(Model model, 
			@RequestParam(name = "userId", required=false, defaultValue="0") int userId) {
		
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
			User user = null;
			if (userId == 0) {
				user = User.findById(sessionBean.getLoggedOnUser().getUserId());
			}
			else {
				user = User.findById(userId); 
			}
			if (user != null) {
				model.addAttribute("isEditing",true);
				model.addAttribute("user", user);			
				this.setInDialogue(true,model);
				model.addAttribute("tempFilename", profilePicService.getTempfilename());
				returnPage = "user";
			}
			else {
				sessionBean.setFlashMessage("Invalid Link");
				returnPage = sessionBean.getRedirectUrl();				
			}
		} 
		return returnPage;
	}

	/**
	 * End point to retreie form to create a new user
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/createUser")
	public String createUser(Model model) {		
		model.addAttribute("isEditing",false);		
		this.setInDialogue(true,model);
		User user = new User();
		user.addParent(new Parent());
		model.addAttribute("user", user);		
		model.addAttribute("tempFilename", profilePicService.getTempfilename());
		return "user";
	}



    

	/**
	 * End point to save a newly created use or update an existing one with fresh details
	 * @param userId - primary key for the user
	 * @param title
	 * @param firstName
	 * @param surname
	 * @param email
	 * @param password
	 * @param conPassword
	 * @param description
	 * @param keywords
	 * @param telephoneNum
	 * @param altContactName
	 * @param altTelephoneNum
	 * @param tempFilename
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@PostMapping("/saveUserDetails")
	public String saveUserDetails(
			@RequestParam(name = "userId") int userId,			
			@RequestParam(name = "title") String title,			
			@RequestParam(name = "firstName") String firstName,
			@RequestParam(name = "surname") String surname, 
			@RequestParam(name = "email") String email,
			@RequestParam(name = "password", required=false, defaultValue = "") String password, 
			@RequestParam(name = "conPassword", required=false, defaultValue = "")String conPassword,			
			@RequestParam(name = "description", required=false, defaultValue = "") String description,
			@RequestParam(name = "keywords", required=false, defaultValue = "") String keywords,						
			@RequestParam(name = "telephoneNum", required=false, defaultValue = "") String telephoneNum, 
			@RequestParam(name = "altContactName", required=false, defaultValue = "") String altContactName, 
			@RequestParam (name = "altTelephoneNum", required=false, defaultValue = "")	String altTelephoneNum,
			@RequestParam (name = "tempFilename")	String tempFilename, 
			
			Model model) {

		String returnPage = sessionBean.getRedirectUrl();
		boolean newUser = false;
		
		User user = User.findById(userId);	
		
		if (user == null) {
			User existingUser = User.findByEmail(email);
			if (existingUser != null && existingUser.getState()==State.INACTIVE && existingUser.isParent()) {
				user = existingUser;
				user.setState(State.ACTIVE);	
				user.setEmailVerified(false);
				user.setAdminVerified(false);
				user.setValidationKey();			
			}
			else {
				user = new User();								
				user.addParent(new Parent());
				newUser = true;
			}
			user.setPassword(password);				
		}
		boolean emailChanged=false;
		if (!email.equals(user.getEmail())) {
			user.setValidationKey();			
			user.setEmailVerified(false);
			emailChanged = true;
		};

		user.setTitle(title);
		user.setFirstName(firstName);
		user.setSurname(surname);
		user.setEmail(email);
		user.setTelephoneNum(telephoneNum);
		
		if (user.isParent()) {
			Parent parent = user.getParent();
			parent.setAltContactName(altContactName);
			parent.setAltTelephoneNum(altTelephoneNum);
			
		}
		else {
			Resource resource = user.getResourceObject();
			resource.setDescription(description);
			resource.setKeywords(keywords);
			resource.setState(State.ACTIVE);
		}
		
		
		// Need to ensure email not in use if new user or email has changed
		boolean emailOk = true;
		if (newUser || emailChanged) {
			emailOk = User.findByEmail(email) == null;
		}
		
		if (emailOk) {
			// Ensure passwords match
			boolean passwordOk = (newUser && conPassword.equals(password)) || !newUser;
			if (passwordOk) {


				if (newUser) {
					user.save();
				} 
				else {
					user.update();											
				}
				if (sessionBean.getLoggedOnUser() != null && user.getUserId() == sessionBean.getLoggedOnUser().getUserId()) {
					sessionBean.setLoggedOnUser(user);
				}
				profilePicService.renameImage(tempFilename, user);
				
				if (!user.isEmailVerified()) {
					// Send email
					
					Context context = new Context();
					context.setVariable("user", user);
					context.setVariable("sessionBean", sessionBean);	
					String link = String.format("%s/validateEmail?userId=%d&validationKey=%d", sessionBean.getHomePage(), user.getUserId(), user.getValidationKey());
					context.setVariable("link", link);

					Email emailToSend = new Email(user.getEmail(), sessionBean.getContactEmail(), "Verify Email For Afterschool Club",  mailService.getHTML("email/verifyEmail", context));					
					try {
						mailService.sendHTMLEmail(emailToSend);
						sessionBean.setFlashMessage("Please verify your email address before attempting to login.");
					} catch (MessagingException e) {
						sessionBean.setFlashMessage("Failed to send verication email.");
						e.printStackTrace();
					}										
				}
				else {
					if (newUser) {						
						sessionBean.setFlashMessage("User has been created");
					}
					else {
						sessionBean.setFlashMessage("Profile has been updated");
					}
				}
			}
			else {
				model.addAttribute("isEditing",!newUser);
				sessionBean.setFlashMessage("Passwords do not match");
				model.addAttribute("user",user);
				this.setInDialogue(true,model);
				model.addAttribute("tempFilename", tempFilename);				
				returnPage = "user";					
			}					
		}
		else {
			sessionBean.setFlashMessage("Email already in use");
			model.addAttribute("user", user);
			model.addAttribute("isEditing", !newUser);
			model.addAttribute("tempFilename", tempFilename);			
			this.setInDialogue(true, model);
			returnPage = "user";
		}

		return returnPage;
	}
    

	
	/**
	 * End point to move one month back in transactional view	 * 
	 * @param userId - primary key for the user
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/transactionBack")
	public String transactionBack(@RequestParam(name = "userId", required = false,  defaultValue="0") int userId,	
			Model model) {
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {		
			this.setInDialogue(true,model);			
			sessionBean.setTransactionStartDate(sessionBean.getTransactionStartDate().minusMonths(1));
			if (sessionBean.isAdminLoggedOn()) {
				returnPage = String.format("redirect:./adminViewTransactions?userId=%d", userId);
			}
			else {
				returnPage = "redirect:./viewTransactions";
			}
		}
		return returnPage;			
	}
	
	/**
	 * End point to move one month forward in transactional view
	 * @param userId - primary key for the user
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/transactionForward")
	public String transactionForward(@RequestParam(name = "userId", required = false,  defaultValue="0") int userId,
					Model model) {
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {		
			this.setInDialogue(true,model);			
			sessionBean.setTransactionStartDate(sessionBean.getTransactionStartDate().plusMonths(1));
			if (sessionBean.isAdminLoggedOn()) {
				returnPage = String.format("redirect:./adminViewTransactions?userId=%d", userId);
			}
			else {
				returnPage = "redirect:./viewTransactions";
			}
		}
		return returnPage;	
	}	
	
	

	/**
	 * End point to view edit screen for a student
	 * @param userId - primary key for the student
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@GetMapping("/editStudent")
	public String editStudent(@RequestParam(name = "studentId") int studentId, Model model) {
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
			Student s = Student.findById(studentId);
			if (sessionBean.isAdminLoggedOn() || s.getParent().getParentId() == s.getParentId().getId().intValue()) {
				model.addAttribute("student",s);			
				Iterable<StudentClass> classNames = StudentClass.findAll();
				model.addAttribute("classNames",classNames);
				model.addAttribute("isEditing",true);			
				this.setInDialogue(true,model);
				returnPage = "student";		
			}
			else {
				sessionBean.setFlashMessage("Not authorised to edit this student");
				returnPage = sessionBean.getRedirectUrl();
			}
		}
		return returnPage;
	}


	
	/**
	 * End point to store a new student or update an existing students details
	 * @param userId - primary key for the student
	 * @param firstName
	 * @param surname
	 * @param className
	 * @param dateOfBirth
	 * @param allergyNote
	 * @param healthNote
	 * @param dietNote
	 * @param careNote
	 * @param medicationNote
	 * @param otherNote
	 * @param consentToShare
	 * @param model - holder of context data from view 
	 * @return the next page for the user
	 */
	@PostMapping("/addStudent")
	public String addStudent(
			@RequestParam(name = "studentId") int studentId,
			@RequestParam(name = "firstName") String firstName,
			@RequestParam(name = "surname") String surname, @RequestParam(name = "className") int className,
			@RequestParam(name = "dateOfBirth") LocalDate dateOfBirth,
			@RequestParam(name = "allergyNote", required=false, defaultValue="") String allergyNote,
			@RequestParam(name = "healthNote", required=false, defaultValue="") String healthNote,
			@RequestParam(name = "dietNote", defaultValue="") String dietNote,
			@RequestParam(name = "careNote", required=false, defaultValue="") String careNote,
			@RequestParam(name = "medicationNote", required=false, defaultValue="") String medicationNote,
			@RequestParam(name = "otherNote", required=false, defaultValue="") String otherNote,
			@RequestParam(name = "consentToShare", defaultValue = "false") boolean consentToShare, Model model) {
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
			Student student = null;
			if (studentId == 0) {
				student = new Student();
				sessionBean.getLoggedOnParent().addStudent(student);				
			}
			else {
				if (sessionBean.isParentLoggedOn()) {
					student = sessionBean.getLoggedOnParent().getStudentFromId(studentId);
				}
				else {
					student = Student.findById(studentId);
				}
			}
			student.setFirstName(firstName);
			student.setSurname(surname);
			student.setDateOfBirth(dateOfBirth);
			student.setClassId(AggregateReference.to(className));	
			
			if (sessionBean.isParentLoggedOn()) {
				student.setParentId(AggregateReference.to(sessionBean.getLoggedOnParent().getParentId()));				
				student.setConsentToShare(consentToShare);				
				student.setAllergyNoteText(allergyNote);
				student.setHealthNoteText(healthNote);
				student.setDietNoteText(dietNote);
				student.setCarePlanNoteText(careNote);
				student.setMedicationNoteText(medicationNote);
				student.setOtherNoteText(otherNote);
				student.updateTimestamp();
			}
			
			if (sessionBean.isParentLoggedOn()) {	
				student.save();
				sessionBean.refreshLoggedOnUser();
				//sessionBean.getLoggedOnUser().save();
			}
			else {
				student.update();
			}
			
			if (studentId == 0) {
				sessionBean.setFlashMessage("Child added you will be notified by email when validated by an administrator.");
			}
			else {
				sessionBean.setFlashMessage("Updated Child Details.");
			}				
			returnPage = sessionBean.getRedirectUrl();
		}
		return returnPage;
	}	
	
}
