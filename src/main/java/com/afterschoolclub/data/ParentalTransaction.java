package com.afterschoolclub.data;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ParentalTransaction {
	@Id
	private int transactionId;
	private int amount;
	private LocalDateTime dateTime;
	private String transactionType;
	private String description;
	
	/**
	 * @param amount
	 * @param dateTime
	 * @param transactionType
	 * @param description
	 */
	public ParentalTransaction(int amount, LocalDateTime dateTime, String transactionType, String description) {
		super();
		this.amount = amount;
		this.dateTime = dateTime;
		this.transactionType = transactionType;
		this.description = description;
	}
	
	public String getFormattedAmount() {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK); 
		String s = n.format(amount / 100.0);
		return s;
	}
	
	public String getFormattedTransactionType() {
		String s = "";
		if (transactionType.equals("A")) {
			s = "Attendance Fee";
		}
		else if (transactionType.equals("D")) {
			s = "Deposit";
		}
		else if (transactionType.equals("R")) {
			s = "Refund";
		}
		return s;
	}
}
