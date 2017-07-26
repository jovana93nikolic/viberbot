package com.teamshort.viberbot.service.room;


import com.teamshort.viberbot.database.entity.Room;



public interface RoomService {

	
	/*
	 * 
	 * 
	 * RoomService.java should be interface inside service package.
	RoomServiceImpl.java is RoomService implementation class (inside service package).
Service has 5 functions:

findAll()	- listAllRooms()
add(Room room) - saveRoom()
update(Room room)
getOne(Long id)
delete(Long id)
These functions should be used for Room entity maninupulation inside app
*Update function for list view inside controller developed in RB-006 to use findAll() function from service
	 * 
	 * */
	
	Iterable<Room> listAllRooms();

    Room getRoomById(Long id);

    Room saveRoom(Room room);

    void deleteRoom(Long id);
	
    public Room getRoomById(String id);
	
	
}
