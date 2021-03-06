package com.teamshort.viberbot.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
	    public void deleteUser(Long id) {
	    	userRepository.delete(id);
	    }

		@Override
		public User add(User user) {
			 return userRepository.save(user);
		}

		@Override
		public User getByViberId(@RequestParam("viberId") String viberId) {
			 return userRepository.findByViberId(viberId);
		}

		@Override
		public void subscribe(@RequestParam("viberId") String viberId) {
			userRepository.findByViberId(viberId).setSubscribe(true);
		}

		@Override
		public void unsubscribe(@RequestParam("viberId") String viberId) {
			
			User user= userRepository.findByViberId(viberId);
			
			user.setSubscribe(false);
			
			//userRepository.save(user);
			add(user);
			
		}



}
