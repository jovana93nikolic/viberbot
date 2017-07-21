package com.teamshort.viberbot.database.entity;

public class Users {
	/*
	id (type: java.util.Long)
	viberId (type: java.util.String)
	name (type: java.util.String)
	subscribe (type: boolean)*/
	
	private long id;
	private String viberId;
	private String name;
	private boolean subscribe;
	
	public Users() {}
	
	public String toString() {
		
		return "id: " + this.id + " viberId: " + this.viberId + " name: " + this.name + " subscribe: " + this.subscribe;
		
		
	}
	

}
