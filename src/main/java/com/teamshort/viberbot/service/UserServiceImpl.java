package com.teamshort.viberbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamshort.viberbot.database.entity.User;
import com.teamshort.viberbot.database.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	

	 private UserRepository userRepository;

	    @Autowired
	    public void setProductRepository(UserRepository userRepository) {
	        this.userRepository = userRepository;
	    }

	    @Override
	    public Iterable<User> findAll() {
	        return userRepository.findAll();
	    }

	   
	    @Override
	    public User saveUser(User user) {
	        return userRepository.save(user);
	    }

	    @Override
	    public void deleteUser(Long id) {
	    	userRepository.delete(id);
	    }

}
