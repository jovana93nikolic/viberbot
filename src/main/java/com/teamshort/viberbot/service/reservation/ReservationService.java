package com.teamshort.viberbot.service.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

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
	Reservation edit (Reservation reservation);
	void delete(Long id);
	Iterable<LocalTime> getFreeRoomCapacitiesOnDate(Long roomId, LocalDate date);
	Iterable<Reservation> getByUser(String viberId);
	
	
}
