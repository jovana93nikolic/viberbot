package com.teamshort.viberbot.database.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;



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
public class Reservation implements Serializable {
	
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@JoinColumn(name = "room_id")	
	@ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	private Room room;
	
	@JoinColumn(name = "user_id")	
	@ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	private User user;
	
	
	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "time")
	private LocalTime time;

	
	public Reservation() {}
	
	
	
	
	public Reservation(User user, Room room,  String date, String time) {
		super();
		this.user = user;
		this.room = room;
		this.date = LocalDate.parse(date);
		this.time = LocalTime.parse(time);
	}




	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
		
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
		return "Reservation [id=" + id + ", room=" + room.getName() + " , user="   + ", date=" + date + ", time=" + time
				+ "]";
	}
	
	
	
	

}
