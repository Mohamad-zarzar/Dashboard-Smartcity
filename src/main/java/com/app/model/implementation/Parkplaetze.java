package com.app.model.implementation;

import com.app.model.interfaces.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

public class Parkplaetze extends RecursiveTreeObject<Parkplaetze> implements ParkplaetzeImpl {

	private int id;
	private String value;
	private String time;
	private Sensor sensor;
	
	
	// TEST- Konstructoren
	public Parkplaetze(int id,String value,String time){
		this.id = id;
		this.value = value;
		this.time = time;
	}
	public Parkplaetze(int id,String value){
		this.id = id;
		this.value = value;
	}
	public Parkplaetze() {
	}

	// get-methods
	public int getId() {return id;}
	public String getValue() {return value;}
	public String getTime() {return time;}
	public Sensor getSensor() {return sensor;}
	
	//set-methods
	public void setId(int id) {
		this.id = id;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setSensor(Sensor sensor) {
		this.sensor=sensor;
	}
}
