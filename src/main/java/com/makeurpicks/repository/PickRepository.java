package com.makeurpicks.repository;

import org.springframework.data.repository.CrudRepository;

import com.makeurpicks.domain.Pick;

public interface PickRepository extends CrudRepository<Pick, String> {

}
