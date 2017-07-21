package com.teamshort.viberbot.database.model;

import java.io.Serializable;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.Version;

@Entity
@Table(name = "rooms")
public class Room implements Serializable {
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;
	
	@Column(name = "number")
	private String number;
 
	@Column(name = "name")
	private String name;
	
	
	/*
	 * 
	 * 	startWorkTime (type: java.time.LocalTime)
		endWorkTime (type: java.time.LocalTime)
	 * */
	
	@Column(name = "startWorkTime")
	private LocalTime startWorkTime;
 
	@Column(name = "endWorkTime")
	private LocalTime endWorkTime;

	@Version
	private Integer version;

	
	public Room() {}

	public Room(String number, String name) {
		super();
		this.number = number;
		this.name = name;
	}
	
	public Room(String number, String name, LocalTime startWork, LocalTime endWork) {
		super();
		this.number = number;
		this.name = name;
		this.startWorkTime = startWork;
		this.endWorkTime = endWork;
	}
	
	
	
	@Override
	public String toString() {
		
		return "Id: " + this.id + " number: " + this.number + " name: " + this.name + 
				" Starting work: " + this.startWorkTime + " Ending work: " + this.endWorkTime;
	}

//	public String getId() {
//		String Id = String.valueOf(this.id);
//		
//		return Id;
//	}
	
	

	public String getNumber() {
		return number;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Long getId() {
		return id;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalTime getStartWorkTime() {
		return startWorkTime;
	}

	public void setStartWorkTime(LocalTime startWorkTime) {
		this.startWorkTime = startWorkTime;
	}

	public LocalTime getEndWorkTime() {
		return endWorkTime;
	}

	public void setEndWorkTime(LocalTime endWorkTime) {
		this.endWorkTime = endWorkTime;
	}
	
	
	

}


