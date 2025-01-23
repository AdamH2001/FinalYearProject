package com.afterschoolclub.data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import com.afterschoolclub.data.respository.ClubRepository;

import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
		
}
