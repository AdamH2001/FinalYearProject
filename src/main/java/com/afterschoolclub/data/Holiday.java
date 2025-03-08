package com.afterschoolclub.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.afterschoolclub.data.repository.HolidayRepository;

@Getter
@Setter
@ToString
public class Holiday {

	public static HolidayRepository repository = null;

	
	@Id
	private int holidayId;
	private LocalDate startDate;
	private LocalDate endDate;
	
	public static List<Holiday> findAll() {		
		return repository.findAll();
	}	
	
	public static boolean isHoliday(LocalDate date) {
		List<Holiday> holidays = repository.findByDate(date);
		return holidays.size() > 0;		
	}
	
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
