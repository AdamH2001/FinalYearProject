package com.afterschoolclub.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.transaction.annotation.Transactional;

import com.afterschoolclub.data.ParentalTransaction.BalanceType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a Parent   
 */

@Getter
@Setter
@ToString
public class Parent {
	
	/**
	 * Primary key for parent
	 */
	@Id
	public int parentId;
	/**
	 * Alternative contact details 
	 */
	private String altContactName = "";
	/**
	 * Alternative contact details
	 */
	private String altTelephoneNum = "";
	/**
	 * Overdraft limit in pennies
	 */
	private int overdraftLimit = 0;

	/**
	 * Doreign key to User
	 */
	AggregateReference<User, Integer> userId;

	
	/**
	 * Set of students 
	 */
	@MappedCollection(idColumn = "parent_id")
	private Set<Student> students = new HashSet<>();
	/**
	 * Set of transactions
	 */
	@MappedCollection(idColumn = "parent_id")
	private Set<ParentalTransaction> transactions = new HashSet<>();

	/**
	 * Constructor 
	 * @param altContactName - alternative contact details
	 * @param altTelephoneNum - alternative contact details
	 */
	public Parent(String altContactName, String altTelephoneNum) {
		super();		
		this.altContactName = altContactName;
		this.altTelephoneNum = altTelephoneNum;
	}
	
	/**
	 * Default Constructor
	 */
	public Parent() {
		super();		
	}
	
	
	/**
	 * @return
	 */
	public Student getFirstStudent() {
		List<Student> allActiveVerifiedStudents = getStudents();
		Student result = null;
		if (allActiveVerifiedStudents.size() > 0) {
			result = allActiveVerifiedStudents.get(0);
		}
		return result;
	}
	
	/**
	 * Return students for this parent
	 * @return List of Students
	 */
	public List<Student> getStudents() {
		List<Student> allActiveVerifiedStudents = new ArrayList<Student>();
		for (Student student : students) {
			if (student.isAdminVerified() && student.getState() == State.ACTIVE) {
				allActiveVerifiedStudents.add(student);
			}
				
		}
		return allActiveVerifiedStudents;
	}
	
	/**
	 * Return the overdraft limit for this parent
	 * @return balance in pennies
	 */
	static public int getTotalOverdraftLmit() {		
		Integer limit = User.repository.totalOverdraftLimit();		
		int result = 0;
		if (limit != null) {
			result = limit.intValue();
		}		
		return result;
	}
		
	
	/**
	 * Add a student to this Parent
	 * @param student - Student
	 */
	public void addStudent(Student student) {
		this.students.add(student);
	}
	
	/**
	 * Add a transaction to this parents log 
	 * @param transaction - ParentalTransaction
	 */
	public void addTransaction(ParentalTransaction transaction) {
		this.transactions.add(transaction);
	}
	
	/**
	 * Return all transactions for this Parent
	 * @return List of ParentTransactions
	 */
	public List<ParentalTransaction> getAllTransactions() {
		List<ParentalTransaction> allTransactions = new ArrayList<ParentalTransaction>();
		allTransactions.addAll(transactions);
		return allTransactions;
	}
	
	/**
	 * Reutrn the student for this parent
	 * @param studentId - primary key for Student
	 * @return Student
	 */
	public Student getStudentFromId(int studentId) {
		Student result = null;
		Iterator<Student> studentIterator = students.iterator();
		
		while (result == null && studentIterator.hasNext()) {
			Student compare = studentIterator.next();
			if (compare.getStudentId() == studentId)
				result = compare;
		}
		return result;
	}
		
	
	/**
	 * Return transactions between start and end date
	 * @param start - LocalDate
	 * @param end - LocalDate
	 * @return List of ParentTransactions
	 */
	public List<ParentalTransaction>  getTransactions(LocalDate start, LocalDate end) {
		return ParentalTransaction.getTransactions(this, start, end);		
	}		
	
	/**
	 * Return the cash balance for this parent 
	 * @return balance in pennies
	 */
	public int getBalance() {
		return ParentalTransaction.getBalance(this);			
	}	
	
	/**
	 * Return the voucher balance for this parent 
	 * @return balance in pennies
	 */
	public int getVoucherBalance() {
		return ParentalTransaction.getVoucherBalance(this);			
	}		
	
	/**
	 * Return the cash balance for this parent on a specific day
	 * @param date - LocalDate
	 * @return balance in pennies
	 */
	public int getBalanceOn(LocalDate date) {
		return ParentalTransaction.getBalanceOn(this, date);			
	}	
	
