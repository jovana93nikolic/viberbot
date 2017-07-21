package com.teamshort.viberbot.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	/*
	id (type: java.util.Long)
	viberId (type: java.util.String)
	name (type: java.util.String)
	subscribe (type: boolean)*/
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;
	
	@Column(name = "viberId")
	private String viberId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "subscribe")
	private boolean subscribe;
	
	public User() {}
	
	
	
	
	public User(long id, String viberId, String name, boolean subscribe) {
		super();
		this.id = id;
		this.viberId = viberId;
		this.name = name;
		this.subscribe = subscribe;
	}




	public long getId() {
		return id;
	}




	public void setId(long id) {
		this.id = id;
	}




	public String getViberId() {
		return viberId;
	}




	public void setViberId(String viberId) {
		this.viberId = viberId;
	}




	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public boolean isSubscribe() {
		return subscribe;
	}




	public void setSubscribe(boolean subscribe) {
		this.subscribe = subscribe;
	}




	public String toString() {
		
		return "id: " + this.id + " viberId: " + this.viberId + " name: " + this.name + " subscribe: " + this.subscribe;
		
		
	}
	

}
