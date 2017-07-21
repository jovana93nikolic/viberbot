package com.teamshort.viberbot.service;

import com.teamshort.viberbot.database.entity.User;

/*
 * 
 * findAll()
add(User user)
getByViberId(String viberId)
subscribe(String viberId)
unsubscribe(String viberId)
delete(Long id)
 * 
 * */


public interface UserService {
	Iterable<User> findAll();


    User saveUser(User user);

    void deleteUser(Long id);
	
}
