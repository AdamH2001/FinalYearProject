package com.afterschoolclub.data;

import java.time.LocalDateTime;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import com.afterschoolclub.data.repository.UserRepository;
import com.afterschoolclub.service.PaypalService;
import com.afterschoolclub.service.ProfilePicService;
import com.paypal.base.rest.PayPalRESTException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  Class that encapsulates the data and operations for a User of AfterSchool Club  
 */

@Getter
@Setter
@ToString
public class User {
	static Logger logger = LoggerFactory.getLogger(User.class);    
	
	/**
	 * Repository to retrieve and store instances
	 */	
	public static UserRepository repository = null;
	
	/**
	 * Profile Picture Service
	 */
	public static ProfilePicService profilePicService = null;
	
	/**
	 * PayPal service
	 */
	public static PaypalService paypalService = null;
	
	
	
	/**
	 * Primary key for User
	 */
	@Id
	private int userId;
	
	/**
	 * Email address for user. This is unique across all users
	 */
	private String email;
	
	/**
	 * Encrypted password for User
	 */
	private String password;
	
	/**
	 * first name of User
	 */
	private String firstName;
	
	/**
	 * Surname for User
	 */
	private String surname;
	
	/**
	 * Title  for User e.g Mr, Mrs etc..
	 */
	private String title;
	
	/**
	 * Telephone number for user
	 */
	private String telephoneNum;
	
	
	/**
	 * Key used when sending out verification emails 
	 */
	static Random randomGenerator = new Random();	
	private int validationKey = randomGenerator.nextInt(999999999);
	
	/**
	 * Date time when account was requested 
	 */
	private LocalDateTime dateRequested = LocalDateTime.now();
	
	/**
	 * Indicates if email has been verified or not 
	 */
	private boolean emailVerified = false;
	
	
	/**
	 * Indicates if user has been validate by adminsitrator or not
	 */
	private boolean adminVerified = false;
	
	
	/**
	 * State of the User ACTIVE or INACTIVE
	 */
	private State state = State.ACTIVE;
	
	
	/**
	 * List of resources. Will only have a single resource in it if administrator otherwise will be empty
	 */
	@MappedCollection(idColumn = "user_id")
	private Set<Resource> resources = new HashSet<>();	

	/**
	 * List of parents. Will only have a single parent in it if parent otherwise will be empty
	 */
	
	@MappedCollection(idColumn = "user_id")
	private Set<Parent> parents = new HashSet<>();	
	
	/**
	 * Return user identified by useId
	 * @param userId - primary key for the user
	 * @return User 
	 */
	public static User findById(int userId) {
		Optional<User> optional = repository.findById(userId);
		User user = null;
		if (optional.isPresent()) {
			user = optional.get();
		}
		return user;
	}	
	
	
	/**
	 * Return list of Users that are parents depending on their state and verification flag
	 * @param state - ACTIVE or INACTIVE
	 * @param verified - boolean true of false
	 * @return List of Users
	 */
	public static List<User> findParentByStateVerified(State state, boolean verified) {
		return repository.findParentByStateVerified(state, verified);		
	}		
	
	
	/**
	 * Return the user with a specific email address
	 * @param email - email adddress of user required
	 * @return User
	 */
	public static User findByEmail(String email) {
		List<User> users = repository.findByEmail(email);
		User user = null;
		if (users.size() > 0) {
			user = users.get(0);
		}
		return user;
	}		
		

	/**
	 * Return list of users that have booked on a specfic session
	 * @param sessionId - primary key for the session
	 * @return List of Users
	 */
	public static List<User> findStaffBySessionId(int sessionId) {
		return repository.findStaffBySessionId(sessionId);
		
	}		
	
	
	/**
	 * Return list of users that are in debt
	 * @return List of Users
	 */
	public static List<User> findInDebt() {
		return repository.findInDebt();
		
	}		
		
	
	/**
	 * Return a list of Users that are administrators
	 * @return List of Users
	 */
	public static List<User> findStaff() {
		return repository.findStaff();		
	}			
	
	/**
	 * Return a list of Users that are parents
	 * @return List of Users
	 */
	public static List<User> findParents() {
		return repository.findParents();		
	}			
	
	/**
	 * Returns a list of User Objects that are administrators in a specific state
	 * @param state - state (ACTIVE or INACTIVE)
	 * @return List of Users
	 */
	public static List<User> findStaffByState(State state) {
		return repository.findStaffByState(state);		
	}			
	
	/**
	 * Returns a List of User objects that are ACTIVE and Administrators
	 * @return List of Users
	 */
	public static List<User> findActiveStaff() {
		return repository.findStaffByState(State.ACTIVE);		
		
		
	}	
	
	
		
	
	/**
	 * Update all the different attributes for this user in the repository
	 */
	@Transactional
	public void update() {
		
		repository.update(userId, firstName, surname, email, title, telephoneNum, password, validationKey, dateRequested, emailVerified, state, adminVerified);
		
		Resource r = this.getResourceObject();
		if (r != null) {
			r.update();		
		}
		
		Parent p = this.getParent();
		if (p != null) {
			p.update();		
		}
		
	}
	
