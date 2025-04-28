package com.afterschoolclub.data;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import com.afterschoolclub.data.repository.ParentalTransactionRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a ParentalTransaction   
 */

@Getter
@Setter
@ToString
public class ParentalTransaction {
	
	/**
	 * Repository to retrieve and store instances
     */		
	public static ParentalTransactionRepository  repository = null;

	
	public enum Type {
		DEPOSIT, 
		WITHDRAWAL, 
		REFUND, 
		PAYMENT
	}
	/**
	 * Primary key for ParentalTransaction
	 */
	@Id
	private int transactionId;
	/**
	 * Amount in pennies
	 */
	private int amount;
	/**
	 * Time of transaction
	 */
	private LocalDateTime dateTime;
	/**
	 * Type of transaction
	 */
	private Type transactionType;
	/**
	 * VOUCHER or CASH
	 */
	private BalanceType balanceType;
	/**
	 * description of transaction
	 */
	private String description;
	/**
	 * External reference either a PayPal reference or a voucher Reference
	 */
	private String paymentReference;
	/**
	 * Foeign key to Parent
	 */
	AggregateReference<Parent, Integer> parentId = null;
	/**
	 * Foreign key to Club
	 */
	AggregateReference<Club, Integer> clubId = null;

	
	
	/**
	 * Return all transaction for a parent between two dates
	 * @param parent - Parent
	 * @param start = LocalDate
	 * @param end - LocalDate
	 * @return List of ParentalTransaction
	 */
	public static List<ParentalTransaction>  getTransactions(Parent parent, LocalDate start, LocalDate end) {
		return repository.getTransactions(parent.getParentId(), start, end);		
	}		
	
	/**
	 * Return all transaction for a parent
	 * @param parent - Parnet
	 * @return List of ParentalTransaction
	 */
	public static List<ParentalTransaction> getCashTopUps(Parent parent) {
		return repository.getCashTopUps(parent.getParentId());		
	}		

	
	/**
	 * Return specific trnasaction identified by voucherReference
	 * @param voucherReference
	 * @return ParentTransaction
	 */
	public static ParentalTransaction findVoucherByReferenceId(String voucherReference) {
		ParentalTransaction trans = null;
		List<ParentalTransaction> allTrans = repository.findVoucherByReferenceId(voucherReference);
		if (allTrans != null ) {
			Iterator<ParentalTransaction> itr = allTrans.iterator();
			if (itr.hasNext()) {
				trans = itr.next();
			}
		}
		return trans;
	}		
	

	
	
