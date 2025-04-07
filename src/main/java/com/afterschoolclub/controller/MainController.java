package com.afterschoolclub.controller;
import jakarta.mail.MessagingException;

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.Resource;
import com.afterschoolclub.data.State;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.StudentClass;
import com.afterschoolclub.data.User;
import com.afterschoolclub.data.EventDay;
import com.afterschoolclub.data.Holiday;
import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.RecurrenceSpecification;

import com.afterschoolclub.data.repository.ClassRepository;
import com.afterschoolclub.data.repository.ClubRepository;
import com.afterschoolclub.data.repository.EventRepository;
import com.afterschoolclub.data.repository.HolidayRepository;
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
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.context.Context;

@Controller
@SessionAttributes({"sessionBean"})

public class MainController {
	
	@Autowired
	private EmailService mailService;
	
	@Autowired
	private ProfilePicService profilePicService;	
	
	@Autowired	
    private PaypalService paypalService;	
	
	static Logger logger = LoggerFactory.getLogger(MainController.class);
	
    private final SessionBean sessionBean;	
	
	/**
	 * @param userRepository
	 * @param eventRepository
	 * @param menuGroupRepository
	 * @param resourceRepository
	 * @param classRepository
	 */
	public MainController(UserRepository userRepository, EventRepository eventRepository,
			MenuGroupRepository menuGroupRepository, ResourceRepository resourceRepository,
			ClassRepository classRepository, StudentRepository studentRepository, ParentalTransactionRepository transactionRepository, 
			ClubRepository clubRepository, RecurrenceSpecificationRepository recurrenceSpecificationRepository, HolidayRepository holidayRepository, MenuOptionRepository menuOptionRepository, ProfilePicService profilePicService, ClubPicService clubPicService, SessionBean sessionBean) {
		super();		
		Club.repository = clubRepository;
		Event.repository = eventRepository;
		Resource.repository = resourceRepository;
		MenuGroup.repository = menuGroupRepository;
		Student.repository = studentRepository;
		ParentalTransaction.repository = transactionRepository;
		User.repository = userRepository;
        User.profilePicService = profilePicService;
        Club.clubPicService = clubPicService;

        
		StudentClass.repository = classRepository;
		RecurrenceSpecification.repository = recurrenceSpecificationRepository;
		MenuOption.repository = menuOptionRepository;
		Holiday.repository = holidayRepository;
		
        this.sessionBean = sessionBean;
		
	}
	
	public String validateIsLoggedOn(Model model)
	{
		String returnPage = null;
		model.addAttribute("sessionBean", sessionBean);
		
		if (!sessionBean.isLoggedOn()) {			
			model.addAttribute("flashMessage","Please login to perform this action.");
			setInDialogue(false,model);
			returnPage = "home";		
		}
		return returnPage;		
	}
	
	
	public void setInDialogue(boolean inDialogue, Model model)
	{
		sessionBean.setInDialogue(inDialogue);
	    model.addAttribute("sessionBean", sessionBean);	    
		return;
	}
	



	@GetMapping("/log")
	public String log(Model model) {
		logger.trace("A TRACE Message");
		logger.debug("A DEBUG Message");
		logger.info("An INFO Message");
		logger.warn("A WARN Message");
		logger.error("An ERROR Message");
		this.setInDialogue(false,model);
		return "home";
	}
	

