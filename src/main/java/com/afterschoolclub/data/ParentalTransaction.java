package com.afterschoolclub.data;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.data.annotation.Id;

import com.afterschoolclub.data.repository.ParentalTransactionRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ParentalTransaction {
	
	public static ParentalTransactionRepository  repository = null;

	
	public enum Type {
		DEPOSIT, 
		WITHDRAWAL, 
		REFUND, 
		PAYMENT
	}
	
	
	public static List<ParentalTransaction>  getTransactions(Parent parent, LocalDate start, LocalDate end) {
		return repository.getTransactions(parent.getParentId(), start, end);		
	}		
	
	
	public static int getBalanceOn(Parent parent, LocalDate start) {
		Integer balance =  repository.getBalance(parent.getParentId(), start);
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}
		return result;
	}		
	
	public static int getBalance(Parent parent) {
		Integer balance =  repository.getBalance(parent.getParentId());
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}
		return result;
	}			
	
	public enum BalanceType {
		VOUCHER, 
		CASH 
	}	
	
	@Id
	private int transactionId;
	private int amount;
	private LocalDateTime dateTime;
	private Type transactionType;
	private BalanceType balanceType;
	private String description;
	
	/**
	 * @param amount
	 * @param dateTime
	 * @param transactionType
	 * @param description
	 */
	public ParentalTransaction(int amount, LocalDateTime dateTime, Type transactionType, String description) {
		super();
		this.amount = amount;
		this.dateTime = dateTime;
		this.transactionType = transactionType;
		this.description = description;
		balanceType = BalanceType.CASH;
	}
	
	public String getFormattedAmount() {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK); 
		String s = n.format(amount / 100.0);
		return s;
	}
	
	public String getFormattedTransactionType() {
		String s = "";
		if (transactionType == Type.PAYMENT) {
			s = "Attendance Fee";
		}
		else if (transactionType == Type.DEPOSIT) {
			s = "Deposit";
		}
		else if (transactionType== Type.REFUND) {
			s = "Refund";
		}
		else if (transactionType== Type.WITHDRAWAL) {
			s = "Withdrawal";
		}
		return s;
	}
}
