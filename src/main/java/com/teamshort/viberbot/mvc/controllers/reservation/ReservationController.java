package com.teamshort.viberbot.mvc.controllers.reservation;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.teamshort.viberbot.database.entity.Reservation;
import com.teamshort.viberbot.database.entity.Room;
import com.teamshort.viberbot.database.entity.User;
import com.teamshort.viberbot.database.repository.RoomRepository;
import com.teamshort.viberbot.database.repository.UserRepository;
import com.teamshort.viberbot.service.reservation.ReservationService;

@Controller
public class ReservationController {
	
	@Autowired
	 private ReservationService reservationService;
	
	private RoomRepository roomRepo;
	private UserRepository userRepo;
	

	    @RequestMapping(value = "reservation/reservations", method = RequestMethod.GET)
	    public String list(Model model){
	        model.addAttribute("reservations", reservationService.getAll());
	        return "reservation/reservations";
	    }
	    @RequestMapping("reservation/delete/{id}")
	    public String delete(@PathVariable Long id){
	    	reservationService.delete(id);
	        return "redirect:/reservation/reservations";
	    }   

	   
	    
	    @RequestMapping("/addnewreservation")
			public String process(){
				reservationService.reserve(new Reservation(null, null, "2017-10-05", "15:02"));			
	
				return "reservation/reservations";
}
}