package com.afterschoolclub.controller;
import jakarta.mail.MessagingException;

import com.afterschoolclub.SessionBean;
import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.Resource;
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
import com.afterschoolclub.service.ProfilePicService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Base64.Encoder;
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
	

	@GetMapping("/createUser")
	public String createUser(Model model) {		
		model.addAttribute("action","createUser");
		this.setInDialogue(true,model);
		return "createuser";
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

	@PostMapping("/addNewUser")
	public String addNewUser(@RequestParam(name = "firstName") String title,
			@RequestParam(name = "firstName") String firstName,
			@RequestParam(name = "surname") String surname, @RequestParam(name = "email") String email,
			@RequestParam(name = "password") String password, @RequestParam(name = "conPassword") String conPassword,
			@RequestParam(name = "telephoneNum") String telephoneNum, @RequestParam(name = "altContactName") String altContactName, @RequestParam (name = "altTelephoneNum") String altTelephoneNum, Model model) {
		User existingUser = User.findByEmail(email);

		String returnPage = "home";
		this.setInDialogue(false,model);
		
		//TODO encode password properly
		
		Encoder encoder = Base64.getEncoder();
		String encodedPass = encoder.encodeToString(password.getBytes());		
		User user = new User(email, encodedPass, title, firstName, surname, telephoneNum, LocalDateTime.now(), false);
		Parent parent = new Parent(altContactName,altTelephoneNum);
		user.addParent(parent);	
		
		if (existingUser == null) {
			if (conPassword.equals(password)) {
				
				user.save();
				logger.info("Saved user{}", user);

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

			} else {
				//TODO set formation
				model.addAttribute("action","createUser");
				model.addAttribute("flashMessage","Passwords do not match");
				model.addAttribute("editUser",user);
				this.setInDialogue(true,model);
				returnPage = "createuser";
			}
		} else {
			model.addAttribute("flashMessage","User already exists");
			model.addAttribute("action","createUser");			
			model.addAttribute("editUser",user);
			this.setInDialogue(true,model);
			returnPage = "createuser";
		}
		
		return returnPage;
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
		String returnPage = null;
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
					returnPage = "home";
				}
			}
			else {
				model.addAttribute("flashMessage","Link out of date");				
			}
			returnPage = "home";
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
			returnPage = setupCalendar(model);			
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
				sessionBean.getLoggedOnUser().save();
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
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
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
			returnPage = "home";
		}
		return returnPage;
	}
	

	

	
	// TODO allow edit of admin details... 
	
	@GetMapping("/editUserDetails") // complete
	public String editUserDetails(Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				model.addAttribute("action","updateUser");
				model.addAttribute("editUser", loggedOnUser);
				this.setInDialogue(true,model);

				return "createuser";
			} else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				setupCalendar(model);
				return "calendar";
			}
		} else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}


	@GetMapping("/viewClubs") // complete
	public String viewClubs(Model model) {
		this.setInDialogue(false,model);	  
		List<Club> allClubs = Club.findAll();
		model.addAttribute("allClubs",allClubs);
		return "viewClubs";	
	}	
    


    
    
}
