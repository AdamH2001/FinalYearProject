package com.afterschoolclub.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.transaction.annotation.Transactional;

import com.afterschoolclub.data.ParentalTransaction.BalanceType;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class Parent {
	
	@Id
	public int parentId;
	private String altContactName = "";
	private String altTelephoneNum = "";
	private int overdraftLimit = 0;

	AggregateReference<User, Integer> userId;

	
	@MappedCollection(idColumn = "parent_id")
	private Set<Student> students = new HashSet<>();
	@MappedCollection(idColumn = "parent_id")
	private Set<ParentalTransaction> transactions = new HashSet<>();

	public Parent(String altContactName, String altTelephoneNum) {
		super();		
		this.altContactName = altContactName;
		this.altTelephoneNum = altTelephoneNum;
	}
	
	public Parent() {
		super();		
	}
	
	public Student getFirstStudent() {
		Student result = null;
		if (students.size() > 0)
			result = (Student) students.toArray()[0];
		return result;
	}
	
	static public int getTotalOverdraftLmit() {
		return User.repository.totalOverdraftLimit();
	}
		
	
	public void addStudent(Student student) {
		this.students.add(student);
	}
	
	public void addTransaction(ParentalTransaction transaction) {
		this.transactions.add(transaction);
	}
	
	public List<ParentalTransaction> getAllTransactions() {
		List<ParentalTransaction> allTransactions = new ArrayList<ParentalTransaction>();
		allTransactions.addAll(transactions);
		return allTransactions;
	}
	
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
		
	
	public List<ParentalTransaction>  getTransactions(LocalDate start, LocalDate end) {
		return ParentalTransaction.getTransactions(this, start, end);		
	}		
	
	public int getBalance() {
		return ParentalTransaction.getBalance(this);			
	}	
	
	public int getVoucherBalance() {
		return ParentalTransaction.getVoucherBalance(this);			
	}		
	
	public int getBalanceOn(LocalDate date) {
		return ParentalTransaction.getBalanceOn(this, date);			
	}	
	
	public int getVoucherBalanceOn(LocalDate date) {
		return ParentalTransaction.getVoucherBalanceOn(this, date);			
	}	
		
	
	
	public boolean hasChildren() {
		return this.getStudents().size() > 0;		
	}

	public int numFutureSessionsBooked() {		
		int result = User.repository.numFutureSessionsBooked(parentId);
		return result;
	}

	@Transactional
	public void update()
	{
		User.repository.updateParent(parentId, altContactName, altTelephoneNum, overdraftLimit);
	}

	public int getCashCredit() {
		int result = getBalance();
		if (result < 0 ) {
			result = 0;
		}
		return result;
	}
	
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
	
	public void recordPaymentForClub(int totalCost, Club club, String description) {
		int remaining = totalCost;
		if (club.isAcceptsVouchers()) {
			int voucherBalance = this.getVoucherBalance();
			int voucherCharge = (voucherBalance > remaining) ? remaining :voucherBalance;
			if (voucherCharge > 0) {
				remaining -= voucherCharge;
				ParentalTransaction pt = new ParentalTransaction(-voucherCharge, LocalDateTime.now(), ParentalTransaction.Type.PAYMENT, description, club);
				pt.setBalanceType(BalanceType.VOUCHER);
				addTransaction(pt);
			}
		}
		if (remaining > 0) {
			ParentalTransaction pt = new ParentalTransaction(-remaining, LocalDateTime.now(), ParentalTransaction.Type.PAYMENT, description, club);
			addTransaction(pt);
		}
		return;
	}
	
	public void recordRefundForClub(int totalRefund, Club club, String description) {
		int remaining = totalRefund;
		int voucherBalance = this.getVoucherBalance();
		int totalCashPaidForClub = ParentalTransaction.getCashPaidForClub(this.getParentId(), club.getClubId());
		int cashRefund = (totalCashPaidForClub > remaining) ? remaining :totalCashPaidForClub;
		if (cashRefund > 0) {
			remaining -= cashRefund;
			ParentalTransaction pt = new ParentalTransaction(cashRefund, LocalDateTime.now(), ParentalTransaction.Type.REFUND, description, club);
			addTransaction(pt);
		}
		if (remaining > 0 && club.isAcceptsVouchers()) {
			ParentalTransaction pt = new ParentalTransaction(remaining, LocalDateTime.now(), ParentalTransaction.Type.REFUND, description, club);
			pt.setBalanceType(BalanceType.VOUCHER);
			addTransaction(pt);
		}
		return;
	}	

	public List<ParentalTransaction> getCashTopUps() {
		return ParentalTransaction.getCashTopUps(this);		
	}		
	
	
}
