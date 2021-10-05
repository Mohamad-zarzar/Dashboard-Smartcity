package com.app.model.implementation;

import com.app.model.interfaces.ParkplaetzeGruppeImpl;

public class ParkplaetzeGruppe implements ParkplaetzeGruppeImpl {

	private int id;
	private String name;
	private String information;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getInformation() {
		return information;
	}
	
	//Set
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setInformation(String information) {
		this.information = information;
	}

}
