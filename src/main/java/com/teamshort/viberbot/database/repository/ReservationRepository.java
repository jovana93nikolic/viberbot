package com.teamshort.viberbot.database.repository;

import org.springframework.data.repository.CrudRepository;

import com.teamshort.viberbot.database.entity.Reservation;

public interface ReservationRepository extends CrudRepository<Reservation, Long>{

}