	/**
	 * Return cash balance for a parent on a specifc date 
	 * @param parent - Parent
	 * @param date - LocalDate
	 * @return amount in pennies
	 */
	public static int getBalanceOn(Parent parent, LocalDate date) {
		Integer balance =  repository.getBalance(parent.getParentId(), date);
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}
		return result;
	}		
	
	
	

	/**
	 * Return amount of a PayPal transaction that has not been refunded  
	 * @param paymentReference - PayPal reference
	 * @return amount in pennies
	 */
	public static int getRemainingCreditForPayment(String paymentReference) {
		Integer balance =  repository.getRemainingCreditForPayment(paymentReference);
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}
		return result;
	}	
	
	
	
	
	/**
	 * Return the parents voucher balance on a specific date   
	 * @param parent - Parent
	 * @param date - LocalDate
	 * @return amount in pennies
	 */
	public static int getVoucherBalanceOn(Parent parent, LocalDate date) {
		Integer balance =  repository.getVoucherBalance(parent.getParentId(), date);
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}
		return result;
	}		
	
	/**
	 * Return the parents cash balance
	 * @param parent - Parent
	 * @return amount in pennies
	 */
	public static int getBalance(Parent parent) {
		Integer balance =  repository.getBalance(parent.getParentId());
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}
		return result;
	}			
	
	/**
	 * Return the parents coucher balance
	 * @param parent - Parent
	 * @return amount in pennies
	 */
	public static int getVoucherBalance(Parent parent) {
		Integer balance =  repository.getVoucherBalance(parent.getParentId());
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}
		return result;
	}	
	
	/**
	 * Return the revenue for a Club between two dates
	 * @param clubId - primary identifier for the CLub
	 * @param startDate - LocalDate
	 * @param endDate - LocalDate
	 * @return amount in pennies
	 */
	public static int getRevenueForClubBetween(int clubId, LocalDate startDate, LocalDate endDate) {
		Integer revenue = repository.getRevenueForClubBetween(clubId, startDate, endDate);		
		int result = 0;
		if (revenue != null) {
			result = revenue.intValue();
		}
		result = result * -1; // Transactions shown as negative numbers against parent. 
		return result;
	}	
	
	/**
	 * Return the amount paid to a club by a parent
	 * @param parentId - primary key for parent
	 * @param clubId - primary id for a club
	 * @return amount in pennies
	 */
	public static int getCashPaidForClub(int parentId, int clubId) {
		Integer cash = repository.getCashPaidForClub(parentId, clubId);		
		int result = 0;
		if (cash != null) {
			result = cash.intValue();
		}
		result = result * -1; // Transactions shown as negative numbers against parent. 
		return result;
	}	
	
	
		
	/**
	 * Get the total revenue between two dates
	 * @param startDate - LocalDate
	 * @param endDate - LocalDate
	 * @return amount in pennies
	 */
	public static int getTotalRevenueBetween(LocalDate startDate, LocalDate endDate) {
		Integer revenue = repository.getTotalRevenueBetween(startDate, endDate);		
		int result = 0;
		if (revenue != null) {
			result = revenue.intValue();
		}
		result = result * -1; // Transactions shown as negative numbers against parent. 
		return result;
	}		
	
	/**
	 * Get the overall voucher balance for AFTERSCHOOL CLUB 
	 * @return amount in pennies
	 */
	public static int getTotalVoucherBalance() {
		Integer balance = repository.getTotalVoucherBalance();		
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}		
		return result;
	}		
	
	/**
	 * Get the overall cash balance for AFTERSCHOOL CLUB 	 
	 * @return amount in pennies	 	
	 **/
	 
	public static int getTotalCashBalance() {
		Integer balance = repository.getTotalCashBalance();		
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}		
		return result;
	}		
	
	/**
	 * Return the total amount owed to AFTERSCHOOL CLUB
	 * @return amount in pennies
	 * 	 
	 */
	public static int getTotalOwed() {
		Integer balance = repository.getTotalOwed();		
		int result = 0;
		if (balance != null) {
			result = balance.intValue();
		}		
		result = result * -1; // Transactions shown as negative numbers against parent. 

		return result;
	}		
	
	/**
	 * Return the total Cash in AFTERSCHOOL CLUB
	 * 
	 * @return amount in pennies
	 */
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
	


	/**
	 * Default Constructor
	 */
	public ParentalTransaction() {
		super();

		
	}
	
	
	/**
	 * Constructor for a ParentTransaction
	 * 
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
	 * Constructor for a ParentTransaction
	 * @param amount - in pennies
	 * @param dateTime - time of transaction
	 * @param transactionType - type 
	 * @param description
	 * @param club - CLub
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
	
		
	
	/**
	 * @return amount of transaction according to UK Locale
	 */
	public String getFormattedAmount() {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK); 
		String s = n.format(amount / 100.0);
		return s;
	}
	
	/**
	 * @return string indicatin transaction type
	 */
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
	
	/**
	 * @return true if is a transaction for a cash otherwise return false
	 */
	public boolean isCash() {
			return balanceType == BalanceType.CASH;
	}
	
	/**
	 * @return true if is a transaction for a voucher otherwise return false
	 */
	public boolean isVoucher() {
		return balanceType == BalanceType.VOUCHER;
	}	
	
	/**
	 * Save this transaction to the repository
	 */
	public void save()
	{
		repository.save(this);
	}
	
	/**
	 * Set the parent for this transaction
	 * @param parent - Parent
	 */
	public void setParent(Parent parent) {
		this.setParentId(AggregateReference.to(parent.getParentId()));		
	}
}
