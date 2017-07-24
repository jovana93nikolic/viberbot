package com.teamshort.viberbot.mvc.controllers.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.teamshort.viberbot.service.reservation.ReservationService;

@Controller
public class ReservationController {
	
	@Autowired
	 private ReservationService reservationService;

	    @RequestMapping(value = "reservations/reservations", method = RequestMethod.GET)
	    public String list(Model model){
	        model.addAttribute("users", reservationService.getAll());
	        return "reservations/reservations";
	    }
	    @RequestMapping("reservation/delete/{id}")
	    public String delete(@PathVariable Long id){
	    	reservationService.delete(id);
	        return "redirect:/reservations/reservations";
	    }   

}