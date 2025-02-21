package com.afterschoolclub;
import jakarta.mail.MessagingException;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;

import com.afterschoolclub.data.Attendee;
import com.afterschoolclub.data.AttendeeMenuChoice;
import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.User;
import com.afterschoolclub.data.EventDay;
import com.afterschoolclub.data.EventMenu;
import com.afterschoolclub.data.EventResource;
import com.afterschoolclub.data.Club;
import com.afterschoolclub.data.MenuOption;
import com.afterschoolclub.data.Resource;
import com.afterschoolclub.data.FilteredEvent;
import com.afterschoolclub.data.repository.ClassRepository;
import com.afterschoolclub.data.repository.ClubRepository;
import com.afterschoolclub.data.repository.EventRepository;
import com.afterschoolclub.data.repository.MenuGroupRepository;
import com.afterschoolclub.data.repository.ParentalTransactionRepository;
import com.afterschoolclub.data.repository.ResourceRepository;
import com.afterschoolclub.data.repository.StudentRepository;
import com.afterschoolclub.data.repository.UserRepository;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.context.Context;

@Controller
@SessionAttributes({"sessionBean"})

public class MainController {
	private final UserRepository userRepository;
	private final EventRepository eventRepository;
	private final MenuGroupRepository menuGroupRepository;
	private final ResourceRepository resourceRepository;
	private final ClubRepository clubRepository;
	
