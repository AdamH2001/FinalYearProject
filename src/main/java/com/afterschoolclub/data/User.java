package com.afterschoolclub.data;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.Set;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import com.afterschoolclub.data.repository.UserRepository;
import com.afterschoolclub.service.ProfilePicService;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {

	
    @Value("${file.upload-dir}")
    private String uploadDir;
    
	public static UserRepository repository = null;
	public static ProfilePicService profilePicService = null;
	
	static Random r = new Random();

	@Id
	private int userId;
	private String email;
	private String password;
	private String firstName;
	private String surname;
	private String title;
	private String telephoneNum;
	private int validationKey = r.nextInt(999999999);
	private LocalDateTime dateRequested;
	private boolean emailVerified;
	private State state = State.ACTIVE;
	
	
	@MappedCollection(idColumn = "user_id")
	private Set<Resource> resources = new HashSet<>();	
	
	@MappedCollection(idColumn = "user_id")
	private Set<Parent> parents = new HashSet<>();	
	
	public static User findById(int userId) {
		Optional<User> optional = repository.findById(userId);
		User user = null;
		if (optional.isPresent()) {
			user = optional.get();
		}
		return user;
		
		
	}	
	
	public static User findByEmail(String email) {
		List<User> users = repository.findByEmail(email);
		User user = null;
		if (users.size() > 0) {
			user = users.get(0);
		}
		return user;
	}		
		

	public static List<User> findStaffBySessionId(int sessionId) {
		return repository.findStaffBySessionId(sessionId);
		
	}		
	
	
	public static List<User> findUsersInDebt() {
		return repository.findUsersInDebt();
		
	}		
	
	
	
	public static List<User> findStaff() {
		return repository.findStaff();		
	}			
	
	public static List<User> findParents() {
		return repository.findParents();		
	}			
	
	public static List<User> findStaffByState(State state) {
		return repository.findStaffByState(state);		
	}			
	
	public static List<User> findActiveStaff() {
		return repository.findStaffByState(State.ACTIVE);		
	}	
	
	public static List<User> findInDebt() {
		return repository.findUsersInDebt();		
	}	
	
		
	
	@Transactional
	public void update() {
		
		repository.update(userId, firstName, surname, email, title, telephoneNum, password, validationKey, dateRequested, emailVerified, state);
		
		Resource r = this.getResourceObject();
		if (r != null) {
			r.update();		
		}
		
		Parent p = this.getParent();
		if (p != null) {
			p.update();		
		}
		
	}
	
	public Resource getResourceObject() {
		Resource result = null;
		Iterator<Resource>  iterator = this.resources.iterator();
		if (iterator.hasNext()) {
			result = iterator.next();
		}
		return result;
	}

	
	public void addResource(Resource resource) {
		this.resources.add(resource);
	}
	
	
	public String getImageURL() {
		return profilePicService.getImageURL(userId);		
	}
	

	/**
	 * @param email
	 * @param password
	 * @param firstName
	 * @param surname
	 * @param validationKey
	 * @param dateRequested
	 * @param emailVerified
	 */
	public User(String email, String password, String title, String firstName, String surname, String telephoneNum,
			LocalDateTime dateRequested, boolean emailVerified) {
		super();
		this.email = email;
		this.setPassword(password);		
		this.firstName = firstName;
		this.surname = surname;
		this.telephoneNum = telephoneNum;
		this.title = title; 
		this.dateRequested = dateRequested;
		this.emailVerified = emailVerified;
	}
	
	public User() {		
		super();
	}
	

	/**
	 * @return the full name
	 */
	public String getFullName() {
		return firstName.concat(" ").concat(surname);
	}

	public void addParent(Parent parent) {
		this.parents.add(parent);
	}
	

	public boolean isParent() {
		boolean result = false;
		if (this.parents != null && this.parents.size() > 0)
			result = true;
		return result;
	}

	public boolean isAdmin() {
		return !isParent();
	}

	public boolean isPasswordValid(String entPassword) {
		boolean valid = false;
		Encoder encoder = Base64.getEncoder();
		String encodedPass = encoder.encodeToString(entPassword.getBytes());
		if (encodedPass.equals(this.password)) {
			valid = true;
		}
		return valid;
	}
	
	public void setPassword(String password) {
		Encoder encoder = Base64.getEncoder();
		this.password = encoder.encodeToString(password.getBytes());
	}
	

	public Parent getParent() {
		Parent parent = null;
		if (this.isParent())
			parent = (Parent) this.parents.toArray()[0];
		return parent;
	}
	

	
	public void setValidationKey() {
		this.validationKey = r.nextInt(999999999);
	}
	
	
	public void save()
	{
		repository.save(this);
	}		
}
