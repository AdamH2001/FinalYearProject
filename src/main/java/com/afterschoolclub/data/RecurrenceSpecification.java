package com.afterschoolclub.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import com.afterschoolclub.data.repository.RecurrenceSpecificationRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a RecurrenceSpecification   
 */

@Getter
@Setter
@ToString
public class RecurrenceSpecification {
	
	/**
	 * Utility to log info and error messages
	 */
	static Logger logger = LoggerFactory.getLogger(RecurrenceSpecification.class);
	
	
	/**
	 * Repository to retrieve and store instances
	 */
	public static RecurrenceSpecificationRepository repository = null;


	
	/**
	 * Primary key for RecurrenceSpecification
	 */
	@Id
	private int recurrenceSpecificationId;
		
	/**
	 * Scheduling start date 
	 */
	private LocalDate startDate = LocalDate.now();
	/**
	 * Scheduling end date 
	 */
	private LocalDate endDate = LocalDate.now();
	/**
	 * True if scheduling on Mondays 
	 */
	boolean occursMonday = false;
	/**
	 * True if scheduling on Tuesdays
	 */
	boolean occursTuesday = false;
	/**
	 * True if scheduling on Wednesdays 
	 */
	boolean occursWednesday = false;
	/**
	 * True if scheduling on Thursdays 
	 */
	boolean occursThursday = false;
	/**
	 * True if scheduling on Fridays
	 */
	boolean occursFriday = false;
	/**
	 * True if scheduling on Saturdays 
	 */
	boolean occursSaturday = false;
	/**
	 * True if scheduling on Sundays 
	 */
	boolean occursSunday = false;
	/**
	 * True if term time only scheduling
	 */
	boolean termTimeOnly = false;
		
	
	/**
	 * Delete specific ReccurrenceSpecification
	 * @param recurrenceSpecificationId - primary key of RecurrenceSpecification
	 */
	public static void deleteById(int recurrenceSpecificationId) {
		repository.deleteById(recurrenceSpecificationId);		
	}		
	
	
	/**
	 * Return a specific Recurrence Specification
	 * @param recurrenceSpecificationId - primary key for RecurrenceSpecification
	 * @return RecurrenceSpecification
	 */
	public static RecurrenceSpecification findById(int recurrenceSpecificationId) {
		Optional<RecurrenceSpecification> optional = repository.findById(recurrenceSpecificationId);
		RecurrenceSpecification recurrenceSpecification = null;
		if (optional.isPresent()) {
			recurrenceSpecification = optional.get();
		}
		return recurrenceSpecification;
	}	
	
	/**
	 * Find the future RecurrenceSpecifcations for a specific Club 
	 * @param clubId - primary key of Club 
	 * @return List of RecurrenceSpecification
	 */
	public static List<RecurrenceSpecification> findRegularByClubId(int clubId) {
		return repository.findRegularByClubId(clubId);
	}		
	
	 
	
	/**
	 * Save this RecurrenceSpecification to the repository 
	 */
	public void save()
	{
		repository.save(this);
		
	}
	
	/**
	 * Delete this RecurrenceSpecification from the repository
	 */
	public void delete()
	{
		RecurrenceSpecification.deleteById(this.getRecurrenceSpecificationId());
		recurrenceSpecificationId = 0;		
		
		
	}	


	/**
	 * Default constructor
	 */
	public RecurrenceSpecification() {
		super();		
	}
	
	

