package com.afterschoolclub.data.respository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.User;

public interface UserRepository extends CrudRepository<User, Integer> {

	List<User> findByEmail(String email);
	List<User> findAll();
}
