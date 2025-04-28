package com.afterschoolclub.data;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.annotation.Id;

import org.springframework.data.relational.core.mapping.Column;

import com.afterschoolclub.data.repository.ClubRepository;
import com.afterschoolclub.service.ClubPicService;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a Club   
 */

@Getter
@Setter
@ToString
public class Club {
		
	/**
	 * Repository to retrieve and store instances
     */	
	public static ClubRepository repository = null;
	
	/**
	 * Service that manages the files for Club pictures 
	 */
	public static ClubPicService clubPicService = null;

	
	/**
	 * Primary Key for Club
	 */
	@Id
	private int clubId;
	
	/**
	 * Title of Club 
	 */
	private String title;
	
	/**
	 * Description of Club 
	 */
	private String description;
	
	/**
	 * Keywords associated with Club to aid search 
	 */
	private String keywords;	

	/**
	 * Base price of club in pennies
	 */
	private int basePrice;
	
	/**
	 * Can this Club accept vouchers as payment
	 */
	private boolean acceptsVouchers = true;
	
	/**
	 * is year R student eligible to attend
	 */
	private boolean yearRCanAttend;
	/**
	 * is year 1 student eligible to attend 
	 */
	@Column("year_1_can_attend")
	private boolean year1CanAttend;
	
	/**
	 * is year 2 student eligible to attend
	 */	
	@Column("year_2_can_attend")
	private boolean year2CanAttend;
	/**
	 * is year 3 student eligible to attend
	 */
	@Column("year_3_can_attend")
	private boolean year3CanAttend;
	
	/**
	 * is year 4 student eligible to attend
	 **/
	@Column("year_4_can_attend")
	private boolean year4CanAttend;
	
	/**
	 * is year 5 student eligible to attend
	 **/
	@Column("year_5_can_attend")
	private boolean year5CanAttend;
	
	/**
	 * is year 6 student eligible to attend 
	 */
	@Column("year_6_can_attend")
	private boolean year6CanAttend;
	
	/**
	 * ACTIVE or INACTIVE
	 */
	private State state = State.ACTIVE;
	
	
	/**
	 * Return all Clubs
	 * @return List of Club
	 */
	public static List<Club> findAll() {		
		return repository.findAll();
	}
	
	/**
	 * Return all active Clubs
	 * @return List of Club
	 */
	public static List<Club> findActive() {
		return repository.findByState(State.ACTIVE);		
	}	
	
	/**
	 * Return specific Club	  
	 * @param title - title of Club
	 * @return Club
	 */
	public static Club findByTitle(String title) {
		List<Club> titleMatched = repository.findByTitle(title);
		Club result = null; 
		if (titleMatched != null && titleMatched.size() > 0) {
			result = titleMatched.get(0);
		}
		return result;		
	}	
	
	
	/**
	 * Return specific Club
	 * @param clubId - primary key for Club
	 * @return Club
	 */
	public static Club findById(int clubId) {
		Optional<Club> optional = repository.findById(clubId);
		Club club = null;
		if (optional.isPresent()) {
			club = optional.get();
		}
		return club;
	}	
	
	/**
	 * Default Constructor
	 */
	public Club() {
		super();	
	}
	

	/**
	 * Constructor 
	 * @param title
	 * @param description
	 * @param basePrice
	 * @param yearRCanAttend
	 * @param year1CanAttend
	 * @param year2CanAttend
	 * @param year3CanAttend
	 * @param year4CanAttend
	 * @param year5CanAttend
	 * @param year6CanAttend
	 * @param keywords
	 */
	public Club(String title, String description, int basePrice, boolean yearRCanAttend,
			boolean year1CanAttend, boolean year2CanAttend, boolean year3CanAttend, boolean year4CanAttend,
			boolean year5CanAttend, boolean year6CanAttend, String keywords) {
		super();
		this.title = title;
		this.description = description;
		this.basePrice = basePrice;
		this.yearRCanAttend = yearRCanAttend;
		this.year1CanAttend = year1CanAttend;
		this.year2CanAttend = year2CanAttend;
		this.year3CanAttend = year3CanAttend;
		this.year4CanAttend = year4CanAttend;
		this.year5CanAttend = year5CanAttend;
		this.year6CanAttend = year6CanAttend;
		this.keywords = keywords;
		
	}
	