	/**
	 * Create a new RecurrenceSpecification
	 * @param startDate
	 * @param endDate
	 * @param occursMonday
	 * @param occursTuesday
	 * @param occursWednesday
	 * @param occursThursday
	 * @param occursFriday
	 * @param occursSaturday
	 * @param occursSunday
	 * @param termTimeOnly
	 */
	public RecurrenceSpecification(LocalDate startDate, LocalDate endDate, Boolean occursMonday, Boolean occursTuesday,
			Boolean occursWednesday, Boolean occursThursday, Boolean occursFriday, Boolean occursSaturday,
			Boolean occursSunday, Boolean termTimeOnly) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.occursMonday = occursMonday == null ? false : occursMonday.booleanValue();
		this.occursTuesday = occursTuesday == null ? false : occursTuesday.booleanValue();
		this.occursWednesday = occursWednesday == null ? false : occursWednesday.booleanValue();
		this.occursThursday = occursThursday == null ? false : occursThursday.booleanValue();
		this.occursFriday = occursFriday == null ? false : occursFriday.booleanValue();
		this.occursSaturday = occursSaturday == null ? false : occursSaturday.booleanValue();
		this.occursSunday = occursSunday == null ? false : occursSunday.booleanValue();
		this.termTimeOnly = termTimeOnly == null ? false : termTimeOnly.booleanValue();
	}
	
	/**
	 *  Return user friendly  description of recurring days 
	 *  @return String
	 */
	public String getRegularDaysDisplay() {
		String result = "";

		if (occursMonday && occursTuesday && occursWednesday && occursThursday && occursFriday && occursSaturday && occursSunday) {
			result = "Everyday";
		} else {
			if (occursMonday && occursTuesday && occursWednesday && occursThursday && occursFriday) {
				result = "Every weekday";
			}
			else {
				result = getRegularDays();
			}
		}
		
		return result;		
	}	
	
	
	/**
	 *  Return search friendly  description of recurring days 
	 *  @return String
	 */
	public String getRegularDays() {
		String result = "";

		if (occursMonday) {
			result = "Monday";
		}			
		if (occursTuesday) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Tuesday");
		}
		if (occursWednesday) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Wednesday");
		}
		if (occursThursday) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Thursday");
		}
		if (occursFriday) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Friday");
		}		
		if (occursSaturday) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Saturday");
		}		
		if (occursSunday) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Sunday");
		}				
		int index = result.lastIndexOf(", ");
		if (index != -1) {
			result = result.substring(0, index).concat(" and ").concat(result.substring(index+2));
		}		
		return result;		
	}	
	
	/**
	 * Return formatted End date suitable for input fields 
	 * @return String
	 */
	public String getFormattedEndDate() {
		return endDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));		
	}	
	
	/**
	 * Return all the recurring Sessions for this RecurrenceSpecification
	 * @param session
	 * @return List of Session
	 */
	public List<Session> getAllRecurringSessions(Session session) {
		List<Holiday> allHolidays = Holiday.findAll();
		
		List<Session> allSessions = new ArrayList<Session>();

		if (!session.isRecurring()) {
			allSessions.add(session);
		}			
		
		
		logger.info("Recurrence Specfication Id = {}", getRecurrenceSpecificationId());

		
		LocalDate nextDate = this.startDate;
						
		while (session.isRecurring() && nextDate.compareTo(endDate) <= 0) {
			Boolean copy; 
			switch (nextDate.getDayOfWeek()) {
			case MONDAY:
				copy = occursMonday;
				break;
			case TUESDAY:
				copy = occursTuesday;
				break;
				
			case WEDNESDAY:
				copy = occursWednesday;
				break;
				
			case THURSDAY:
				copy = isOccursThursday();
				break;
				
			case FRIDAY:
				copy = occursFriday;
				break;

			case SATURDAY:
				copy = occursSaturday;
				break;

			case SUNDAY:
				copy = occursSunday;
				break;

			default:
				copy = Boolean.FALSE;
				break;
			}
			boolean isRecurringDay = (copy == null) ? false:copy.booleanValue();
				
			if (isRecurringDay && (!termTimeOnly || !Holiday.isDateInHolidays(nextDate, allHolidays))) {						
				Session newSession = new Session(session);
				
				newSession.setStartDateTime(nextDate.atTime(session.getStartDateTime().toLocalTime()));
				newSession.setEndDateTime(nextDate.atTime(session.getEndDateTime().toLocalTime()));
				allSessions.add(newSession);
			}
			nextDate = nextDate.plusDays(1);					
		}				
		return allSessions;
	}		
}
