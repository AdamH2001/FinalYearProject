package com.afterschoolclub.data.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.ParentalTransaction;

public interface ParentalTransactionRepository extends CrudRepository<ParentalTransaction, Integer> {
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE parent_id = :parentId AND date_time < :date and balance_type='CASH'")
	Integer getBalance(int parentId, LocalDate date);

	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE parent_id = :parentId AND date_time < :date and balance_type='VOUCHER'")
	Integer getVoucherBalance(int parentId, LocalDate date);	
	
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE parent_id = :parentId AND balance_type='CASH'")
	Integer getBalance(int parentId);
	
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE parent_id = :parentId AND balance_type='VOUCHER'")
	Integer getVoucherBalance(int parentId);	
	
	
	@Query("SELECT * FROM parental_transaction WHERE parent_id = :parentId AND date_time >= :startDate AND date_time < :endDate ORDER BY date_time") //TODO check is <= for endDate
	List<ParentalTransaction> getTransactions(int parentId, LocalDate startDate, LocalDate endDate);
	
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE club_id = :clubId AND date_time >= :startDate AND date_time <= :endDate ")
	Integer getRevenueForClubBetween(int clubId, LocalDate startDate, LocalDate endDate);
	
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE club_id is not null AND  date_time >= :startDate AND date_time <= :endDate  ")
	Integer getTotalRevenueBetween(LocalDate startDate, LocalDate endDate);	
		
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE balance_type='CASH' ")
	Integer getTotalCashBalance();	
	
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE balance_type='VOUCHER'")
	Integer getTotalVoucherBalance();		
	
	@Query("select sum(sub.totalamount) from (SELECT SUM(pt.amount) as totalamount  FROM parental_transaction  pt where balance_type ='CASH'  GROUP BY pt.parent_id having  totalamount < 0) sub")
	Integer getTotalOwed();
	
	@Query("select sum(sub.totalamount) from (SELECT SUM(pt.amount) as totalamount  FROM parental_transaction  pt where balance_type ='CASH'  GROUP BY pt.parent_id having  totalamount > 0) sub")
	Integer getTotalCashCredit();
	
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE balance_type='CASH' AND club_id =:clubId AND parent_id =:parentId ")
	Integer getCashPaidForClub(int parentId, int clubId);
		
	@Query("SELECT * FROM parental_transaction WHERE parent_id = :parentId AND balance_type ='CASH' AND transaction_type = 'DEPOSIT' ORDER BY date_time DESC") 
	List<ParentalTransaction> getCashTopUps(int parentId);	
		
}
	
	   
