package com.teamshort.viberbot.database.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.teamshort.viberbot.database.model.Room;



/*
 * 
 * Create Reservation entity with properties:

id (type: java.util.Long)
user (type: User)
room (type: Room)
date (type: java.time.LocalDate)
time(type: java.time.LocalTime)

 * */

@Entity
@Table(name = "reservations")
public class Reservation {
	
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;
	
	@Column(name = "room")
	private Room room;
	
	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "time")
	private LocalTime time;

	
	public Reservation() {}
	
	
	
	
	public Reservation(long id, Room room, String date, String time) {
		super();
		this.id = id;
		//this.user = user;
		this.room = room;
		this.date = LocalDate.parse(date);
		this.time = LocalTime.parse(time);
	}




	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

/*	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}*/

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}




	@Override
	public String toString() {
		return "Reservation [id=" + id + ", room=" + room + ", date=" + date + ", time=" + time
				+ "]";
	}
	
	
	
	

}
