package com.afterschoolclub;
import jakarta.mail.MessagingException;

import com.afterschoolclub.data.Attendee;
import com.afterschoolclub.data.Event;
import com.afterschoolclub.data.Incident;
import com.afterschoolclub.data.MedicalNote;
import com.afterschoolclub.data.Parent;
import com.afterschoolclub.data.ParentalTransaction;
import com.afterschoolclub.data.Student;
import com.afterschoolclub.data.StudentClass;
import com.afterschoolclub.data.User;
import com.afterschoolclub.data.EventDay;
import com.afterschoolclub.data.respository.ClassRepository;
import com.afterschoolclub.data.respository.EventRepository;
import com.afterschoolclub.data.respository.MenuGroupRepository;
import com.afterschoolclub.data.respository.ParentalTransactionRepository;
import com.afterschoolclub.data.respository.ResourceRepository;
import com.afterschoolclub.data.respository.StudentRepository;
import com.afterschoolclub.data.respository.UserRepository;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.thymeleaf.context.Context;

@Controller
@SessionAttributes({ "loggedOnUser", "calendarIndex", "calendarMonth", "transactionMonth", "selectedStudent", "event", "student" })

public class MainController {

	private final UserRepository userRepository;
	private final EventRepository eventRepository;
	private final MenuGroupRepository menuGroupRepository;
	private final ResourceRepository resourceRepository;
	private final ClassRepository classRepository;
	private final StudentRepository studentRepository;
	private final ParentalTransactionRepository transactionRepository;
	