	/**
	 * Determin if a student is elible to attend Club sessions
	 * @param student - Student 
	 * @return return true if eligible otherwise return false
	 */
	public boolean isEligible(Student student)
	{
		int yearGroup = student.getStudentClass().getYearGroup();
		boolean result = false;
		
		switch (yearGroup) {
		case 0:
			result = this.yearRCanAttend;
			break;
		case 1:
			result = this.year1CanAttend;
			break;
		case 2:
			result = this.year2CanAttend;
			break;
		case 3:
			result = this.year3CanAttend;
			break;
		case 4:
			result = this.year4CanAttend;
			break;
		case 5:
			result = this.year5CanAttend;
			break;
		case 6:
			result = this.year6CanAttend;
			break;
					
		default:
			result = false;
			
		}
		return result; 
	}
	
	/**
	 * Return UK formatedd cost
	 * @return String
	 */
	public String getFormattedBasePrice() {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);
		return n.format(basePrice / 100.0);
	}
	
	
		
	/**
	 * Return user  friendly view of who can attend this Club
	 * @return String	 */
	public String getSuitableFor()
	{
		String result;
		if (yearRCanAttend && year1CanAttend && year2CanAttend && year3CanAttend && year4CanAttend && year5CanAttend && year6CanAttend)
			result = "Everyone";
		else {
			result = getSuitableYears();
			result = result.concat(" only");

		}
	
		return result;
		
	}	
	

	
	/**
	 * Return a overall RecurrenceSpecification for Club
	 * @return
	 */
	public RecurrenceSpecification getRegularSpecification() {
		List<RecurrenceSpecification> allRecurring = RecurrenceSpecification.findRegularByClubId(clubId);
		RecurrenceSpecification result = new RecurrenceSpecification();

		
		for (RecurrenceSpecification spec : allRecurring) {
			result.occursMonday |= spec.occursMonday;
			result.occursTuesday |= spec.occursTuesday;
			result.occursWednesday |= spec.occursWednesday;
			result.occursThursday |= spec.occursThursday;
			result.occursFriday |= spec.occursFriday;
			result.occursSaturday |= spec.occursSaturday;
			result.occursSunday |= spec.occursSunday;			
		}	
		return result;
	}


	
	/**
	 * Return search friendly view of who can attend this Club
	 * @return String
	 */
	public String getSuitableYears()
	{
		String result = "";

		if (yearRCanAttend) {
			result = "Year R";
		}			
		if (year1CanAttend) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Year 1");
		}
		if (year2CanAttend) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Year 2");
		}
		if (year3CanAttend) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Year 3");
		}
		if (year4CanAttend) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Year 4");
		}		
		if (year5CanAttend) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Year 5");
		}		
		if (year6CanAttend) {
			if (result.length() > 0) {
				result = result.concat(", ");
			}
			result = result.concat("Year 6");
		}				
		int index = result.lastIndexOf(", ");
		if (index != -1) {
			result = result.substring(0, index).concat(" and ").concat(result.substring(index+2));
		}
		return result;
		
	}	
	
	/**
	 * Save this club to the repository
	 */
	public void save()
	{
		repository.save(this);
	}
	
	/**
	 * 
	 * @return the url for the image associated with the club 
	 */
	public String getImageURL() {
		return clubPicService.getImageURL(clubId);		
	}
	
	/**
	 * Return the revenue for this club for a specific academic year
	 * @param startDate
	 * @return amountin pennes
	 */
	public int getRevenueForYearStarting(LocalDate startDate) {
		LocalDate endDate = startDate.plusYears(1);
		endDate =endDate.minusDays(1);
		return ParentalTransaction.getRevenueForClubBetween(this.getClubId(), startDate, endDate);			
	}	
		
}
