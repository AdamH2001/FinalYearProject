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

@Getter
@Setter
@ToString
public class Club {
		
	
	public static ClubRepository repository = null;
	
	public static ClubPicService clubPicService = null;

	
	@Id
	private int clubId;
	private String title;
	private String description;
	private String keywords;	

	private int basePrice;
	
	private boolean acceptsVouchers = true;
	
	private boolean yearRCanAttend;
	@Column("year_1_can_attend")
	private boolean year1CanAttend;
	@Column("year_2_can_attend")
	private boolean year2CanAttend;
	@Column("year_3_can_attend")
	private boolean year3CanAttend;
	@Column("year_4_can_attend")
	private boolean year4CanAttend;
	@Column("year_5_can_attend")
	private boolean year5CanAttend;
	@Column("year_6_can_attend")
	private boolean year6CanAttend;
	
	private State state = State.ACTIVE;
	
	
	public static List<Club> findAll() {		
		return repository.findAll();
	}
	
	public static List<Club> findActive() {
		return repository.findByState(State.ACTIVE);		
	}	
	
	public static Club findByTitle(String title) {
		List<Club> titleMatched = repository.findByTitle(title);
		Club result = null; 
		if (titleMatched != null && titleMatched.size() > 0) {
			result = titleMatched.get(0);
		}
		return result;		
	}	
	
	
	public static Club findById(int sessionId) {
		Optional<Club> optional = repository.findById(sessionId);
		Club club = null;
		if (optional.isPresent()) {
			club = optional.get();
		}
		return club;
	}	
	
	public Club() {
		super();	
	}
	
	/**
	 * @param title
	 * @param description
	 * @param basePrice
	 * @param yearRCanAttend
	 * @param yearOneCanAttend
	 * @param yearTwoCanAttend
	 * @param yearThreeCanAttend
	 * @param yearFourCanAttend
	 * @param yearFiveCanAttend
	 * @param yearSixCanAttend
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
	
	public String getFormattedBasePrice() {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);
		return n.format(basePrice / 100.0);
	}
	
	
		
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
	
	public void save()
	{
		repository.save(this);
	}
	
	public String getImageURL() {
		return clubPicService.getImageURL(clubId);		
	}
	
	public int getRevenueForYearStarting(LocalDate startDate) {
		LocalDate endDate = startDate.plusYears(1);
		endDate =endDate.minusDays(1);
		return ParentalTransaction.getRevenueForClubBetween(this.getClubId(), startDate, endDate);			
	}	
		
}
