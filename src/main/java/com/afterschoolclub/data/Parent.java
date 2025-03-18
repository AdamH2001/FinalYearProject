package com.afterschoolclub.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.text.NumberFormat;
import java.time.LocalDate;
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
	private String altContactName;
	private String altTelephoneNum;
	@MappedCollection(idColumn = "parent_id")
	private Set<Student> students = new HashSet<>();
	@MappedCollection(idColumn = "parent_id")
	private Set<ParentalTransaction> transactions = new HashSet<>();

	public Parent(String altContactName, String altTelephoneNum) {
		super();		
		this.altContactName = altContactName;
		this.altTelephoneNum = altTelephoneNum;
	}
	
	public Student getFirstStudent() {
		Student result = null;
		if (students.size() > 0)
			result = (Student) students.toArray()[0];
		return result;
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
	
	public int getBalanceOn(LocalDate date) {
		return ParentalTransaction.getBalanceOn(this, date);			
	}	
	
	
	public String getFormattedBalanceOn(LocalDate date) {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);
		return n.format(getBalanceOn(date)/100.0);		
	}		
	public String getFormattedBalance() {
		NumberFormat n = NumberFormat.getCurrencyInstance(Locale.UK);
		return n.format(this.getBalance() / 100.0);
	}
	
	public boolean hasChildren() {
		return this.getStudents().size() > 0;		
	}
		
}
