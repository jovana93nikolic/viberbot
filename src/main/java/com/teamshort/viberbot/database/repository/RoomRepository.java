package com.teamshort.viberbot.database.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.teamshort.viberbot.database.entity.Room;


public interface RoomRepository extends CrudRepository<Room, Long> {
	List<Room> findByName (String name);

}
