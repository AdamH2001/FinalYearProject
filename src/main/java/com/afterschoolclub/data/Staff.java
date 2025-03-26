package com.afterschoolclub.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Staff {

	public static Staff findById(int userId) {
		Staff result = null; 
		User user = User.findById(userId);
		if (user!=null) {
			result = new Staff(user);
		}
		return result;
	}	
	
	
	public static List<Staff> findAllActive() {
		List<User> allActiveStaffUsers = User.findStaffByState(State.ACTIVE);
		List<Staff> allStaff = new ArrayList<Staff>();
		
		for (User staffUser : allActiveStaffUsers) {
			allStaff.add(new Staff(staffUser));
		}
		return allStaff;
	}		
	
	private int userId=0;
	private String email;	
	private String firstName;
	private String surname;
	private String title;
	private String telephoneNum;
	
	private int capacity;
	private String description;
	private String keywords;
	private State state;
	private int maxDemand;
		
	public Staff() {
		super();
	}	
	
	
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
	
	@Transactional
	public void save()
	{
		User user = null;
		Resource resource = null;
		if (userId == 0) {
			user = new User();
						
			resource = new Resource();
			user.addResource(resource);
			
			user.setDateRequested(LocalDateTime.now());
			user.setEmailVerified(true);
			user.setPassword("somecrap");			
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
			resource.save();
		}				
	}
	
	public void inActivate()
	{
		User  user = User.findById(userId);
		Resource resource = user.getResourceObject();
		resource.setState(State.INACTIVE);
		resource.save();

	}
	
}
