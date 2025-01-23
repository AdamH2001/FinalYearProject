package com.afterschoolclub.data.respository;

import java.util.List;


import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Club;

public interface ClubRepository extends CrudRepository<Club, Integer> {
	List<Club> findAll();
}
