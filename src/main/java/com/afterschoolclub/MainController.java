package com.afterschoolclub;
import jakarta.mail.MessagingException;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;

import com.afterschoolclub.data.Attendee;
import com.afterschoolclub.data.AttendeeMenuChoice;
import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.Incident;
import com.afterschoolclub.data.MedicalNote;
import com.afterschoolclub.data.MenuGroup;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.StudentClass;
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
@SessionAttributes({ "loggedOnUser", "calendarIndex", "calendarMonth", "transactionMonth", "selectedStudent", "event", "student", "available", "unavailable", "attending", "missed", "attended", "onlyMine", "adminFilter", "displayHelper" })

public class MainController {

	private final UserRepository userRepository;
	private final EventRepository eventRepository;
	private final MenuGroupRepository menuGroupRepository;
	private final ResourceRepository resourceRepository;
	private final ClassRepository classRepository;
	private final StudentRepository studentRepository;
	private final ParentalTransactionRepository transactionRepository;
	private final ClubRepository clubRepository;
	
	private final int FILTER_ALL = 0;
	private final int FILTER_FULLYBOOKED = 2;
	private final int FILTER_NOATTENDEES = 3;

    
	@Autowired	
    private PaypalService paypalService;
	

	@Autowired
	private EmailService mailService;
	
	private DisplayHelper displayHelper;
	
	static Logger logger = LoggerFactory.getLogger(MainController.class);
	
	/**
	 * @param userRepository
	 * @param eventRepository
	 * @param menuGroupRepository
	 * @param resourceRepository
	 * @param classRepository
	 */
	public MainController(UserRepository userRepository, EventRepository eventRepository,
			MenuGroupRepository menuGroupRepository, ResourceRepository resourceRepository,
			ClassRepository classRepository, StudentRepository studentRepository, ParentalTransactionRepository transactionRepository, ClubRepository clubRepository) {
		super();
		this.userRepository = userRepository;
		this.eventRepository = eventRepository;
		this.menuGroupRepository = menuGroupRepository;
		this.resourceRepository = resourceRepository;
		this.classRepository = classRepository;
		this.studentRepository = studentRepository;
		this.transactionRepository = transactionRepository;
		this.clubRepository = clubRepository;
		Event.clubRepository = clubRepository;
		Event.resourceRepository = resourceRepository;
		Event.menuGroupRepository = menuGroupRepository;
		Student.classRepository = classRepository;
		Parent.parentalTransactionRepository = transactionRepository;
		this.displayHelper = new DisplayHelper();
		
				
	}
	
