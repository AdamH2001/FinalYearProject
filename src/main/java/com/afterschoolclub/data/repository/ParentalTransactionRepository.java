package com.afterschoolclub.data.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.ParentalTransaction;
/**
 * Repository based on the ParentalTransaction entity
 */
public interface ParentalTransactionRepository extends CrudRepository<ParentalTransaction, Integer> {
	/**
	 * @param parentId
	 * @param date
	 * @return
	 */
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE parent_id = :parentId AND date_time < :date and balance_type='CASH'")
	Integer getBalance(int parentId, LocalDate date);

	/**
	 * Return the cash balance for a specific parent on a particular date 
	 * @param parentId - primary key for the parent
	 * @param date - date
	 * @return parent's total cash balance on specificc date
	 */
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE parent_id = :parentId AND date_time < :date and balance_type='VOUCHER'")
	Integer getVoucherBalance(int parentId, LocalDate date);	
	
	/**
	 * Return the cash balance for a specific parent
	 * @param parentId - primary key for the parent
	 * @return parent's total cash balance
	 */
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE parent_id = :parentId AND balance_type='CASH'")
	Integer getBalance(int parentId);
	
	/**
	 * Return the voucher balance for specific parent
	 * @param parentId - primary key for the parent
	 * @return - parent's total voucher balance
	 */
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE parent_id = :parentId AND balance_type='VOUCHER'")
	Integer getVoucherBalance(int parentId);	
	
	
	/**
	 * Lists of Parental transactions for a specific parent between two dates
	 * @param parentId - primary key for parent
	 * @param startDate = start date
	 * @param endDate - end date
	 * @return list of ParentalTransactions matching search criteria 
	 */
	@Query("SELECT * FROM parental_transaction WHERE parent_id = :parentId AND date_time >= :startDate AND date_time < :endDate ORDER BY date_time") //TODO check is <= for endDate
	List<ParentalTransaction> getTransactions(int parentId, LocalDate startDate, LocalDate endDate);
	
	/**
	 * Total revenue both voucher and cash over a defined period for a specific club
	 * @param clubId - primary key for club
	 * @param startDate - start date
	 * @param endDate - end date
	 * @return - total revenue for club over period
	 */
	
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE club_id = :clubId AND date_time >= :startDate AND date_time <= :endDate ")
	Integer getRevenueForClubBetween(int clubId, LocalDate startDate, LocalDate endDate);
	
	/**
	 * Total revenue both voucher and cash over a defined 	 * Total revenue both voucher and cash over a defined preiod
	 * @param startDate - start date
	 * @param endDate - end date
	 * @return total revenue for period
	 */
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE club_id is not null AND  date_time >= :startDate AND date_time <= :endDate  ")
	Integer getTotalRevenueBetween(LocalDate startDate, LocalDate endDate);	
		
	/**
	 * The total balance of cash e.g. total CashCredit - total CashOwed
	 * @return total cash balance
	 */	
	@Query("SELECT SUM(amount) as total FROM parental_transaction pt, parent p, user u where balance_type ='CASH' AND u.user_id = p.user_id AND u.state='ACTIVE' AND pt.parent_id = p.parent_id")
	Integer getTotalCashBalance();	
	
	/**
	 * Return the total number of vouchers
	 * @return the total amount of vouchers remaining to be spent 
	 */
	@Query("SELECT SUM(amount) as total FROM parental_transaction pt, parent p, user u where balance_type ='VOUCHER' AND  u.user_id = p.user_id AND u.state='ACTIVE' AND pt.parent_id = p.parent_id")
	Integer getTotalVoucherBalance();		
	
	/**
	 * Returns the total amount of owed to AfterSchool CLub account due to overdrafts
	 * @return the total amount of owed 

	 */
	@Query("select sum(sub.totalamount) from (SELECT SUM(pt.amount) as totalamount FROM parental_transaction pt, parent p, user u where balance_type ='CASH' AND u.user_id = p.user_id AND u.state='ACTIVE' AND pt.parent_id = p.parent_id GROUP BY pt.parent_id having  totalamount < 0) sub")
	Integer getTotalOwed();
	
	
	
	/**
	 * Returns the total amount of credit with AfterSchool CLub account
	 * @return the total amount of credit 
	 */
	@Query("select sum(sub.totalamount) from (SELECT SUM(pt.amount) as totalamount FROM parental_transaction pt, parent p, user u where balance_type ='CASH' AND u.user_id = p.user_id AND u.state='ACTIVE' AND pt.parent_id = p.parent_id GROUP BY pt.parent_id having  totalamount > 0) sub")
	Integer getTotalCashCredit();
	
	
	
	/**
	 * Return total paid to club by a parent
	 * @param parentId - primary key for parent
	 * @param clubId - primary key of club
	 * @return total of how much a parent has piad for a specific club
	 */
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE balance_type='CASH' AND club_id =:clubId AND parent_id =:parentId ")
	Integer getCashPaidForClub(int parentId, int clubId);
		
	/**
	 * Returns how much credit we have from a PayPal transaction. For example if topped up £100 and withdrawn £10 then 
	 * still have £90 possible to refund. 
	 * 
	 * @param paymentReference - external payment reference e.g. PayPal transaction Id 
	 * @return total amount of PayPal transaction not refunded
	 */
	@Query("SELECT SUM(amount) as total FROM parental_transaction WHERE payment_reference =:paymentReference")
	Integer getRemainingCreditForPayment(String paymentReference);
			
	/**
	 * @param parentId
	 * @return list of ParentalTransactions matching search criteria 
	 */
	@Query("SELECT * FROM parental_transaction WHERE parent_id = :parentId AND balance_type ='CASH' AND transaction_type = 'DEPOSIT' ORDER BY date_time DESC") 
	List<ParentalTransaction> getCashTopUps(int parentId);	
	
	/**
	 * @param voucherReference
	 * @return list of ParentalTransactions matching search criteria 
	 */
	@Query("SELECT * FROM parental_transaction WHERE payment_reference = :voucherReference") 
	List<ParentalTransaction> findVoucherByReferenceId(String  voucherReference);	

	
}
	   
