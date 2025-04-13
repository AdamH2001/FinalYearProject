package com.afterschoolclub.data;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
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
	
	public static List<ParentalTransaction> getCashTopUps(Parent parent) {
		return repository.getCashTopUps(parent.getParentId());		
	}		

	public static int getBalanceOn(Parent parent, LocalDate start) {
		Integer balance =  repository.getBalance(parent.getParentId(), start);
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}
		return result;
	}		
	

	public static int getRemainingCreditForPayment(String paymentReference) {
		Integer balance =  repository.getRemainingCreditForPayment(paymentReference);
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}
		return result;
	}	
	
	
	
	
	public static int getVoucherBalanceOn(Parent parent, LocalDate start) {
		Integer balance =  repository.getVoucherBalance(parent.getParentId(), start);
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
	
	public static int getVoucherBalance(Parent parent) {
		Integer balance =  repository.getVoucherBalance(parent.getParentId());
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}
		return result;
	}	
	
	public static int getRevenueForClubBetween(int clubId, LocalDate startDate, LocalDate endDate) {
		Integer revenue = repository.getRevenueForClubBetween(clubId, startDate, endDate);		
		int result = 0;
		if (revenue != null) {
			result = revenue.intValue();
		}
		result = result * -1; // Transactions shown as negative numbers against parent. 
		return result;
	}	
	
	public static int getCashPaidForClub(int parentId, int clubId) {
		Integer cash = repository.getCashPaidForClub(parentId, clubId);		
		int result = 0;
		if (cash != null) {
			result = cash.intValue();
		}
		result = result * -1; // Transactions shown as negative numbers against parent. 
		return result;
	}	
	
	
		
	public static int getTotalRevenueBetween(LocalDate startDate, LocalDate endDate) {
		Integer revenue = repository.getTotalRevenueBetween(startDate, endDate);		
		int result = 0;
		if (revenue != null) {
			result = revenue.intValue();
		}
		result = result * -1; // Transactions shown as negative numbers against parent. 
		return result;
	}		
	
	public static int getTotalVoucherBalance() {
		Integer balance = repository.getTotalVoucherBalance();		
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}		
		return result;
	}		
	
	public static int getTotalCashBalance() {
		Integer balance = repository.getTotalCashBalance();		
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}		
		return result;
	}		
	
	public static int getTotalOwed() {
		Integer balance = repository.getTotalOwed();		
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}		
		result = result * -1; // Transactions shown as negative numbers against parent. 

		return result;
	}		
	
	public static int getTotalCashCredit() {
		Integer balance = repository.getTotalCashCredit();		
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
	private String paymentReference;
	AggregateReference<Parent, Integer> parentId = null;
	AggregateReference<Club, Integer> clubId = null;



	public ParentalTransaction() {
		super();

		
	}
	
	
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
	
	/**
	 * @param amount
	 * @param dateTime
	 * @param transactionType
	 * @param description
	 */
	public ParentalTransaction(int amount, LocalDateTime dateTime, Type transactionType, String description, Club club) {
		super();
		this.amount = amount;
		this.dateTime = dateTime;
		this.transactionType = transactionType;
		this.description = description;
		balanceType = BalanceType.CASH;
		this.description = description;
		
		this.clubId = AggregateReference.to(club.getClubId());
	}
	
	
	
	

	
	
	
	public String getFormattedAmount() {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK); 
		String s = n.format(amount / 100.0);
		return s;
	}
	
	public String getFormattedTransactionType() {
		String s = "";
		if (transactionType == Type.PAYMENT) {
			s = "Charge";
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
	
	public boolean isCash() {
			return balanceType == BalanceType.CASH;
	}
	
	public boolean isVoucher() {
		return balanceType == BalanceType.VOUCHER;
	}	
	
	
}
