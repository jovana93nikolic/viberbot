package com.teamshort.viberbot.service.reservation;

import java.time.LocalDate;

import com.teamshort.viberbot.database.entity.Reservation;

public interface ReservationService {
	/*getAll()
	reserve(Reservation reservation)
	edit(Reservation reservation)
	delete(Long id)
	getFreeRoomCapacitiesOnDate(Long roomId, LocalDate date)
	getByUser(String viberId)*/
	
	
	
	Iterable<Reservation> getAll();
	Reservation reserve(Reservation reservation);
	void edit (Reservation reservation);
	void delete(Long id);
	String getFreeRoomCapacitiesOnDate(Long roomId, LocalDate date);
	String getByUser(String viberId);
	
	
}
