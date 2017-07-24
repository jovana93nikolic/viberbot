package com.teamshort.viberbot.service;

import java.time.LocalDate;

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
		//to do
		return null;
	}

	@Override
	public void delete(Long id) {
		reservationRepository.delete(id);
	}

	@Override
	public String getFreeRoomCapacitiesOnDate(Long roomId, LocalDate date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getByUser(String viberId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
