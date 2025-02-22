package com.afterschoolclub;
import jakarta.mail.MessagingException;

import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.User;
import com.afterschoolclub.data.EventDay;
import com.afterschoolclub.data.repository.ClassRepository;
import com.afterschoolclub.data.repository.ClubRepository;
import com.afterschoolclub.data.repository.EventRepository;
import com.afterschoolclub.data.repository.MenuGroupRepository;
import com.afterschoolclub.data.repository.ParentalTransactionRepository;
import com.afterschoolclub.data.repository.ResourceRepository;
import com.afterschoolclub.data.repository.StudentRepository;
import com.afterschoolclub.data.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Optional;

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
	private final UserRepository userRepository;
	private final EventRepository eventRepository;
	

	
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
			ClassRepository classRepository, StudentRepository studentRepository, ParentalTransactionRepository transactionRepository, ClubRepository clubRepository, SessionBean sessionBean) {
		super();
		this.userRepository = userRepository;
		this.eventRepository = eventRepository;
		Event.clubRepository = clubRepository;
		Event.resourceRepository = resourceRepository;
		Event.menuGroupRepository = menuGroupRepository;
		Student.classRepository = classRepository;
		Parent.parentalTransactionRepository = transactionRepository;
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
	public String addNewUser(@RequestParam(name = "firstName") String firstName,
			@RequestParam(name = "surname") String surname, @RequestParam(name = "email") String email,
			@RequestParam(name = "password") String password, @RequestParam(name = "conPassword") String conPassword,
			@RequestParam(name = "telephoneNum") String telephoneNum, @RequestParam(name = "altContactName") String altContactName, @RequestParam (name = "altTelephoneNum") String altTelephoneNum, Model model) {
		List<User> users = userRepository.findByEmail(email);

		String returnPage = "home";
		this.setInDialogue(false,model);
		
		//TODO encode password properly
		
		Encoder encoder = Base64.getEncoder();
		String encodedPass = encoder.encodeToString(password.getBytes());		
		User user = new User(email, encodedPass, firstName, surname, LocalDateTime.now(), false);
		Parent parent = new Parent(telephoneNum,altContactName,altTelephoneNum);
		user.addParent(parent);	
		
		if (users.size() == 0 || users == null) {
			if (conPassword.equals(password)) {
				
				userRepository.save(user);
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
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			User userToValidate = user.get();
			if (userToValidate.getValidationKey() == validationKey) {
				userToValidate.setEmailVerified(true);
				userRepository.save(userToValidate);
				Context context = new Context();
				context.setVariable("user", userToValidate);
				try {
					mailService.sendTemplateEmail(userToValidate.getEmail(), "afterschooladmin@hattonsplace.co.uk",
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
			Optional<User> user = userRepository.findById(userId);
			if (user.isPresent()) {
				User userToAlter = user.get();
				if (userToAlter.getValidationKey() == validationKey) {
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
			List<User> users = userRepository.findByEmail(username);			
			if (users.size() == 0) {
				model.addAttribute("flashMessage", "Email or Password Incorrect");
			} else {
				User loginUser = users.get(0);
				if (loginUser.isPasswordValid(password)) {
					if (loginUser.isEmailVerified()) {
						sessionBean.setLoggedOnUser(loginUser);	
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
		}
		return returnPage;
	}

	@GetMapping("/calendarBack")
	public String calendarBack(Model model) {
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
			sessionBean.setTimetableStartDate(sessionBean.getTimetableStartDate().minusMonths(1)) ;						
			returnPage = setupCalendar(model);
		}
		return returnPage;	
	}

	@GetMapping("/calendarForward")
	public String calendarForward(Model model) {
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
			sessionBean.setTimetableStartDate(sessionBean.getTimetableStartDate().plusMonths(1)) ;						
			returnPage = setupCalendar(model);
		}
		return returnPage;	
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

	
	public String setupCalendar(Model model) {		
		LocalDate calendarMonth = sessionBean.getTimetableStartDate();
		LocalDate firstCalendarDay = null;
		LocalDate calendarNextMonth = null;
		this.setInDialogue(false,model);
			
		firstCalendarDay = calendarMonth.withDayOfMonth(1);
		calendarNextMonth = firstCalendarDay.plusMonths(1);
		List<Event> events = eventRepository.findEventsBetweenDates(firstCalendarDay, calendarNextMonth);
		
		int numCalendarWeeks = (firstCalendarDay.lengthOfMonth() + firstCalendarDay.getDayOfWeek().getValue() - 1);
		if (numCalendarWeeks % 7 > 0)
			numCalendarWeeks = numCalendarWeeks / 7 + 1;
		else
			numCalendarWeeks = numCalendarWeeks / 7;
				
		EventDay[][] calendar = new EventDay[numCalendarWeeks][7];
		for (int i = 0; i < calendar.length; i++) {
			for (int j = 0; j < calendar[i].length; j++) {
				if ((firstCalendarDay.getDayOfWeek().getValue() == (j + 1))
						&& (firstCalendarDay.isBefore(calendarNextMonth))) {
					EventDay eventDay = new EventDay(firstCalendarDay, events, sessionBean.getLoggedOnUser(), sessionBean.getSelectedStudent(), sessionBean.getFilter());					
					calendar[i][j] = eventDay;
					firstCalendarDay = firstCalendarDay.plusDays(1);
				} else {
					calendar[i][j] = null;
				}
			}
		}		
		model.addAttribute("calendar", calendar);
		return "calendar";
	}
	
	
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam( name = "password") String password, @RequestParam( name = "conPassword") String conPassword, Model model) {
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
			if (password.equals(conPassword)) {
				sessionBean.getLoggedOnUser().setPassword(password);
				userRepository.save(sessionBean.getLoggedOnUser());
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
			Optional<User> users = userRepository.findById(userId);
			if (users.isPresent()) {
				User user = users.get();
				if (password.equals(conPassword)) {
					if (user.getValidationKey() == validationKey.intValue()) {
						user.setPassword(password);
						user.setValidationKey();
						userRepository.save(user);
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
	public String calendar(Model model) {
		String returnPage = validateIsLoggedOn(model);
		if (returnPage == null) {
			this.setInDialogue(false,model);		
			returnPage = setupCalendar(model);			
		}
		return returnPage;
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
			List<User> users = userRepository.findByEmail(email);
			if (users != null && users.size() > 0) {
				// Send email
				User resetUser = users.get(0);
				resetUser.setValidationKey();
				userRepository.save(resetUser);
				Context context = new Context();
				context.setVariable("user", resetUser);
				String link = "http://localhost:8080/AfterSchoolClub/alterPassword?userId=" + resetUser.getUserId()
						+ "&validationKey=" + resetUser.getValidationKey();
				context.setVariable("link", link);

				try {
					mailService.sendTemplateEmail(resetUser.getEmail(), "afterschooladmin@hattonsplace.co.uk",
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


    


    
    
}
