package com.teamshort.viberbot.mvc.controllers.room;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.teamshort.viberbot.database.model.Room;
import com.teamshort.viberbot.database.repository.RoomRepository;
import com.teamshort.viberbot.service.RoomService;
import com.teamshort.viberbot.service.RoomServiceImpl;

@Controller
public class RoomController {
	
	 private RoomService roomService;

	    @Autowired
	    public void setRoomService(RoomService roomService) {
	        this.roomService = roomService;
	    }

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
	        return "redirect:/room/" + room.getId();
	    }

	    @RequestMapping("room/delete/{id}")
	    public String delete(@PathVariable Long id){
	        roomService.deleteRoom(id);
	        return "redirect:/room/rooms";
	    }

	
}
