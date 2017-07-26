package com.teamshort.viberbot.service.room;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamshort.viberbot.database.entity.Room;
import com.teamshort.viberbot.database.repository.RoomRepository;

@Service
public class RoomServiceImpl implements RoomService {
	
/*	@Autowired
	RoomRepository repository;
	

	@Override
	public List<Room> findAll() {
		// TODO Auto-generated method stub
		
		List<Room> roomsList = new ArrayList<Room>();
		
		for(Room room : repository.findAll()){
			roomsList.add(room);
			System.out.println("Adding" + room.toString() + "to Service");
		}
		
		return roomsList;
	}
*/
	
	
	 private RoomRepository roomRepository;

	    @Autowired
	    public void setProductRepository(RoomRepository roomRepository) {
	        this.roomRepository = roomRepository;
	    }

	    @Override
	    public Iterable<Room> listAllRooms() {
	        return roomRepository.findAll();
	    }

	    @Override
	    public Room getRoomById(Long id) {
	        return roomRepository.findOne(id);
	    }
	    

	    @Override
	    public Room getRoomById(String id) {
	        return roomRepository.findOne(Long.parseLong(id));
	    }
	    
	    

	    @Override
	    public Room saveRoom(Room room) {
	        return roomRepository.save(room);
	    }

	    @Override
	    public void deleteRoom(Long id) {
	    	roomRepository.delete(id);
	    }
}