	/**
	 * Return the voucher balance for this parent on a specific day
	 * @param date - LocalDate
	 * @return balance in pennies
	 */
	public int getVoucherBalanceOn(LocalDate date) {
		return ParentalTransaction.getVoucherBalanceOn(this, date);			
	}	
		
	
	
	/**
	 * @return true if has children otherwise return false
	 */
	public boolean hasChildren() {
		return this.getStudents().size() > 0;		
	}

	/**
	 * @return number of future sessions this parent has paid for 
	 */
	public int numFutureSessionsBooked() {		
		int result = User.repository.numFutureSessionsBooked(parentId);
		return result;
	}

	/**
	 * Update the details for this parent in the repository
	 */
	@Transactional
	public void update()
	{
		User.repository.updateParent(parentId, altContactName, altTelephoneNum, overdraftLimit);
	}

	/**
	 * Return the amount this parent is in credit
	 * @return amount in pennies
	 */	 
	public int getCashCredit() {
		int result = getBalance();
		if (result < 0 ) {
			result = 0;
		}
		return result;
	}
	
	/**
	 * Return the amount this parent is in debt
	 * @return amount in pennies
	 */
	public int getCashDebt() {
		int result = getBalance();
		if (result > 0 ) {
			result = 0;
		}
		else {
			result = result * -1;
		}
		return result;
	}	

	/**
	 * Return true if this parent can afford  charge for a Club. 
	 * Some clubs accet=pt vouchers others don't 
	 * @param charge - amount in pennies
	 * @param club - Club
	 * @return true if can afford otherwise return false
	 */
	public boolean canAfford(int charge,Club club) {
		int remaining = charge;
		if (club.isAcceptsVouchers()) {
			int voucherBalance = this.getVoucherBalance();
			int voucherCharge = (voucherBalance > remaining) ? remaining :voucherBalance;
			remaining -= voucherCharge;
		}
		
		if (remaining > 0) {
			int cashAvailable = getBalance() + overdraftLimit;
			int cashCharge = (cashAvailable > remaining) ? remaining :cashAvailable;
			remaining -= cashCharge;

		}
		return remaining == 0;
	}
	
	/**
	 * Record a payment for a club
	 * @param totalRefund - amount in pennies
	 * @param club - Club
	 * @param description - reason for refund
	 * @return ParentalTransaction
	 */
	public ParentalTransaction recordPaymentForClub(int totalCost, Club club, String description) {
		int remaining = totalCost;
		ParentalTransaction pt = null;	
		if (club.isAcceptsVouchers()) {
			int voucherBalance = this.getVoucherBalance();
			int voucherCharge = (voucherBalance > remaining) ? remaining :voucherBalance;
			if (voucherCharge > 0) {
				remaining -= voucherCharge;
				pt = new ParentalTransaction(-voucherCharge, LocalDateTime.now(), ParentalTransaction.Type.PAYMENT, description, club);
				pt.setBalanceType(BalanceType.VOUCHER);
				addTransaction(pt);
			}
		}
		if (remaining > 0) {
			pt = new ParentalTransaction(-remaining, LocalDateTime.now(), ParentalTransaction.Type.PAYMENT, description, club);
			addTransaction(pt);
		}
		return pt;
	}
	
	/**
	 * Record a refund for a club
	 * @param totalRefund - amount in pennies
	 * @param club - Club
	 * @param description - reason for refund
	 * @return ParentalTransaction
	 */
	public ParentalTransaction recordRefundForClub(int totalRefund, Club club, String description) {
		int remaining = totalRefund;
	//	int voucherBalance = this.getVoucherBalance();
		int totalCashPaidForClub = ParentalTransaction.getCashPaidForClub(this.getParentId(), club.getClubId());
		int cashRefund = (totalCashPaidForClub > remaining) ? remaining :totalCashPaidForClub;
		ParentalTransaction pt = null;
		if (cashRefund > 0) {
			remaining -= cashRefund;
			pt = new ParentalTransaction(cashRefund, LocalDateTime.now(), ParentalTransaction.Type.REFUND, description, club);			
			addTransaction(pt);
		}
		if (remaining > 0 && club.isAcceptsVouchers()) {
			pt = new ParentalTransaction(remaining, LocalDateTime.now(), ParentalTransaction.Type.REFUND, description, club);
			pt.setBalanceType(BalanceType.VOUCHER);			
			addTransaction(pt);
		}
		pt.setParent(this);
		
		return pt;
	}	

	/**
	 * Return all the topups for this parent
	 * @return List of ParentalTransaction
	 */
	public List<ParentalTransaction> getCashTopUps() {
		return ParentalTransaction.getCashTopUps(this);		
	}		
	
	
}
