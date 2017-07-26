package com.teamshort.viberbot.service.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamshort.viberbot.database.entity.Reservation;
import com.teamshort.viberbot.database.repository.ReservationRepository;

@Service
public class ReservationServiceImpl implements ReservationService {

	@Autowired
	ReservationRepository reservationRepository;
	
	@Override
	public Iterable<Reservation> getAll() {
		return reservationRepository.findAll();
	}

	@Override
	public Reservation reserve(Reservation reservation) {
		return reservationRepository.save(reservation);
	}

	@Override
	public Reservation edit(Reservation reservation) {
		return reservationRepository.save(reservation);
	
	}

	@Override
	public void delete(Long id) {
		reservationRepository.delete(id);
	}

	@Override
	public Iterable<LocalTime> getFreeRoomCapacitiesOnDate(Long roomId, LocalDate date) {
		return reservationRepository.getFreeRoomCapacitiesOnDate(roomId, date);
	}

	@Override
	public Reservation getByUser(String viberId) {
		//return reservationRepository.getByUser(viberId);
		return null;
	}
	
	
	

}