	public void setInDialogue(boolean inDialogue, Model model)
	{
		displayHelper.setInDialogue(inDialogue);
		model.addAttribute("displayHelper", displayHelper);
		return;
	}
	
	
	@GetMapping("/createStudent")
	public String createStudent(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				List<StudentClass> classNames = classRepository.findAll();
				model.addAttribute("classNames",classNames);
				this.setInDialogue(true,model);
				return "createstudent";
			} else {
				this.setupCalendar(model);
				return "calendar";
			}
		} else {
			model.addAttribute("flashMessage","Please login to add student");
			this.setInDialogue(false,model);
			return "home";
		}
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
		Student student = new Student(firstName, surname, dateOfBirth, consentToShare);
		student.setClassId(AggregateReference.to(className));
		
		//TODO - remove these thare are just tests
		student.addMedicalNote(new MedicalNote(MedicalNote.Type.ALLERGY,allergyNote));
		student.addMedicalNote(new MedicalNote(MedicalNote.Type.HEALTH,healthNote));
		student.addMedicalNote(new MedicalNote(MedicalNote.Type.DIET, dietNote));
		student.addMedicalNote(new MedicalNote(MedicalNote.Type.CAREPLAN,careNote));
		student.addMedicalNote(new MedicalNote(MedicalNote.Type.MEDICATION,medicationNote));
		student.addMedicalNote(new MedicalNote(MedicalNote.Type.OTHER,otherNote));
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		Parent parent = loggedOnUser.getParentObject();
		parent.addStudent(student);
		userRepository.save(loggedOnUser);
		this.setupCalendar(model);
		return "calendar";
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			Parent loggedOnParent = loggedOnUser.getParentObject();
			loggedOnUser.setFirstName(firstName);
			loggedOnUser.setSurname(surname);
			loggedOnParent.setTelephoneNum(telephoneNum);
			loggedOnParent.setAltContactName(altContactName);
			loggedOnParent.setAltTelephoneNum(altTelephoneNum);
			userRepository.save(loggedOnUser);
			this.setupCalendar(model);
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
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
			this.setupCalendar(model);
			return "calendar";
		}
	}
	
	
	@GetMapping("/createClub")
	public String createClub(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()) {				
				this.setInDialogue(true,model);
				return "createclub";
			} else {
				this.setupCalendar(model);
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		this.setInDialogue(false,model);
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()) {
				Club club = new Club(title, description, basePrice, yearRCanAttend, yearOneCanAttend, yearTwoCanAttend, yearThreeCanAttend, yearFourCanAttend, yearFiveCanAttend, yearSixCanAttend); 
				clubRepository.save(club);
				this.setupCalendar(model);
				return "calendar";
			}
			else {
				this.setupCalendar(model);
				return "calendar";
			}
		}
		else {


			return "home";
		}
	}	
	
	@GetMapping("/createEvent")
	public String createEvent(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
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
				this.setupCalendar(model);
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
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

				this.setupCalendar(model);
				return "calendar";
			}
			else {
				this.setupCalendar(model);
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
					model.addAttribute("loggedOnUser", loginUser);
					
					if (loginUser.isAdmin())
					{
						logger.info("User = {}", loginUser);
						logger.info("Administrator = {}", loginUser.getAdministratorObject());
						logger.info("Resource = {}", loginUser.getAdministratorObject().getResourceObject());
						
					}
					
					this.setupCalendar(model);
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {			
			int num = 0;
			Integer start = (Integer) model.getAttribute("calendarIndex");
			if (start != null) {
				num = start.intValue();
			}
			num -= 35;

			model.addAttribute("calendarIndex", Integer.valueOf(num));
			this.setupCalendar(model);
			return "calendar";
		}
		else
		{
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			this.setInDialogue(false,model);
			return "home";
		}				
	}

	@GetMapping("/calendarForward")

	public String calendarForward(Model model) {
		this.setInDialogue(false,model);
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {			
			int num = 0;
			Integer start = (Integer) model.getAttribute("calendarIndex");
			if (start != null) {
				num = start.intValue();
			}
			num += 35;
			model.addAttribute("calendarIndex", Integer.valueOf(num));
			this.setupCalendar(model);
			return "calendar";
		
		}
		else
		{
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			return "home";
		}		
	}

	@GetMapping("/")
	public String home(Model model) {
		this.setInDialogue(false,model);
	    User loggedOnUser = (User) model.getAttribute("loggedOnUser");
	    if (loggedOnUser == null) {
	    	return "home";
	    }
	    else {
			this.setupCalendar(model);
			return "calendar";
	    }
	}

	@GetMapping("/logout")
	public String home(Model model, SessionStatus status) {
		status.setComplete();
		this.setInDialogue(false,model);
		
		return "redirect:/";
	}

	public void calculateFilters(EventDay eventDay, Model model)
	{
	    User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser.isAdmin() ) {		    
			Boolean onlyMine = (Boolean) model.getAttribute("onlyMine");
			Integer adminFilter= (Integer) model.getAttribute("adminFilter");
			Resource resource = loggedOnUser.getAdministratorObject().getResourceObject();
			
			for (FilteredEvent filteredEvent : eventDay.getFilteredEvents()) {
				Event event = filteredEvent.getEvent();
				if (event.usesResource(resource)) {
					filteredEvent.setAttending(true);						
				}
				else {
					if (onlyMine.booleanValue()) {
						filteredEvent.setHidden(true);
					}
						
				}	
				switch (adminFilter.intValue()) {				
				case FILTER_FULLYBOOKED:
					if (!event.isFullyBooked()) {
						filteredEvent.setHidden(true);
					}
						
					break;
				case FILTER_NOATTENDEES:
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
			Boolean showAttending = (Boolean) model.getAttribute("attending");
			Boolean showAvailable = (Boolean) model.getAttribute("available");
			Boolean showUnavailable = (Boolean) model.getAttribute("unavailable");
			Boolean showMissed = (Boolean) model.getAttribute("missed");
			Boolean showAttended = (Boolean) model.getAttribute("attended");
					
			Student student= (Student) model.getAttribute("selectedStudent");
			
			for (FilteredEvent filteredEvent : eventDay.getFilteredEvents()) {
				Event event = filteredEvent.getEvent();
				
				if (event.endInPast()) {
					if (student != null && event.didAttend(student)) {
						filteredEvent.setAttended(true);
						if (!showAttended.booleanValue()) {
							filteredEvent.setHidden(true);
						}							
					} 
					else if (student != null && event.registered(student)) {
						filteredEvent.setMissed(true);
						if (!showMissed.booleanValue()) {
							filteredEvent.setHidden(true);
						}						
					}
					else {
						filteredEvent.setAvailable(false);
						if (!showUnavailable.booleanValue()) {
							filteredEvent.setHidden(true);
						}
					}
				} 
				else {
					if (student != null && student.isAttendingEvent(event)) {
						filteredEvent.setAttending(true);
						if (!showAttending.booleanValue()) {
							filteredEvent.setHidden(true);
						}
					}
					else if (event.endInPast() || (student != null && !event.canAttend(student))) { 
						filteredEvent.setAvailable(false);	
						if (!showUnavailable.booleanValue()) {
							filteredEvent.setHidden(true);
						}
					}
					else if (!showAvailable.booleanValue()) {
						filteredEvent.setHidden(true);						
					}
				}								 								
			}
		}
			
	}
	
	public void setupCalendar(Model model) {
		int num = 0;
		LocalDate calendarMonth = null;
		LocalDate calendarDay = null;
		LocalDate calendarNextMonth = null;
		
		//TODO - Tidy up and move...  
		
		Boolean attending = (Boolean) model.getAttribute("attending");
		Boolean available = (Boolean) model.getAttribute("available");
		Boolean unavailable = (Boolean) model.getAttribute("unavailable");
		Boolean missed = (Boolean) model.getAttribute("missed");
		Boolean attended = (Boolean) model.getAttribute("attended");
				 
		this.setInDialogue(false,model);

		
		Boolean onlyMine = (Boolean) model.getAttribute("onlyMine");
		Integer adminFilter= (Integer) model.getAttribute("adminFilter");
		
		model.addAttribute("filtersEnabled", Boolean.TRUE);
		
		if (attending == null)
			model.addAttribute("attending", Boolean.TRUE);
		if (available == null)
			model.addAttribute("available", Boolean.TRUE);
		if (unavailable == null)
			model.addAttribute("unavailable", Boolean.TRUE);
		if (missed == null)
			model.addAttribute("missed", Boolean.TRUE);
		if (attended == null)
			model.addAttribute("attended", Boolean.TRUE);
		
		if (onlyMine == null)
			model.addAttribute("onlyMine", Boolean.FALSE);
		if (adminFilter == null)
			model.addAttribute("adminFilter", Integer.valueOf(FILTER_ALL));
				
		
		Student student= (Student) model.getAttribute("selectedStudent");
		logger.info("Selected Student = {}",student);
		if (student == null) {
			User loggedOnUser = (User) model.getAttribute("loggedOnUser");
			Parent loggedOnParent = loggedOnUser.getParentObject();
			if (loggedOnParent != null) {
				Student firstStudent = loggedOnParent.getFirstStudent();
				if (firstStudent != null) {
					model.addAttribute("selectedStudent",firstStudent);
				}
			}
		}
		Integer start = (Integer) model.getAttribute("calendarIndex");
		if (start != null) {
			num = start.intValue();
			model.addAttribute("calendarIndex", Integer.valueOf(num));
			calendarMonth = LocalDate.now().plusMonths(start / 35);
		} else {
			calendarMonth = LocalDate.now();
		}
		
		
		calendarDay = calendarMonth.withDayOfMonth(1);
		calendarNextMonth = calendarDay.plusMonths(1);
		List<Event> events = eventRepository.findEventsBetweenDates(calendarDay, calendarNextMonth);

		
		int numCalendarWeeks = (calendarDay.lengthOfMonth() + calendarDay.getDayOfWeek().getValue() - 1);
		if (numCalendarWeeks % 7 > 0)
			numCalendarWeeks = numCalendarWeeks / 7 + 1;
		else
			numCalendarWeeks = numCalendarWeeks / 7;
						
			
		/// add 1 id remainder above 
		
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
		model.addAttribute("calendarMonth", calendarMonth);
		model.addAttribute("calendar", calendar);
	}
	
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam( name = "password") String password, @RequestParam( name = "conPassword") String conPassword, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (password.equals(conPassword)) {
				loggedOnUser.setPassword(password);
				userRepository.save(loggedOnUser);
				model.addAttribute("flashMessage","Password has been changed");
				this.setupCalendar(model);
				return "calendar";
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
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
		this.setInDialogue(false,model);

		if (model.getAttribute("loggedOnUser") == null) {
			return "home";
		} else {


			
			this.setupCalendar(model);
			return "calendar";

		}

	}
	
	@GetMapping("/viewEvent")
	public String viewEvent(@RequestParam (name="eventId") Integer eventId, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
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
				this.setupCalendar(model);
				return "calendar";
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		this.setInDialogue(false,model);

		if (loggedOnUser != null) {			
				eventRepository.deleteById(eventId);
				this.setupCalendar(model);		
				return "calendar";
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			return "home";
		}
	}	
	
	@PostMapping("/changeStudentField")
	public String changeStudentField(@RequestParam (name="students") int studentId, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				Parent loggedOnParent = loggedOnUser.getParentObject();
				model.addAttribute("selectedStudent",loggedOnParent.getStudentFromId(studentId));
				logger.info("Selected Student = {} for id {}",model.getAttribute("selectedStudent"), studentId);
				this.setupCalendar(model);
				return "calendar";
			}
			else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
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
		
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		int eventId = Integer.parseInt(allParams.getOrDefault("eventId", "0"));
		
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				User tmpUser = userRepository.findById(loggedOnUser.getUserId()).get();
				
				Parent loggedOnParent = tmpUser.getParentObject();
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
					this.setupCalendar(model);
					return "calendar";
				}
				else if ((event.getNumberAttendees() + studentCount) <= event.getMaxAttendees()) {
					if (loggedOnParent.getBalance() >= totalCost) {
						if (studentCount > 0) {
							if ( totalCost > 0) {
								loggedOnParent.addTransaction(new ParentalTransaction(-totalCost,LocalDateTime.now(),ParentalTransaction.Type.PAYMENT, event.getClub().getTitle()));
							}
							
							userRepository.save(tmpUser);
							model.addAttribute("flashMessage", "Booked ".concat(event.getClub().getTitle()));
							model.addAttribute("loggedOnUser", tmpUser);
							
							Student selectedStudent = (Student) model.getAttribute("selectedStudent");
							model.addAttribute("selectedStudent", loggedOnParent.getStudentFromId(selectedStudent.getStudentId()));
							
						}
						else {
							model.addAttribute("flashMessage", "No students selected for booking.");
						}
						
						this.setupCalendar(model);
						return "calendar";
					}
					else {
						model.addAttribute("flashMessage", "Not enough funds to attend this event. Please top up your account.");
						this.setupCalendar(model);
						return "calendar";
					}
				}
				else {
					model.addAttribute("flashMessage", "Booking failed due to maximum attendees exceeded.");
					this.setupCalendar(model);
					return "calendar";					
				}
			}
					
			else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	@GetMapping("/registerForEvent")
	public String registerForEvent(@RequestParam (name="eventId") Integer eventId, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				Optional<Event> event = eventRepository.findById(eventId);
				if (event.isPresent()) {					
					Event eventToView = event.get();
					Student selectedStudent = (Student) model.getAttribute("selectedStudent");

					model.addAttribute("eventToView",eventToView);
					model.addAttribute("selectedStudent",selectedStudent);
					
					List<User> staff = userRepository.findStaffByEventId(eventToView.getEventId());
					model.addAttribute("staff", staff);
					
					model.addAttribute("parent", loggedOnUser.getParentObject());
				
					
					logger.info("Event stadd are {}", eventToView.getStaff());
					this.setInDialogue(true,model);

					return "registerforEvent";
				}
				else {
					model.addAttribute("flashMessage","Event does not exist");
					this.setupCalendar(model);
					return "calendar";
				}
			}
			else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	
	
	
	@GetMapping("/deregisterForEvent")
	public String deregisterForEvent(@RequestParam (name="eventId") int eventId, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		this.setInDialogue(false,model);

		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				Optional<Event> events = eventRepository.findById(eventId);
				Event event = events.get();
				Parent loggedOnParent = loggedOnUser.getParentObject();
				Student selectedStudent = (Student) model.getAttribute("selectedStudent");
				int cost = selectedStudent.getCostOfEvent(event);
				selectedStudent.deregister(eventId);
				
				loggedOnParent.addTransaction(new ParentalTransaction(cost,LocalDateTime.now(), ParentalTransaction.Type.REFUND, event.getClub().getTitle()));
				userRepository.save(loggedOnUser);
				model.addAttribute("flashMessage","Cancelled booking for ".concat(selectedStudent.getFirstName()).concat(" and account refunded."));
				this.setupCalendar(model);
				return "calendar";
				
			}
			else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			return "home";
		}
	}
	
	@GetMapping("/forgotPassword")
	public String forgotPassword(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser == null) {
			this.setInDialogue(true,model);

			return "forgottenpassword";
		}
		else {
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			this.setupCalendar(model);
			return "calendar";
		}
	}
	
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam (name="email") String email, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
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
			this.setupCalendar(model);
			return "calendar";
		}
	}
	
	@GetMapping("/takeRegister")
	public String takeRegister(@RequestParam (name="eventId") int eventId, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()){
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

					return "takeregister";
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					this.setupCalendar(model);
					return "calendar";
				}
			}
			else {
				model.addAttribute("flashMessage","Must be an admin to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	@PostMapping("/addRegister")
	public String addRegister(@RequestParam Map<String,String> register, Model model) {
		Event event = (Event) model.getAttribute("event");
		
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
		this.setupCalendar(model);
		return "calendar";
	}
	
	@PostMapping("/addIncident")
	public String addIncident(@RequestParam(name = "attendee") int attendeeId,
			@RequestParam(name = "summary") String summary, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()) {
				Event event = (Event) model.getAttribute("event");
				Attendee attendee = event.getAttendee(attendeeId);
				Incident newIncident = new Incident(AggregateReference.to(event.getEventId()), summary);
				attendee.addIncident(newIncident);
				eventRepository.save(event);
				model.addAttribute("flashMessage", "Incident has been added");
				this.setupCalendar(model);
				return "calendar";
				

			} else {
				model.addAttribute("flashMessage", "Link out of date");
				this.setupCalendar(model);
				return "calendar";
			}
		}

		else {
			model.addAttribute("flashMessage", "Must be an admin to perform this action");
			this.setupCalendar(model);
			return "calendar";
		}
	}
	
	
	@GetMapping("/createMedicalNote")
	public String createMedicalNote(@RequestParam (name="studentId") int studentId, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				Student student = loggedOnUser.getParentObject().getStudentFromId(studentId);
				model.addAttribute("student",student);
				this.setInDialogue(true,model);

				return "createmedicalnote";
			}
			else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	
	
	@GetMapping("/createIncident")
	public String createIncident(@RequestParam (name="eventId") int eventId, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()){
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

					return "createincident";
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					this.setupCalendar(model);
					return "calendar";
				}
			}
			
			else {
				model.addAttribute("flashMessage","Must be an admin to perform this action");
				this.setupCalendar(model);
				return "calendar";
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
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
					this.setupCalendar(model);
					return "calendar";
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					this.setupCalendar(model);
					return "calendar";
				}
			}
			
			else {
				model.addAttribute("flashMessage","Must be an admin to perform this action");
				this.setupCalendar(model);
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
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
					this.setupCalendar(model);
					return "calendar";
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					this.setupCalendar(model);
					return "calendar";
				}
			}
			
			else {
				model.addAttribute("flashMessage","Must be an admin to perform this action");
				this.setupCalendar(model);
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
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
					this.setupCalendar(model);
					return "calendar";
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					this.setupCalendar(model);
					return "calendar";
				}
			}
			
			else {
				model.addAttribute("flashMessage","Must be an admin to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	/*
	@GetMapping("/topUpBalance")
	public String topUpBalance(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				Parent loggedOnParent = loggedOnUser.getParentObject();
				loggedOnParent.alterBalance(100);
				loggedOnParent.addTransaction(new ParentalTransaction(100,LocalDateTime.now(),ParentalTransaction.Type.DEPOSIT, "Paypal"));
				userRepository.save(loggedOnUser);
				model.addAttribute("flashMessage","Balance has been updated");
				this.setupCalendar(model);
				return "calendar";
			} else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		} else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			return "home";
		}
	}*/
	
	
	@GetMapping("/topUpBalance")
	public String topUpBalance(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				this.setInDialogue(true,model);

				return "topup";
			} else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		} else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
		
		
	@GetMapping("/viewTransactions")
	public String viewTransactions(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				LocalDate start = (LocalDate) model.getAttribute("transactionMonth");
				if (start == null) {
					start = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
					
				}
				LocalDate end = start.plusMonths(1);
				model.addAttribute("transactionMonth",start);
				Parent loggedOnParent = loggedOnUser.getParentObject();
				List<ParentalTransaction> transactions = transactionRepository.getMonthlyTransactions(loggedOnParent.getParentId(),start,end);
				Integer openingBalance = transactionRepository.getBalance(loggedOnParent.getParentId(), start);
				String openingBalanceStr = "";
				NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);
				if (openingBalance == null) {
					openingBalanceStr = n.format(0);
				}
				else {
					openingBalanceStr = n.format(openingBalance.intValue()/100.0);
				}
				 				 
				Integer closingBalance = transactionRepository.getBalance(loggedOnParent.getParentId(), end);
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

				return "viewtransactions";
			} else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		} else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	@GetMapping("/transactionBack")
	public String transactionBack(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				LocalDate start = (LocalDate) model.getAttribute("transactionMonth");
				if (start == null) {
					start = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
					
				}
				else {
					start = start.minusMonths(1);
				}
				model.addAttribute("transactionMonth",start);
				this.setInDialogue(true,model);

				return "redirect:./viewTransactions";
			} else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		} else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			this.setInDialogue(false,model);

			return "home";
		}
	}
	
	@GetMapping("/transactionForward")
	public String transactionForward(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				LocalDate start = (LocalDate) model.getAttribute("transactionMonth");
				if (start == null) {
					start = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
					
				}
				else {
					start = start.plusMonths(1);
				}
				model.addAttribute("transactionMonth",start);
				this.setInDialogue(true,model);

				return "redirect:./viewTransactions";
			} else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
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
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				model.addAttribute("action","updateUser");
				model.addAttribute("editUser", loggedOnUser);
				this.setInDialogue(true,model);

				return "createuser";
			} else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
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
    	User loggedOnUser = (User) model.getAttribute("loggedOnUser");
    	
    	if (loggedOnUser != null)
    	{
	    	this.setupCalendar(model);
	        try {
	            Payment payment = paypalService.executePayment(paymentId, payerId);
	            if (payment.getState().equals("approved")) {
	            	String id = payment.getId();
	            	logger.info("Payment id = {} @ {}" , id, payment.getCreateTime());
	            	
	            	List <Transaction> transactions = payment.getTransactions();
	            	
	        		
	    			Parent loggedOnParent = loggedOnUser.getParentObject();
	       		
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
    	User loggedOnUser = (User) model.getAttribute("loggedOnUser");
    	
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
    	User loggedOnUser = (User) model.getAttribute("loggedOnUser");    	
    	if (loggedOnUser != null) {		
    		this.setupCalendar(model);
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
    	User loggedOnUser = (User) model.getAttribute("loggedOnUser");    	
    	if (loggedOnUser != null) {		
    		this.setupCalendar(model);
    		return "calendar";
    	}
    	else {			
    		return "home";
    	}
    }
    
    @PostMapping("/updateFilters")
    public String updateFilters(
            @RequestParam(name="attending", required=false) Boolean attending,
            @RequestParam(name="available", required=false) Boolean available,
            @RequestParam(name="unavailable", required=false) Boolean unavailable,
            @RequestParam(name="missed", required=false) Boolean missed,
            @RequestParam(name="attended", required=false) Boolean attended,
            
    		Model model) 
    {
		this.setInDialogue(false,model);


    	User loggedOnUser = (User) model.getAttribute("loggedOnUser");    	
    	if (loggedOnUser != null) {	    	
			if (attending == null)
				model.addAttribute("attending",Boolean.FALSE);
			else
				model.addAttribute("attending",Boolean.TRUE);
			if (available == null)
				model.addAttribute("available",Boolean.FALSE);
			else
				model.addAttribute("available",Boolean.TRUE);
			if (unavailable == null)
				model.addAttribute("unavailable",Boolean.FALSE);
			else
				model.addAttribute("unavailable",Boolean.TRUE);
			if (missed == null)
				model.addAttribute("missed",Boolean.FALSE);
			else
				model.addAttribute("missed",Boolean.TRUE);
			if (attended == null)
				model.addAttribute("attended",Boolean.FALSE);
			else
				model.addAttribute("attended",Boolean.TRUE);    	
			
			
			this.setupCalendar(model);
			return "calendar";
    	}
    	else
    		return "home";
    	
    }    

    @PostMapping("/updateAdminFilters")
    public String updateAdminFilters(
            @RequestParam(name="onlyMine", required=false) Boolean onlyMine,
            @RequestParam(name="adminFilter") Integer adminFilter,
    		Model model) 
    {
		this.setInDialogue(false,model);
    	User loggedOnUser = (User) model.getAttribute("loggedOnUser");    	
    	if (loggedOnUser != null) {	    	
	    	if (onlyMine == null)
	    		model.addAttribute("onlyMine",Boolean.FALSE);
	    	else
	    		model.addAttribute("onlyMine",Boolean.TRUE);
	    	model.addAttribute("adminFilter",adminFilter);
	    	
			this.setupCalendar(model);
			return "calendar";
    	}
    	else
    		return "home";
    	
    }   
    
    
}