	@Autowired
	private EmailService mailService;

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
			ClassRepository classRepository, StudentRepository studentRepository, ParentalTransactionRepository transactionRepository) {
		super();
		this.userRepository = userRepository;
		this.eventRepository = eventRepository;
		this.menuGroupRepository = menuGroupRepository;
		this.resourceRepository = resourceRepository;
		this.classRepository = classRepository;
		this.studentRepository = studentRepository;
		this.transactionRepository = transactionRepository;
	}
	
	@GetMapping("/createStudent")
	public String createStudent(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				List<StudentClass> classNames = classRepository.findAll();
				model.addAttribute("classNames",classNames);
				return "createstudent";
			} else {
				this.setupCalendar(model);
				return "calendar";
			}
		} else {
			model.addAttribute("flashMessage","Please login to add student");
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
		student.addMedicalNote(new MedicalNote("A",allergyNote));
		student.addMedicalNote(new MedicalNote("H",healthNote));
		student.addMedicalNote(new MedicalNote("D",dietNote));
		student.addMedicalNote(new MedicalNote("C",careNote));
		student.addMedicalNote(new MedicalNote("M",medicationNote));
		student.addMedicalNote(new MedicalNote("O",otherNote));
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		Parent parent = loggedOnUser.getParentObject();
		parent.addStudent(student);
		userRepository.save(loggedOnUser);
		this.setupCalendar(model);
		return "calendar";
	}
	
	

	@GetMapping("/createUser")
	public String createUser(Model model) {
		model.addAttribute("formAction","./addUser");
		return "createuser";
	}


	@GetMapping("/log")
	public String log(Model model) {
		logger.trace("A TRACE Message");
		logger.debug("A DEBUG Message");
		logger.info("An INFO Message");
		logger.warn("A WARN Message");
		logger.error("An ERROR Message");
		return "home";
	}

	@PostMapping("/addUser")
	public String addUser(@RequestParam(name = "firstName") String firstName,
			@RequestParam(name = "surname") String surname, @RequestParam(name = "email") String email,
			@RequestParam(name = "password") String password, @RequestParam(name = "conPassword") String conPassword,
			@RequestParam(name = "telephoneNum") String telephoneNum, @RequestParam(name = "altContactName") String altContactName, @RequestParam (name = "altTelephoneNum") String altTelephoneNum, Model model) {
		List<User> users = userRepository.findByEmail(email);
		Encoder encoder = Base64.getEncoder();
		String encodedPass = encoder.encodeToString(password.getBytes());
		if (users.size() == 0 || users == null) {
			if (conPassword.equals(password)) {
				User user = new User(email, encodedPass, firstName, surname, LocalDateTime.now(), false);

				Parent parent = new Parent(telephoneNum,altContactName,altTelephoneNum);
				user.addParent(parent);
				userRepository.save(user);
				logger.info("Saved user{}", user);

				// Send email

				Context context = new Context();
				context.setVariable("user", user);
				String link = "http://localhost:8080/MyProject/validateEmail?userId=" + user.getUserId()
						+ "&validationKey=" + user.getValidationKey();
				context.setVariable("link", link);

				try {
					mailService.sendTemplateEmail(user.getEmail(), "afterschooladmin@hattonsplace.co.uk",
							"Verify Email For Afterschool Club", "verifyemailtemplate", context);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				return "createuser";
			}
		} else {
			User existingUser = users.get(0);
			existingUser.setPassword(encodedPass);
			userRepository.save(existingUser);
		}

		return "home";
	}
	
	@PostMapping("/addDetails")
	public String addDetails(@RequestParam(name = "firstName") String firstName,
			@RequestParam(name = "surname") String surname, @RequestParam(name = "email") String email,
			@RequestParam(name = "telephoneNum") String telephoneNum, @RequestParam(name = "altContactName") String altContactName, @RequestParam (name = "altTelephoneNum") String altTelephoneNum, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			List<User> users = userRepository.findByEmail(email);
			if (users.size() != 0 && users != null) {
				User user = users.get(0);
				if (loggedOnUser.getEmail().equals(user.getEmail())) {
					Parent loggedOnParent = loggedOnUser.getParentObject();
					user.setFirstName(firstName);
					user.setSurname(surname);
					loggedOnParent.setTelephoneNum(telephoneNum);
					loggedOnParent.setAltContactName(altContactName);
					loggedOnParent.setAltTelephoneNum(altTelephoneNum);
					userRepository.save(user);
					this.setupCalendar(model);
					model.addAttribute("flashMessage","Profile has been updated");
					return "calendar";
					
				} else {
					model.addAttribute("flashMessage","Parent must be logged in to perform this action");
					this.setupCalendar(model);
					return "createuser";
				}
			} else {
				model.addAttribute("flashMessage","Profile does not exist");
				this.setupCalendar(model);
				return "calendar";
			}
		}else {
				model.addAttribute("flashMessage","Parent must be logged in to perform this action");
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
							"Email Verified For Tadley After School Club", "emailverifiedtemplate", context);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				model.addAttribute("flashMessage", "Email is now verified");

			}
		}
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
					return "changepassword";
				}
				else {
					model.addAttribute("flashMessage","Link out of date");
					return "home";
				}
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				return "home";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			this.setupCalendar(model);
			return "calendar";
		}
	}
	@GetMapping("/createEvent")
	public String createEvent(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()) {
				return "createevent";
			} else {
				this.setupCalendar(model);
				return "calendar";
			}
		} else {
			return "home";
		}
	}

	@PostMapping("/addEvent")
	public String addEvent(@RequestParam(name = "title") String title,
			@RequestParam(name = "description") String description, 
			@RequestParam(name = "location") String location,
			@RequestParam(name = "startDate") LocalDate startDate,
			@RequestParam(name = "startTime") LocalTime startTime, 
			@RequestParam(name = "endDate") LocalDate endDate,
			@RequestParam(name = "endTime") LocalTime endTime, 
			@RequestParam(name = "basePrice") int basePrice,
			@RequestParam(name = "maxAttendees") int maxAttendees, 
			@RequestParam(name = "yearR", defaultValue="false") boolean yearRCanAttend,
			@RequestParam(name = "year1", defaultValue="false") boolean yearOneCanAttend,
			@RequestParam(name = "year2", defaultValue="false") boolean yearTwoCanAttend,
			@RequestParam(name = "year3", defaultValue="false") boolean yearThreeCanAttend,
			@RequestParam(name = "year4", defaultValue="false") boolean yearFourCanAttend,
			@RequestParam(name = "year5", defaultValue="false") boolean yearFiveCanAttend,
			@RequestParam(name = "year6", defaultValue="false") boolean yearSixCanAttend, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isAdmin()) {
				LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
				LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
				Event event = new Event(title, description, location, startDateTime, endDateTime, basePrice, maxAttendees, yearRCanAttend, yearOneCanAttend, yearTwoCanAttend, yearThreeCanAttend, yearFourCanAttend, yearFiveCanAttend, yearSixCanAttend); 
				eventRepository.save(event);
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
		if (users.size() == 0) {
			model.addAttribute("flashMessage", "Email or Password Incorrect");
			return "home";
		} else {
			User loginUser = users.get(0);
			if (loginUser.isPasswordValid(password)) {
				if (loginUser.isEmailVerified()) {
					model.addAttribute("loggedOnUser", loginUser);
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
		int num = 1;
		Integer start = (Integer) model.getAttribute("calendarIndex");
		if (start != null) {
			num = start.intValue();
		}
		num -= 35;
		if (num < 1) {
			num = 1;
		}
		model.addAttribute("calendarIndex", Integer.valueOf(num));
		this.setupCalendar(model);
		return "calendar";
	}

	@GetMapping("/calendarForward")
	public String calendarForward(Model model) {
		int num = 1;
		Integer start = (Integer) model.getAttribute("calendarIndex");
		if (start != null) {
			num = start.intValue();
		}
		num += 35;
		model.addAttribute("calendarIndex", Integer.valueOf(num));
		this.setupCalendar(model);
		return "calendar";
	}

	@GetMapping("/")
	public String home(Model model) {
		return "home";
	}

	@GetMapping("/logout")
	public String home(Model model, SessionStatus status) {
		status.setComplete();
		return "redirect:/";
	}

	@GetMapping("/allusers")
	public String allUsers(Model model) {
		List<User> users = userRepository.findAll();
		model.addAttribute("users", users);
		return "allusers";
	}

	@GetMapping("/allevents")
	public String allEvents(Model model) {
		List<Event> events = eventRepository.findAll();
		model.addAttribute("events", events);
		return "allevents";
	}

	@GetMapping("/monthEvents")
	public String monthEvents(Model model) {
		List<Event> events = eventRepository.findAll();
		List<Event> monthEvents = new ArrayList<>();
		for (Event event : events) {
			if (event.getStartDateTime().getMonth() == LocalDateTime.now().getMonth()) {
				monthEvents.add(event);
			}
		}
		model.addAttribute("monthEvents", monthEvents);
		return "monthevents";
	}
	
	public void setupCalendar(Model model) {
		int num = 1;
		LocalDate calendarMonth = null;
		LocalDate calendarDay = null;
		LocalDate calendarNextMonth = null;
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
		List<Event> events = eventRepository.findAll();
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
				return "changepassword";
			}
		}
		else {
			model.addAttribute("flashMessage","Please login to change your password");
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
						return "home";
					}
					else {
						model.addAttribute("flashMessage","Link out of date");
						return "home";
					}
				}
				else {
					model.addAttribute("flashMessage","Passwords do not match");
					model.addAttribute("formAction","./updatePasswordWithKey");
					model.addAttribute("userId",userId);
					model.addAttribute("validationKey",validationKey);
					return "changepassword";
				}
			}
			else {
				model.addAttribute("flashMessage","Link out of date");
				return "home";
			}
		}
		else {
			model.addAttribute("flashMessage","Must be logged out to perform this action");
			this.setupCalendar(model);
			return "calendar";
		}
	}
	
	@GetMapping("/changePassword")
	public String changePassword(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			model.addAttribute("formAction","./updatePassword");
			return "changepassword";
		}
		else {
			model.addAttribute("flashMessage","Please login to change your password");
			return "home";
		}
			
	}

	@GetMapping("/calendar")
	public String calendar(Model model) {
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
			return "home";
		}
	}
	
	@GetMapping("/registerForEvent")
	public String registerForEvent(@RequestParam (name="eventId") int eventId, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				Parent loggedOnParent = loggedOnUser.getParentObject();
				Optional<Event> events = eventRepository.findById(eventId);
				Event event = events.get();
				if (loggedOnParent.getBalance() >= event.getBasePrice()) {
					Student selectedStudent = (Student) model.getAttribute("selectedStudent");
					Attendee attendee = new Attendee(AggregateReference.to(eventId), selectedStudent.getStudentId());
					selectedStudent.addAttendee(attendee);
					loggedOnParent.alterBalance(-(event.getBasePrice()));
					loggedOnParent.addTransaction(new ParentalTransaction(-(event.getBasePrice()),LocalDateTime.now(),"A",event.getTitle()));
					userRepository.save(loggedOnUser);
					model.addAttribute("flashMessage", "Registered For Event");
					this.setupCalendar(model);
					return "calendar";
				} else {
					model.addAttribute("flashMessage", "Not enough balance to attend this event");
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
			return "home";
		}
	}
	
	@GetMapping("/deregisterForEvent")
	public String deregisterForEvent(@RequestParam (name="eventId") int eventId, Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				Optional<Event> events = eventRepository.findById(eventId);
				Event event = events.get();
				Parent loggedOnParent = loggedOnUser.getParentObject();
				Student selectedStudent = (Student) model.getAttribute("selectedStudent");
				selectedStudent.deregister(eventId);
				loggedOnParent.alterBalance(event.getBasePrice());
				loggedOnParent.addTransaction(new ParentalTransaction(event.getBasePrice(),LocalDateTime.now(),"R",event.getTitle()));
				userRepository.save(loggedOnUser);
				model.addAttribute("flashMessage","Deregistered For Event");
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
		if (loggedOnUser == null) {
			List<User> users = userRepository.findByEmail(email);
			if (users != null && users.size() > 0) {
				// Send email
				User resetUser = users.get(0);
				resetUser.setValidationKey();
				userRepository.save(resetUser);
				Context context = new Context();
				context.setVariable("user", resetUser);
				String link = "http://localhost:8080/MyProject/alterPassword?userId=" + resetUser.getUserId()
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
			return "home";
		}
	}
	
	@GetMapping("/topUpBalance")
	public String topUpBalance(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				Parent loggedOnParent = loggedOnUser.getParentObject();
				loggedOnParent.alterBalance(100);
				loggedOnParent.addTransaction(new ParentalTransaction(100,LocalDateTime.now(),"D","Paypal"));
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
				return "viewtransactions";
			} else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		} else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
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
				return "redirect:./viewTransactions";
			} else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		} else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
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
				return "redirect:./viewTransactions";
			} else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		} else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			return "home";
		}
	}
	
	@GetMapping("/updateDetails") // complete
	public String updateDetails(Model model) {
		User loggedOnUser = (User) model.getAttribute("loggedOnUser");
		if (loggedOnUser != null) {
			if (loggedOnUser.isParent()) {
				model.addAttribute("formAction","./addDetails");
				return "createuser";
			} else {
				model.addAttribute("flashMessage","Must be a parent to perform this action");
				this.setupCalendar(model);
				return "calendar";
			}
		} else {
			model.addAttribute("flashMessage","Must be logged in to perform this action");
			return "home";
		}
	}


}
