package com.teamshort.viberbot.mvc.controllers.room;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.teamshort.viberbot.database.entity.Room;
import com.teamshort.viberbot.database.repository.RoomRepository;
import com.teamshort.viberbot.service.room.RoomService;
import com.teamshort.viberbot.service.room.RoomServiceImpl;

@Controller
public class RoomController {
	
	@Autowired
	 private RoomService roomService;

	    @RequestMapping(value = "/room/rooms", method = RequestMethod.GET)
	    public String list(Model model){
	        model.addAttribute("rooms", roomService.listAllRooms());
	        return "room/rooms";
	    }

	    @RequestMapping("room/{id}")
	    public String showRoom(@PathVariable Long id, Model model){
	        model.addAttribute("room", roomService.getRoomById(id));
	        return "room/roomshow";
	    }

	    @RequestMapping("room/edit/{id}")
	    public String edit(@PathVariable Long id, Model model){
	        model.addAttribute("room", roomService.getRoomById(id));
	        return "room/roomform";
	    }

	    @RequestMapping("room/new")
	    public String newRoom(Model model){
	        model.addAttribute("room", new Room());
	        return "room/roomform";
	    }

	    @RequestMapping(value = "room", method = RequestMethod.POST)
	    public String saveRoom(Room room){
	        roomService.saveRoom(room);
	        return "redirect:/room/rooms";
	    }

	    @RequestMapping("room/delete/{id}")
	    public String delete(@PathVariable Long id){
	        roomService.deleteRoom(id);
	        return "redirect:/room/rooms";
	    }

	
}
