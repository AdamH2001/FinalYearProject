package com.afterschoolclub.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *  Class that encapsulates the data and operations for a Staff of AfterSchool Club
 *  This is used in ReST service to simplify the database complexities of 
 *  User<-> Resource relationships 
 */
 

@Getter
@Setter
public class Staff {

	/**
	 * Return a Staff object for the given user id
	 * @param userId - primary key for the User
	 * @return
	 */
	
	public static Staff findById(int userId) {
		Staff result = null; 
		User user = User.findById(userId);
		if (user!=null) {
			result = new Staff(user);
		}
		return result;
	}	
	
	
	/**
	 * Return all the Staff
	 * @return List of Staff
	 */
	public static List<Staff> findAllActive() {
		List<User> allActiveStaffUsers = User.findStaffByState(State.ACTIVE);
		List<Staff> allStaff = new ArrayList<Staff>();
		
		for (User staffUser : allActiveStaffUsers) {
			allStaff.add(new Staff(staffUser));
		}
		return allStaff;
	}		
	
	
	/**
	 * Set of fields related to the user part of Staff
	 */
	private int userId=0;
	private String email;	
	private String firstName;
	private String surname;
	private String title;
	private String telephoneNum;

	/**
	 * Set of fields related to the Resource part of Staff
	 */

	private int capacity;
	private String description;
	private String keywords;
	private State state;
	private int maxDemand;
		
	/**
	 * Default constructor 
	 */
	public Staff() {
		super();
	}	
	
	
	/**
	 * Create a Staff object from a User objet
	 * @param user
	 */
	public Staff(User user) {
		super();
		userId = user.getUserId();
		email = user.getEmail();
		firstName = user.getFirstName();
		surname = user.getSurname();
		title = user.getTitle();
		telephoneNum = user.getTelephoneNum();
		capacity = user.getResourceObject().getCapacity();
		description = user.getResourceObject().getDescription();
		keywords = user.getResourceObject().getKeywords();
		state = user.getResourceObject().getState();
		maxDemand = user.getResourceObject().getMaxDemand();
	}
	
	/**
	 * Save this member of Staff 
	 * @return the User the is create or updated
	 */
	public User save()
	{
		User user = null;
		Resource resource = null;
		if (userId == 0) {
			user = User.findByEmail(email);
			if (user != null && user.isAdmin() && user.getState() == State.INACTIVE) {
				user.setState(State.ACTIVE);
				resource = user.getResourceObject();
			}
			else {
				String randomPassword = RandomStringUtils.random(12, 0, 20, true, true,
		                "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!£$%^&*()_+-=:,.<>{}[]".toCharArray());				
				
				user = new User();
							
				resource = new Resource();
				user.addResource(resource);
				
				user.setDateRequested(LocalDateTime.now());
				user.setEmailVerified(true);
				user.setAdminVerified(true);

				user.setPassword(randomPassword);	
			}
		} 
		else {
			user = User.findById(userId);
			resource = user.getResourceObject();
		}
		
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setSurname(surname);
		user.setTitle(title);
		user.setTelephoneNum(telephoneNum);
		resource.setQuantity(1);
		resource.setCapacity(capacity);
		resource.setDescription(description);
		resource.setKeywords(keywords);
		resource.setName(String.format("%s %s %s", title, firstName.substring(0, 1), surname));
		resource.setState(state);
		resource.setType(Resource.Type.STAFF);
		
		if (userId == 0 ) {
			user.save();
			userId = user.getUserId();
		}
		else {
			user.update(); // Don't want to force all the resources to update. 
		}	
		return user;
	}
	
	/**
	 * Deactivate the member of Staff
	 * @return
	 */
	public User deactivate()
	{
		User  user = User.findById(userId);
		user.setState(State.INACTIVE);
		user.update();
		
		Resource resource = user.getResourceObject();
		if (resource != null) {
			resource.setState(State.INACTIVE);
			resource.save();
		}			
		return user;
	}
	
	/**
	 * @return true if is a Parent otherwise return false
	 */
	public boolean isParent() {
		return false;
	}
	
}