	/**
	 * If the user is an administrator results the resource object otherwise returns null
	 * @return Resource object
	 */
	public Resource getResourceObject() {
		Resource result = null;
		Iterator<Resource>  iterator = this.resources.iterator();
		if (iterator.hasNext()) {
			result = iterator.next();
		}
		return result;
	}

	
	/**
	 * Adds a resource for this user. If it user admin then need to add staff as a resource
	 * 
	 * @param resource
	 */
	public void addResource(Resource resource) {
		this.resources.add(resource);
	}
	
	
	/**
	 * @return the url for profile picture for the user
	 */
	public String getImageURL() {
		return profilePicService.getImageURL(userId);		
	}
	


	
	/**
	 * Default constructor
	 */
	public User() {		
		super();
	}
	

	/**
	 * @return the user's full name
	 */
	public String getFullName() {
		return firstName.concat(" ").concat(surname);
	}

	/**
	 * Add the parent object to this user
	 * @param parent 
	 */
	public void addParent(Parent parent) {
		this.parents.add(parent);
	}
	
	/**
	 * Returns true if user is a parent otherwise returns false
	 * @return boolean indicating if parent
	 */
	public boolean isParent() {
		boolean result = false;
		if (this.parents != null && this.parents.size() > 0)
			result = true;
		return result;
	}

	
	/**
	 * Returns true if user is an administrator otherwise returns false
	 * @return boolean indicating if administrator
	 */
	public boolean isAdmin() {
		return !isParent();
	}

	/**
	 * Returns true if the supplied password is valid
	 * @param entPassword - password to check
	 * @return true if valid otherwise fals
	 */
	public boolean isPasswordValid(String entPassword) {
		BCrypt.Result result = BCrypt.verifyer().verify(entPassword.toCharArray(), this.password);
		return result.verified;
	}
	
	/**
	 * Set the users password. Stores it encrypted using BCrypt
	 * 
	 * @param password - new unencrypted password
	 */
	
	public void setPassword(String password) {
		
		this.password = BCrypt.withDefaults().hashToString(12, password.toCharArray());		

	}
	

	/**
	 * If this user is a parent return the Parent object otherwise 
	 * return null
	 * 
	 * @return
	 */
	public Parent getParent() {
		Parent parent = null;
		if (this.isParent())
			parent = (Parent) this.parents.toArray()[0];
		return parent;
	}
	
	
	/**
	 * Resets the validation key. Validation key is used when user needs to verify email etc..
	 * Ensure it is valid key matched with a valid userId
	 */
	public void setValidationKey() {
		this.validationKey = randomGenerator.nextInt(999999999);
	}
	
	
	/**
	 * Delete this user from the repostiory
	 */
	public void delete()
	{
		repository.delete(this);
	}	
	
	/**
	 * Persist this user to the repository
	 */
	public void save()
	{
		repository.save(this);
	}		
	
	/**
	 * Refunds a user all their remaining cash to PayPal.
	 * May fail for PayPal reasons e.g. if transaction over 3 months old
	 * @return true if it managed to refund otherwise returns false.
	 */
	public boolean refund()
	{
		boolean result = false;
		Parent parent = this.getParent();
		
		if (parent != null) {
			int balance = parent.getBalance();
			int remainingRefund = balance;
			
			List<ParentalTransaction> allTopUps = parent.getCashTopUps();
			
			Iterator<ParentalTransaction> itr = allTopUps.iterator();
			
			// Try iterating over all PayPal transactions until can refund User their cash balance
			while (remainingRefund > 0 && itr.hasNext()) {
				ParentalTransaction pt = itr.next();				
				int refundedAmount =0;
				int maxAmountCanRefund = ParentalTransaction.getRemainingCreditForPayment(pt.getPaymentReference());
				int amountCanRefund = Math.min(maxAmountCanRefund,  remainingRefund);
				if (amountCanRefund > 0) {
				
					try {
						refundedAmount = paypalService.refundSale(pt.getPaymentReference(), amountCanRefund);
					}
					catch (PayPalRESTException e) {
						refundedAmount = 0;
					}
					remainingRefund -= refundedAmount;
					logger.info("Refunded {}", refundedAmount);
					
					if (refundedAmount > 0) {
						ParentalTransaction withdrawTrans = new ParentalTransaction(-refundedAmount, LocalDateTime.now(), ParentalTransaction.Type.WITHDRAWAL, "Withdrawn Cash");
						withdrawTrans.setPaymentReference(pt.getPaymentReference());
						withdrawTrans.setParent(parent);
						withdrawTrans.save();
						result = true;
					}
				}
			}
		}
		return result;
	}		
}
