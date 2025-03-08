package com.afterschoolclub.data;

import org.springframework.data.annotation.Id;
import com.afterschoolclub.data.repository.RecurrenceSpecificationRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RecurrenceSpecification {
	public static RecurrenceSpecificationRepository repository = null;

	
	public static void deleteById(int recurrenceSpecificationId) {
		repository.deleteById(recurrenceSpecificationId);		
	}		
	
	
	public static RecurrenceSpecification findById(int recurrenceSpecificationId) {
		Optional<RecurrenceSpecification> optional = repository.findById(recurrenceSpecificationId);
		RecurrenceSpecification recurrenceSpecification = null;
		if (optional.isPresent()) {
			recurrenceSpecification = optional.get();
		}
		return recurrenceSpecification;
	}	
	
	
	@Id
	private int recurrenceSpecificationId;
		
	private LocalDate startDate = LocalDate.now();
	private LocalDate endDate = LocalDate.now();
	boolean occursMonday = false;
	boolean occursTuesday = false;
	boolean occursWednesday = false;
	boolean occursThursday = false;
	boolean occursFriday = false;
	boolean occursSaturday = false;
	boolean occursSunday = false;
	boolean termTimeOnly = false;
	
	
	
	public void save()
	{
		repository.save(this);
		
	}
	
	public void delete()
	{
		RecurrenceSpecification.deleteById(this.getRecurrenceSpecificationId());
		recurrenceSpecificationId = 0;		
		
		
	}	


	public RecurrenceSpecification() {
		super();		
	}
	
	

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
	
	public String getFormattedEndDate() {
		return endDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));		
	}	
}
