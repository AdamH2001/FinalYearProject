package com.afterschoolclub.data.respository;

import org.springframework.data.repository.CrudRepository;

import com.afterschoolclub.data.Resource;

public interface ResourceRepository extends CrudRepository<Resource, Integer> {

}
