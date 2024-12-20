package com.afterschoolclub.data;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {

	static Random r = new Random();

	@Id
	private int userId;
	private String email;
	private String password;
	private String firstName;
	private String surname;
	private int validationKey = r.nextInt(999999999);
	private LocalDateTime dateRequested;
	private boolean emailVerified;
	@MappedCollection(idColumn = "user_id")
	private Set<Parent> parent = new HashSet<>();

	/**
	 * @param email
	 * @param password
	 * @param firstName
	 * @param surname
	 * @param validationKey
	 * @param dateRequested
	 * @param emailVerified
	 */
	public User(String email, String password, String firstName, String surname, 
			LocalDateTime dateRequested, boolean emailVerified) {
		super();
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.surname = surname;
		this.dateRequested = dateRequested;
		this.emailVerified = emailVerified;
	}

	/**
	 * @return the full name
	 */
	public String getFullName() {
		return firstName.concat(" ").concat(surname);
	}

	public void addParent(Parent parent) {
		this.parent.add(parent);
	}

	public boolean isParent() {
		boolean result = false;
		if (this.parent != null && this.parent.size() > 0)
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
	

	public Parent getParentObject() {
		Parent result = null;
		if (this.isParent())
			result = (Parent) this.parent.toArray()[0];
		return result;
	}
	
	public void setValidationKey() {
		this.validationKey = r.nextInt(999999999);
	}

}
