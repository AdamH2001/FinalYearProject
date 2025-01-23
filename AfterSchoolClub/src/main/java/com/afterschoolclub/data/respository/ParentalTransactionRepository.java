package com.afterschoolclub.data.respository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.ParentalTransaction;

public interface ParentalTransactionRepository extends CrudRepository<ParentalTransaction, Integer> {
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE parent_id = :parentId AND date_time < :date")
	Integer getBalance(int parentId, LocalDate date);
	@Query("SELECT * FROM parental_transaction WHERE parent_id = :parentId AND date_time >= :startDate AND date_time < :endDate ORDER BY date_time") 
	List<ParentalTransaction> getMonthlyTransactions(int parentId, LocalDate startDate, LocalDate endDate);
}