	@GetMapping("/validateEmail")
	public String validateEmail(@RequestParam(name = "userId") Integer userId,
			@RequestParam(name = "validationKey") Integer validationKey, Model model) {
		User user = User.findById(userId);
		if (user!=null) {
			if (user.getValidationKey() == validationKey) {
				user.setEmailVerified(true);
				user.save();
				Context context = new Context();
				context.setVariable("user", user);
				try {
					mailService.sendTemplateEmail(user.getEmail(), "afterschooladmin@hattonsplace.co.uk",
							"Email Verified For After School Club", "emailverifiedtemplate", context);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				model.addAttribute("flashMessage", "Email is now verified");
			}
		}
		this.setInDialogue(false,model);
		return "home";

	}
	
	@GetMapping("/alterPassword")
	public String alterPassword(@RequestParam(name = "userId") Integer userId,
			@RequestParam(name = "validationKey") Integer validationKey, Model model) {
		String returnPage = "home";
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
					model.addAttribute("flashMessage","Link out of date");					
				}
			}
			else {
				model.addAttribute("flashMessage","Link out of date");				
			}			
		}
		else {
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			returnPage = setupCalendar(model);			
		}
		return returnPage;
	}
	
	

	@PostMapping("/processlogin")
	public String processLogin(@RequestParam(name = "username") String username,
			@RequestParam(name = "password") String password, Model model) {
		String returnPage = "home";
		this.setInDialogue(false,model);
		if (!sessionBean.isLoggedOn()) {			
			User existingUser = User.findByEmail(username);			
			if (existingUser == null) {
				model.addAttribute("flashMessage", "Email or Password Incorrect");
			} else {
				if (existingUser.isPasswordValid(password)) {
					if (existingUser.isEmailVerified()) {
						sessionBean.setLoggedOnUser(existingUser);	
						returnPage = setupCalendar(model);
					} else {
						model.addAttribute("flashMessage", "Email has not been verified");						
					}	
				} else {
					model.addAttribute("flashMessage", "Email or Password Incorrect");					
				}
			}
		}
		else {
			model.addAttribute("flashMessage", "Already logged on.");
			returnPage = sessionBean.getRedirectUrl();
		}
		return returnPage;
	}

	@GetMapping("/calendarBack")
	public String calendarBack(Model model) {
		sessionBean.setTimetableStartDate(sessionBean.getTimetableStartDate().minusMonths(1)) ;						
		return setupCalendar(model);
	}

	@GetMapping("/calendarForward")
	public String calendarForward(Model model) {
		sessionBean.setTimetableStartDate(sessionBean.getTimetableStartDate().plusMonths(1)) ;						
		return setupCalendar(model);		
	}
	

	@GetMapping("/")
	public String home(Model model) {
		String returnPage = "home";
		this.setInDialogue(false,model);	  
	    if (sessionBean.isLoggedOn()) {
			returnPage = setupCalendar(model);			
	    }
	    paypalService.getAllWebProfiles();
	    return returnPage;
	}

	@GetMapping("/logout")
	public String home(Model model, SessionStatus status) {
		sessionBean.reset();
		status.setComplete();
		return "redirect:/";
	}

	
	public String initialiseCalendar(Model model) {		
		LocalDate calendarMonth = sessionBean.getTimetableStartDate();
		LocalDate calendarDay = null;
		LocalDate calendarNextMonth = null;
			
		calendarDay = calendarMonth.withDayOfMonth(1);
		calendarNextMonth = calendarDay.plusMonths(1);
		List<Event> events = Event.findForMonth(calendarDay);
		
		int numCalendarWeeks = (calendarDay.lengthOfMonth() + calendarDay.getDayOfWeek().getValue() - 1);
		if (numCalendarWeeks % 7 > 0)
			numCalendarWeeks = numCalendarWeeks / 7 + 1;
		else
			numCalendarWeeks = numCalendarWeeks / 7;
				
		EventDay[][] calendar = new EventDay[numCalendarWeeks][7];
		List<Holiday> allHolidays = Holiday.findAll();
		for (int i = 0; i < calendar.length; i++) {
			for (int j = 0; j < calendar[i].length; j++) {
				if ((calendarDay.getDayOfWeek().getValue() == (j + 1))
						&& (calendarDay.isBefore(calendarNextMonth))) {
					EventDay eventDay = new EventDay(calendarDay, allHolidays, events, sessionBean.getLoggedOnUser(), sessionBean.getSelectedStudent(), sessionBean.getFilter());					
					calendar[i][j] = eventDay;
					calendarDay = calendarDay.plusDays(1);
				} else {
					calendar[i][j] = null;
				}
			}
		}		
		model.addAttribute("calendar", calendar);
		return "calendar";
	}
	
	public String setupCalendar(Model model) {		
		return "redirect:/calendar";
	}
	
	
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam( name = "password") String password, @RequestParam( name = "conPassword") String conPassword, Model model) {
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
			if (password.equals(conPassword)) {
				sessionBean.getLoggedOnUser().setPassword(password);
				sessionBean.getLoggedOnUser().update();
				model.addAttribute("flashMessage","Password has been changed");
				returnPage = setupCalendar(model);				
			}
			else {
				model.addAttribute("flashMessage","Passwords do not match");
				model.addAttribute("formAction","./updatePassword");
				this.setInDialogue(true,model);
				returnPage = "changepassword";
			}
		}
		return returnPage;
	}
	
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
						user.save();
						model.addAttribute("flashMessage","Password has been changed");
					}
					else {
						model.addAttribute("flashMessage","Link out of date");
					}
					returnPage = "home";
				}
				else {
					model.addAttribute("flashMessage","Passwords do not match");
					model.addAttribute("formAction","./updatePasswordWithKey");
					model.addAttribute("userId",userId);
					model.addAttribute("validationKey",validationKey);
					this.setInDialogue(true,model);
					returnPage =  "changepassword";
				}
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				returnPage = "home";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			returnPage = "home";
		}
		return returnPage;
	}
	
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

	@GetMapping("/calendar")
	public String calendar(      
            @RequestParam(name="filterClub", required=false,  defaultValue="0") int filterClub,
            Model model) {

		if (filterClub != 0) {
			sessionBean.getFilter().setFilterClubId(filterClub);
		}
		this.setInDialogue(false,model);
		sessionBean.setCalendarView(true);
		sessionBean.setReturnCalendar();
		return initialiseCalendar(model);			
	}
	

	




	
	@GetMapping("/forgotPassword")
	public String forgotPassword(Model model) {
		String returnPage = null;
		if (sessionBean.getLoggedOnUser() == null) {
			this.setInDialogue(true,model);
			returnPage = "forgottenpassword";
		}
		else {
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			returnPage = setupCalendar(model);			
		}
		return returnPage;
	}
	
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam (name="email") String email, Model model) {
		String returnPage  = "home";
		if (sessionBean.getLoggedOnUser() == null) {
			User existingUser = User.findByEmail(email);
			if (existingUser != null) {
				// Send email
				existingUser.setValidationKey();
				existingUser.save();
				Context context = new Context();
				context.setVariable("user", existingUser);
				String link = "http://localhost:8080/AfterSchoolClub/alterPassword?userId=" + existingUser.getUserId()
						+ "&validationKey=" + existingUser.getValidationKey();
				context.setVariable("link", link);

				try {
					mailService.sendTemplateEmail(existingUser.getEmail(), "afterschooladmin@hattonsplace.co.uk",
							"Reset Your Password For Afterschool Club", "forgottenpasswordtemplate", context);
					model.addAttribute("flashMessage","Forgotten password email sent.");				

				} catch (MessagingException e) {
					model.addAttribute("flashMessage","Failed to send email.");
					e.printStackTrace();
				}				
			}
			else {
				model.addAttribute("flashMessage","User with that email address does not exist.");				
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			returnPage = setupCalendar(model);			
		}
				
		return returnPage;
	}
	

	@GetMapping("/viewClubs") // complete
	public String viewClubs(Model model) {
		this.setInDialogue(false,model);	  
		List<Club> allClubs = Club.findAll();
		model.addAttribute("allClubs",allClubs);
		sessionBean.setReturnClubs();
		return "viewClubs";	
	}	

	
	
	@GetMapping("/editUserDetails") 
	public String editUserDetails(Model model) {
		
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
			User loggedOnUser = sessionBean.getLoggedOnUser();
			model.addAttribute("isEditing",true);
			model.addAttribute("user", loggedOnUser);			
			this.setInDialogue(true,model);
			model.addAttribute("tempFilename", profilePicService.getTempfilename());
			returnPage = "user";
		} 
		return returnPage;
	}

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



    

	@PostMapping("/saveUserDetails")
	public String saveUserDetails(
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

		String returnPage = "home";			
		boolean newUser = false;
		
		User user = sessionBean.getLoggedOnUser();	
		
		if (user == null) {
			user = new User();								
			user.addParent(new Parent());
			newUser = true;
		}
		boolean emailChanged = !email.equals(user.getEmail());

		user.setTitle(title);
		user.setFirstName(firstName);
		user.setSurname(surname);
		user.setEmail(email);
		user.setTelephoneNum(telephoneNum);
		if (newUser) {
			user.setPassword(password);
		}
		
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
				if (emailChanged) {
					user.setValidationKey();// Update validation key to use in email
					user.setEmailVerified(false);
				}
				profilePicService.renameImage(tempFilename, user);

				if (newUser) {
					user.save();
				} 
				else {
					user.update();											
				}
				
				if (emailChanged || newUser) {
					// Send email

					//TODO used config for domain
					
					Context context = new Context();
					context.setVariable("user", user);
					String link = "http://localhost:8080/AfterSchoolClub/validateEmail?userId=" + user.getUserId()
							+ "&validationKey=" + user.getValidationKey();
					context.setVariable("link", link);

					try {
						mailService.sendTemplateEmail(user.getEmail(), "afterschooladmin@hattonsplace.co.uk",
								"Verify Email For Afterschool Club", "verifyemailtemplate", context);
						model.addAttribute("flashMessage","Please verify your email address");

					} catch (MessagingException e) {
						model.addAttribute("flashMessage","Failed to send verication email.");
						e.printStackTrace();
					}										
				}
				else {
					if (newUser) {						
						model.addAttribute("flashMessage","User has been created");
					}
					else {
						model.addAttribute("flashMessage","Profile has been updated");
					}
				}
				returnPage = setupCalendar(model);
			}
			else {
				model.addAttribute("isEditing",!newUser);
				model.addAttribute("flashMessage","Passwords do not match");
				model.addAttribute("user",user);
				this.setInDialogue(true,model);
				model.addAttribute("tempFilename", tempFilename);
				
				returnPage = "user";					
			}					
		}
		else {
			model.addAttribute("flashMessage", "Email already in use");
			model.addAttribute("user", user);
			model.addAttribute("isEditing", !newUser);
			model.addAttribute("tempFilename", tempFilename);
			
			this.setInDialogue(true, model);
			returnPage = "user";
		}

		return returnPage;
	}
    

	
	@GetMapping("/transactionBack")
	public String transactionBack(@RequestParam(name = "userId", required = false) int userId,	
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
	
	@GetMapping("/transactionForward")
	public String transactionForward(@RequestParam(name = "userId", required = false) int userId,
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
	
	
	
	
}
