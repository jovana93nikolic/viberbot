package com.teamshort.viberbot.database.repository;

import org.springframework.data.repository.CrudRepository;

import com.teamshort.viberbot.database.entity.User;

public interface UserRepository extends CrudRepository<User, Long>{

}
