package com.afterschoolclub.data;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.data.annotation.Id;

import org.springframework.data.relational.core.mapping.Column;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Club {
	@Id
	private int clubId;
	private String title;
	private String description;	
	private int basePrice;	
	
	
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
			boolean year5CanAttend, boolean year6CanAttend) {
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
		String result = "";
		if (yearRCanAttend && year1CanAttend && year2CanAttend && year3CanAttend && year4CanAttend && year5CanAttend && year6CanAttend)
			result = "Everyone";
		else {
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
			result = result.concat(" only");
		}
		return result;
		
			
	}	
}
