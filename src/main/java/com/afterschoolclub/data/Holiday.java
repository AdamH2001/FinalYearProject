package com.afterschoolclub.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.afterschoolclub.data.repository.HolidayRepository;

/**
 *  Class that encapsulates the data and operations for a Holiday   
 */

@Getter
@Setter
@ToString
public class Holiday {

	/**
	 * Repository to retrieve and store instances
	 */
	public static HolidayRepository repository = null;

	
	/**
	 * Primary key for Holiday
	 */
	@Id
	private int holidayId;
	/**
	 * Start Date of holiday 
	 */
	private LocalDate startDate;
	/**
	 * End Date of holiday
	 */
	private LocalDate endDate;
	
	/**
	 * Return list of all Holidays
	 * @return List of Holiday
	 */
	public static List<Holiday> findAll() {		
		return repository.findAll();
	}	
	
	/**
	 * Determine if a date is in the holidays 
	 * @param date - LocalDate
	 * @return true if date in in holidays other return false
	 */
	public static boolean isHoliday(LocalDate date) {
		List<Holiday> holidays = repository.findByDate(date);
		return holidays.size() > 0;		
	}
	
	/**
	 * Determine if a date is in the holidays given a list of holidays
	 * @param date - LocalDate
	 * @param holidays - List of Holidays
	 * @return true if date in in holidays other return false
	 */
	public static boolean isDateInHolidays(LocalDate date, List<Holiday> holidays) {
		boolean foundHoliday = false;
		Iterator <Holiday> iterator = holidays.iterator();
		
		while (!foundHoliday && iterator.hasNext()) {
			Holiday nextHoliday = iterator.next();			
			foundHoliday = (date.compareTo(nextHoliday.getStartDate()) >= 0) && (date.compareTo(nextHoliday.getEndDate()) <= 0);  
		}
				
		return foundHoliday;
		
	}		
		

}
