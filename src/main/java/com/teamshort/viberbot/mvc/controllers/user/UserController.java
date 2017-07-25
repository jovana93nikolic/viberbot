package com.teamshort.viberbot.mvc.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.teamshort.viberbot.database.entity.Room;
import com.teamshort.viberbot.database.entity.User;
import com.teamshort.viberbot.database.repository.UserRepository;
import com.teamshort.viberbot.service.user.UserService;
import com.teamshort.viberbot.service.user.UserServiceImpl;

@Controller
public class UserController {
	
	@Autowired
	 private UserService userService;

	    @RequestMapping(value = "/user/users", method = RequestMethod.GET)
	    public String list(Model model){
	        model.addAttribute("users", userService.findAll());
	        return "user/users";
	    }
	    @RequestMapping("user/delete/{id}")
	    public String delete(@PathVariable Long id){
	        userService.deleteUser(id);
	        return "redirect:/user/users";
	    }   
//		@RequestMapping("/saveusers")
	//	public String process(){
	//		userService.add(new User("randomid","namename",true));			
			
	//		return "user/users";
		
	    
}