	@Autowired	
    private PaypalService paypalService;
	
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
		this.menuGroupRepository = menuGroupRepository;
		this.resourceRepository = resourceRepository;
		this.clubRepository = clubRepository;
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				//TODO set formation
				model.addAttribute("action","createUser");
				model.addAttribute("flashMessage","Passwords do not match");
				model.addAttribute("editUser",user);
				this.setInDialogue(true,model);
				return "createuser";
			}
		} else {
			model.addAttribute("flashMessage","User already exists");
			model.addAttribute("action","createUser");			
			model.addAttribute("editUser",user);
			this.setInDialogue(true,model);
			return "createuser";
		}
		this.setInDialogue(false,model);
		return "home";
	}
	
	@PostMapping("/saveUserDetails")
	public String saveUserDetails(@RequestParam(name = "firstName") String firstName,
			@RequestParam(name = "surname") String surname, @RequestParam(name = "email") String email,
			@RequestParam(name = "telephoneNum") String telephoneNum, @RequestParam(name = "altContactName") String altContactName, @RequestParam (name = "altTelephoneNum") String altTelephoneNum, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			Parent loggedOnParent = sessionBean.getLoggedOnParent();
			loggedOnUser.setFirstName(firstName);
			loggedOnUser.setSurname(surname);
			loggedOnParent.setTelephoneNum(telephoneNum);
			loggedOnParent.setAltContactName(altContactName);
			loggedOnParent.setAltTelephoneNum(altTelephoneNum);
			userRepository.save(loggedOnUser);
			setupCalendar(model);
			model.addAttribute("flashMessage","Profile has been updated");
			return "calendar";
		}else {
				model.addAttribute("flashMessage","Parent must be logged in to perform this action");
				this.setInDialogue(false,model);
				return "home";
			}
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
					// TODO Auto-generated catch block
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
					return "changepassword";
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					this.setInDialogue(false,model);
					return "home";
				}
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				this.setInDialogue(false,model);
				return "home";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			setupCalendar(model);
			return "calendar";
		}
	}
	
	
	@GetMapping("/createClub")
	public String createClub(Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()) {				
				this.setInDialogue(true,model);
				return "createclub";
			} else {
				setupCalendar(model);
				return "calendar";
			}
		} else {
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	@PostMapping("/addClub")
	public String addClub(@RequestParam(name = "title") String title,
			@RequestParam(name = "description") String description, 
			@RequestParam(name = "basePrice") int basePrice, 
			@RequestParam(name = "yearR", defaultValue="false") boolean yearRCanAttend,
			@RequestParam(name = "year1", defaultValue="false") boolean yearOneCanAttend,
			@RequestParam(name = "year2", defaultValue="false") boolean yearTwoCanAttend,
			@RequestParam(name = "year3", defaultValue="false") boolean yearThreeCanAttend,
			@RequestParam(name = "year4", defaultValue="false") boolean yearFourCanAttend,
			@RequestParam(name = "year5", defaultValue="false") boolean yearFiveCanAttend,
			@RequestParam(name = "year6", defaultValue="false") boolean yearSixCanAttend, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		this.setInDialogue(false,model);
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()) {
				Club club = new Club(title, description, basePrice, yearRCanAttend, yearOneCanAttend, yearTwoCanAttend, yearThreeCanAttend, yearFourCanAttend, yearFiveCanAttend, yearSixCanAttend); 
				clubRepository.save(club);
				setupCalendar(model);
				return "calendar";
			}
			else {
				setupCalendar(model);
				return "calendar";
			}
		}
		else {


			return "home";
		}
	}	
	
	@GetMapping("/createEvent")
	public String createEvent(Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()) {
				List<Club> clubs = clubRepository.findAll();
				model.addAttribute("clubs",clubs);
				List<Resource> rooms = resourceRepository.findByType(Resource.Type.ROOM);
				model.addAttribute("rooms", rooms);
				
				List<Resource> staff = resourceRepository.findByType(Resource.Type.STAFF);
				model.addAttribute("staff", staff);

				List<Resource> equipment = resourceRepository.findByType(Resource.Type.EQUIPMENT);
				model.addAttribute("equipment", equipment);
				
				Iterable<MenuGroup> menus = menuGroupRepository.findAll();
				model.addAttribute("menus", menus);				
				this.setInDialogue(true,model);

				return "createevent";
			} else {
				setupCalendar(model);
				return "calendar";
			}
		} else {
			this.setInDialogue(false,model);
			return "home";
		}
	}

	@PostMapping("/addEvent")
	public String addEvent(@RequestParam(name = "club") int clubId,
			@RequestParam(name = "location") int location,
			@RequestParam(name = "startDate") LocalDate startDate,
			@RequestParam(name = "startTime") LocalTime startTime, 
			@RequestParam(name = "endTime") LocalTime endTime, 
			@RequestParam(name = "maxAttendees") int maxAttendees,
			@RequestParam(name = "staff") List<Integer> staff,	
			@RequestParam(name = "menu", required=false) List<Integer> menuGroups,			
			@RequestParam(name = "equipment") List<Integer> equipment,
			@RequestParam(name = "equipmentQuantity") List<Integer> equipmentQuantity,
			@RequestParam(name = "hiddenPerAttendee") List<Boolean> perAttendee,			
			Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		this.setInDialogue(false,model);
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()) {
				LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
				LocalDateTime endDateTime = LocalDateTime.of(startDate, endTime);
												
				Event event = new Event(AggregateReference.to(clubId),  startDateTime, endDateTime, maxAttendees);
				
				for (Integer staffMember : staff) {
					EventResource er = new EventResource(AggregateReference.to(staffMember), 1, false);
					event.addResource(er);
				}
				
				logger.info("Selected equipment = {}",equipment);
				logger.info("Selected equipmentQuantity = {}",equipmentQuantity);
				logger.info("Selected perAttendee = {}",perAttendee);

				int counter = 0;
				
				for (Integer item : equipment) {					
					if ( item.intValue() != 0) {
						int quantity = equipmentQuantity.get(counter).intValue();
						boolean bPerAttendee = perAttendee.get(counter).booleanValue();
						
						EventResource er = new EventResource(AggregateReference.to(item), quantity, bPerAttendee);
						
						logger.info("Selected perAtEventResource= {}",er);
						event.addResource(er);
					}
					counter++;
				}			

				EventResource er = new EventResource(AggregateReference.to(location), 1, false);
				event.addResource(er);
				
				
				if (menuGroups != null)
				{
					for (Integer menu : menuGroups) {
					
						EventMenu newMenu = new EventMenu(AggregateReference.to(menu));
						event.addEventMenu(newMenu);
						
					}
				}
				
				
				
				eventRepository.save(event);
				
				logger.info("Selected Staff = {}",staff);
				logger.info("Menu Group Id = {}",menuGroups);

				setupCalendar(model);
				return "calendar";
			}
			else {
				setupCalendar(model);
				return "calendar";
			}
		}
		else {
			return "home";
		}
	}
	

	@PostMapping("/processlogin")
	public String processLogin(@RequestParam(name = "username") String username,
			@RequestParam(name = "password") String password, Model model) {
		List<User> users = userRepository.findByEmail(username);
		this.setInDialogue(false,model);
		if (users.size() == 0) {
			model.addAttribute("flashMessage", "Email or Password Incorrect");
			return "home";
		} else {
			User loginUser = users.get(0);
			if (loginUser.isPasswordValid(password)) {
				if (loginUser.isEmailVerified()) {
					sessionBean.setLoggedOnUser(loginUser);

					if (loginUser.isAdmin())
					{
						logger.info("User = {}", loginUser);
						logger.info("Administrator = {}", loginUser.getAdministratorObject());
						logger.info("Resource = {}", loginUser.getAdministratorObject().getResourceObject());
						
					}
					
					setupCalendar(model);
					return "calendar";
				} else {
					model.addAttribute("flashMessage", "Email has not been verified");
					return "home";
				}

			} else {
				model.addAttribute("flashMessage", "Email or Password Incorrect");
				return "home";
			}
		}
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
		
		this.setInDialogue(false,model);
		
		return "redirect:/";
	}

	public void calculateFilters(EventDay eventDay, Model model)
	{
	    User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser.isAdmin() ) {		    
			Resource resource = loggedOnUser.getAdministratorObject().getResourceObject();
			
			for (FilteredEvent filteredEvent : eventDay.getFilteredEvents()) {
				Event event = filteredEvent.getEvent();
				if (event.usesResource(resource)) {
					filteredEvent.setAttending(true);						
				}
				else {
					if (sessionBean.isOnlyMineFilter()) {
						filteredEvent.setHidden(true);
					}
						
				}	
				switch (sessionBean.getAdminFilter()) {				
				case FULLYBOOKED:
					if (!event.isFullyBooked()) {
						filteredEvent.setHidden(true);
					}
						
					break;
				case NOATTENDEES:
					if (event.getNumberAttendees() != 0) {
						filteredEvent.setHidden(true);
					}					
					break;
				default: 
					break;
				}
			}
		} else
		{
			Student student= sessionBean.getSelectedStudent();
			
			for (FilteredEvent filteredEvent : eventDay.getFilteredEvents()) {
				Event event = filteredEvent.getEvent();
				
				if (event.endInPast()) {
					if (student != null && event.didAttend(student)) {
						filteredEvent.setAttended(true);
						if (!sessionBean.isDisplayingAttended()) {
							filteredEvent.setHidden(true);
						}							
					} 
					else if (student != null && event.registered(student)) {
						filteredEvent.setMissed(true);
						if (!sessionBean.isDisplayingMissed()) {
							filteredEvent.setHidden(true);
						}						
					}
					else {
						filteredEvent.setAvailable(false);
						if (!sessionBean.isDisplayingUnavailable()) {
							filteredEvent.setHidden(true);
						}
					}
				} 
				else {
					if (student != null && student.isAttendingEvent(event)) {
						filteredEvent.setAttending(true);
						if (!sessionBean.isDisplayingAttending()) {
							filteredEvent.setHidden(true);
						}
					}
					else if (event.endInPast() || (student != null && !event.canAttend(student))) { 
						filteredEvent.setAvailable(false);	
						if (!sessionBean.isDisplayingAvailable()) {
							filteredEvent.setHidden(true);
						}
					}
					else if (!sessionBean.isDisplayingAvailable()) {
						filteredEvent.setHidden(true);						
					}
				}								 								
			}
		}
			
	}
	
	public String setupCalendar(Model model) {
		
		LocalDate calendarMonth = sessionBean.getTimetableStartDate();
		LocalDate calendarDay = null;
		LocalDate calendarNextMonth = null;
		this.setInDialogue(false,model);
		
		Student student= sessionBean.getSelectedStudent();
		logger.info("Selected Student = {}",student);
		
		calendarDay = calendarMonth.withDayOfMonth(1);
		calendarNextMonth = calendarDay.plusMonths(1);
		List<Event> events = eventRepository.findEventsBetweenDates(calendarDay, calendarNextMonth);
		
		int numCalendarWeeks = (calendarDay.lengthOfMonth() + calendarDay.getDayOfWeek().getValue() - 1);
		if (numCalendarWeeks % 7 > 0)
			numCalendarWeeks = numCalendarWeeks / 7 + 1;
		else
			numCalendarWeeks = numCalendarWeeks / 7;
						
			
		
		EventDay[][] calendar = new EventDay[numCalendarWeeks][7];
		

		for (int i = 0; i < calendar.length; i++) {
			for (int j = 0; j < calendar[i].length; j++) {
				if ((calendarDay.getDayOfWeek().getValue() == (j + 1))
						&& (calendarDay.isBefore(calendarNextMonth))) {
					EventDay eventDay = new EventDay(calendarDay);
					eventDay.addAllEventsForDay(events);
					this.calculateFilters(eventDay, model);
					
					//logger.info("eventDay={}", eventDay);
					//logger.info("dayOfMonth={}", eventDay.getDate().getDayOfMonth());
					
					
					
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
	
	
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam( name = "password") String password, @RequestParam( name = "conPassword") String conPassword, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			if (password.equals(conPassword)) {
				loggedOnUser.setPassword(password);
				userRepository.save(loggedOnUser);
				model.addAttribute("flashMessage","Password has been changed");
				return setupCalendar(model);				
			}
			else {
				model.addAttribute("flashMessage","Passwords do not match");
				model.addAttribute("formAction","./updatePassword");
				this.setInDialogue(true,model);
				return "changepassword";
			}
		}
		else {
			model.addAttribute("flashMessage","Please login to change your password");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	@PostMapping("/updatePasswordWithKey")
	public String updatePasswordWithKey(@RequestParam( name = "userId") Integer userId, @RequestParam( name = "validationKey") Integer validationKey, @RequestParam( name = "password") String password, @RequestParam( name = "conPassword") String conPassword, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser == null) {
			Optional<User> users = userRepository.findById(userId);
			if (users.isPresent()) {
				User user = users.get();
				if (password.equals(conPassword)) {
					if (user.getValidationKey() == validationKey.intValue()) {
						user.setPassword(password);
						user.setValidationKey();
						userRepository.save(user);
						model.addAttribute("flashMessage","Password has been changed");
						this.setInDialogue(false,model);

						return "home";
					}
					else {
						model.addAttribute("flashMessage","Link out of date");
						this.setInDialogue(false,model);

						return "home";
					}
				}
				else {
					model.addAttribute("flashMessage","Passwords do not match");
					model.addAttribute("formAction","./updatePasswordWithKey");
					model.addAttribute("userId",userId);
					model.addAttribute("validationKey",validationKey);
					this.setInDialogue(true,model);

					return "changepassword";
				}
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				this.setInDialogue(false,model);

				return "home";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	@GetMapping("/changePassword")
	public String changePassword(Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			model.addAttribute("formAction","./updatePassword");
			this.setInDialogue(true,model);
			return "changepassword";
		}
		else {
			model.addAttribute("flashMessage","Please login to change your password");
			this.setInDialogue(false,model);

			return "home";
		}
			
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
	
	@GetMapping("/viewEvent")
	public String viewEvent(@RequestParam (name="eventId") Integer eventId, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			Optional<Event> event = eventRepository.findById(eventId);
			if (event.isPresent()) {
				Event eventToView = event.get();
				model.addAttribute("eventToView",eventToView);
				
				List<Resource> staff = resourceRepository.findByType(Resource.Type.STAFF);
				model.addAttribute("staff", staff);
				
				logger.info("Event stadd are {}", eventToView.getStaff());
				this.setInDialogue(true,model);
				return "viewevent";
			}
			else {
				model.addAttribute("flashMessage","Event does not exist");
				return setupCalendar(model);
				
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	@GetMapping("/cancelEvent")
	public String cancelEvent(@RequestParam (name="eventId") Integer eventId, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		this.setInDialogue(false,model);

		if (loggedOnUser != null) {			
				eventRepository.deleteById(eventId);
				return setupCalendar(model);		
				
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			return "home";
		}
	}	
	
	@PostMapping("/changeStudentField")
	public String changeStudentField(@RequestParam (name="students") int studentId, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				Parent loggedOnParent = sessionBean.getLoggedOnParent();
				sessionBean.setSelectedStudent(loggedOnParent.getStudentFromId(studentId));
				
				logger.info("Selected Student = {} for id {}",sessionBean.getSelectedStudent(), studentId);
				return setupCalendar(model);
				
			}
			else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				return setupCalendar(model);
				
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	@PostMapping("/confirmRegisterForEvent")
	public String confirmRegisterForEvent(@RequestParam Map<String,String> allParams, Model model) {
		
		User loggedOnUser = sessionBean.getLoggedOnUser();
		int eventId = Integer.parseInt(allParams.getOrDefault("eventId", "0"));
		
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				User tmpUser = userRepository.findById(loggedOnUser.getUserId()).get();
				
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
						
						return setupCalendar(model);
						
					}
					else {
						model.addAttribute("flashMessage", "Not enough funds to attend this event. Please top up your account.");
						return setupCalendar(model);
						
					}
				}
				else {
					model.addAttribute("flashMessage", "Booking failed due to maximum attendees exceeded.");
					return setupCalendar(model);
										
				}
			}
					
			else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				return setupCalendar(model);
				
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	

	@PostMapping("/confirmUpdateOptionsForSession")
	public String confirmUpdateOptionsForSession(@RequestParam Map<String,String> allParams, Model model) {
		
		User loggedOnUser = sessionBean.getLoggedOnUser();
		int eventId = Integer.parseInt(allParams.getOrDefault("eventId", "0"));
		
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				User tmpUser = userRepository.findById(loggedOnUser.getUserId()).get();
				
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
					
					return setupCalendar(model);
					
				}
				else {
					model.addAttribute("flashMessage", "Not enough funds to attend this event. Please top up your account.");
					return setupCalendar(model);
					
				}				
			}				
			else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				return setupCalendar(model);
				
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}	
	
	
	

	
	@GetMapping("/forgotPassword")
	public String forgotPassword(Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser == null) {
			this.setInDialogue(true,model);

			return "forgottenpassword";
		}
		else {
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			return setupCalendar(model);
			
		}
	}
	
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam (name="email") String email, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		this.setInDialogue(false,model);

		if (loggedOnUser == null) {
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
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				model.addAttribute("flashMessage","Forgotten Password Email Sent");
				return "home";
			}
			else {
				model.addAttribute("flashMessage","Forgotten password email sent");
				return "home";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			return setupCalendar(model);
			
		}
	}
	

	
	
	@GetMapping("/createMedicalNote")
	public String createMedicalNote(@RequestParam (name="studentId") int studentId, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				Student student = sessionBean.getLoggedOnParent().getStudentFromId(studentId);
				model.addAttribute("student",student);
				this.setInDialogue(true,model);

				return "createmedicalnote";
			}
			else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				return setupCalendar(model);
				
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	
	
	@GetMapping("/copyEvent")
	public String copyEvent(@RequestParam (name="day") int day, @RequestParam (name="eventId") int eventId, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()){
				Optional<Event> event = eventRepository.findById(eventId);
				if (event.isPresent()) {
					Event newEvent = new Event(event.get());
					LocalDateTime startDateTime = newEvent.getStartDateTime();
					LocalDateTime endDateTime = newEvent.getEndDateTime();
					startDateTime = startDateTime.plusDays(1);
					endDateTime = endDateTime.plusDays(1);
					while (startDateTime.getDayOfWeek().getValue() != day || startDateTime.isBefore(LocalDateTime.now())) {
						startDateTime = startDateTime.plusDays(1);
						endDateTime = endDateTime.plusDays(1);
					}
					newEvent.setStartDateTime(startDateTime);
					newEvent.setEndDateTime(endDateTime);
					eventRepository.save(newEvent);
					model.addAttribute("flashMessage","Event copied");
					setupCalendar(model);
					return "calendar";
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					setupCalendar(model);
					return "calendar";
				}
			}
			
			else {
				model.addAttribute("flashMessage","Must be an admin to perform this action");
				setupCalendar(model);
				return "calendar";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	@GetMapping("/copyEventAllWeek")
	public String copyEventAllWeek(@RequestParam (name="next", defaultValue="false") boolean next, @RequestParam (name="eventId") int eventId, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()){
				Optional<Event> event = eventRepository.findById(eventId);
				if (event.isPresent()) {
					LocalDateTime startDateTime = event.get().getStartDateTime();
					LocalDateTime endDateTime = event.get().getEndDateTime();
			
					if (next) {
						startDateTime = startDateTime.plusDays(1);
						endDateTime = endDateTime.plusDays(1);
						while (startDateTime.getDayOfWeek().getValue() != 1) {
							startDateTime = startDateTime.plusDays(1);
							endDateTime = endDateTime.plusDays(1);
						}
					}
					else {
						while (startDateTime.getDayOfWeek().getValue() != 1) {
							startDateTime = startDateTime.minusDays(1);
							endDateTime = endDateTime.minusDays(1);
						}
					}
					int copiedEvents = 0;
					while (startDateTime.getDayOfWeek().getValue() >= 1 && startDateTime.getDayOfWeek().getValue() <= 5) {
						if (startDateTime.isAfter(LocalDateTime.now())) {
							if (!startDateTime.equals(event.get().getStartDateTime())) {
								Event newEvent = new Event(event.get());
								newEvent.setStartDateTime(startDateTime);
								newEvent.setEndDateTime(endDateTime);
								eventRepository.save(newEvent);
								copiedEvents++;
							}
						}
						startDateTime = startDateTime.plusDays(1);
						endDateTime = endDateTime.plusDays(1);
					}
					String message = "Copied " + Integer.toString(copiedEvents) + " Events";
					model.addAttribute("flashMessage",message);
					setupCalendar(model);
					return "calendar";
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					setupCalendar(model);
					return "calendar";
				}
			}
			
			else {
				model.addAttribute("flashMessage","Must be an admin to perform this action");
				setupCalendar(model);
				return "calendar";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	@GetMapping("/copyEventAllMonth")
	public String copyEventAllMonth(@RequestParam (name="next", defaultValue="false") boolean next, @RequestParam (name="eventId") int eventId, Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()){
				Optional<Event> event = eventRepository.findById(eventId);
				if (event.isPresent()) {
					LocalDateTime startDateTime = event.get().getStartDateTime();
					LocalDateTime endDateTime = event.get().getEndDateTime();
					int currentMonth = startDateTime.getMonthValue();
					if (next) {
						while (startDateTime.getMonthValue() == currentMonth) {
							startDateTime = startDateTime.plusDays(1);
							endDateTime = endDateTime.plusDays(1);
						}
						currentMonth = startDateTime.getMonthValue();
						
					}
					else {
							startDateTime = startDateTime.minusDays(startDateTime.getDayOfMonth()-1);
							endDateTime = endDateTime.minusDays(startDateTime.getDayOfMonth()-1);
					}
					
					int copiedEvents = 0;
					while (startDateTime.getMonthValue() == currentMonth) {
						if (startDateTime.isAfter(LocalDateTime.now())) {
							if (startDateTime.getDayOfWeek().getValue() >= 1 && startDateTime.getDayOfWeek().getValue() <= 5) {
								if (!startDateTime.equals(event.get().getStartDateTime())) {
									Event newEvent = new Event(event.get());
									newEvent.setStartDateTime(startDateTime);
									newEvent.setEndDateTime(endDateTime);
									eventRepository.save(newEvent);
									copiedEvents++;
								}
							}
						}
						startDateTime = startDateTime.plusDays(1);
						endDateTime = endDateTime.plusDays(1);
					}
					String message = "Copied " + Integer.toString(copiedEvents) + " Events";
					model.addAttribute("flashMessage",message);
					setupCalendar(model);
					return "calendar";
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					setupCalendar(model);
					return "calendar";
				}
			}
			
			else {
				model.addAttribute("flashMessage","Must be an admin to perform this action");
				setupCalendar(model);
				return "calendar";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	
	
	@GetMapping("/topUpBalance")
	public String topUpBalance(Model model) {
		User loggedOnUser = sessionBean.getLoggedOnUser();
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				this.setInDialogue(true,model);

				return "topup";
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


    @PostMapping("/paymentcreate")
    public RedirectView createPayment(
            @RequestParam("amount") String amount,
            Model model
    ) {
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
                    paypalService.getDefaultWebProfileId()
            );
            
            
            for (Links links: payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return new RedirectView(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
        	logger.error("Error occurred:: ", e);
        }
        return new RedirectView("/paymenterror");
    }

    @PostMapping("/paymentsuccess")
    public String paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            Model model
    ) {
    	logger.info("In Payment Success");
    	User loggedOnUser = sessionBean.getLoggedOnUser();
    	
    	if (loggedOnUser != null)
    	{
	    	setupCalendar(model);
	        try {
	            Payment payment = paypalService.executePayment(paymentId, payerId);
	            if (payment.getState().equals("approved")) {
	            	String id = payment.getId();
	            	logger.info("Payment id = {} @ {}" , id, payment.getCreateTime());
	            	
	            	List <Transaction> transactions = payment.getTransactions();
	            	
	        		
	    			Parent loggedOnParent = sessionBean.getLoggedOnParent();
	       		
	        		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	        		LocalDateTime paymentDateTime = LocalDateTime.parse(payment.getCreateTime(), formatter);
	        		
	        				
					for (Transaction transaction : transactions) {
						
						String amount = transaction.getAmount().getTotal();
						int amountInPence  = (int)Double.parseDouble(amount) * 100;
		        		
		        		loggedOnParent.addTransaction(new ParentalTransaction(amountInPence, paymentDateTime,ParentalTransaction.Type.DEPOSIT, "Paypal"));
	
		        		//TODO need to add paypal reference in transaction 
		        		
		        		
						logger.info("Amount = {}", amount);
					}
					userRepository.save(loggedOnUser);
					
					model.addAttribute("flashMessage","Payment Successful");
					
	            }
	        } catch (PayPalRESTException e) {
	        	
				model.addAttribute("flashMessage",e.getDetails().getMessage());
	        	logger.error("Error occurred:: ", e);
	        	
	        }
	        return "calendar";
    	}
    	else {
			model.addAttribute("flashMessage","Need to be logged on to process a payment");
			this.setInDialogue(false,model);

    		return "home";
    	}
    }

    @GetMapping("/reviewpayment")
    public String reviewPayment(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            Model model
    ) {
    	logger.info("In ReviewPayment");
    	User loggedOnUser = sessionBean.getLoggedOnUser();
    	
    	if (loggedOnUser != null)
    	{
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

		        
		        return "reviewPayment";
    		}  catch (PayPalRESTException e) {
	        	
				model.addAttribute("flashMessage",e.getDetails().getMessage());
	        	logger.error("Error occurred:: ", e);
	        	setupCalendar(model);
	        	return "calendar"; 	        	
	        }
	      
    	}
    	else {
			model.addAttribute("flashMessage","Need to be logged on to process a payment");
			this.setInDialogue(false,model);

    		return "home";
    	}
    }
    
    @GetMapping("/paymentcancel")
    public String paymentCancel(Model model) {
		this.setInDialogue(false,model);

		model.addAttribute("flashMessage","Payment Cancelled");
    	User loggedOnUser = sessionBean.getLoggedOnUser();    	
    	if (loggedOnUser != null) {		
    		setupCalendar(model);
    		return "calendar";
    	}
    	else {		
    		return "home";
    	}
    }

    @GetMapping("/paymenterror")
    public String paymentError(Model model) {
		this.setInDialogue(false,model);
		model.addAttribute("flashMessage","Payment Error");
    	User loggedOnUser = sessionBean.getLoggedOnUser();    	
    	if (loggedOnUser != null) {		
    		setupCalendar(model);
    		return "calendar";
    	}
    	else {			
    		return "home";
    	}
    }
    


    
    
}
