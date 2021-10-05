package com.app.model.implementation;

import com.app.model.interfaces.*;

public class Sensor implements SensorImpl {

	
	private int id;
	private String name;
	private String sensorType;
	private double x;
	private double y;
	private String information;
	
	// get methods
	public int getId() {return id;}
	public String getName() {return name;}
	public String getSensorType() {return sensorType;}
	public double getX() {return x;}
	public double getY() {return y;}
	public String getInformation() {return information;}
	
	// set methods
	public void setId(int id) {
		this.id=id;
	}
	public void setName(String name) {
		this.name=name;
	}
	public void setSensorType(String sensorType) {
		this.sensorType=sensorType;
	}
	public void setX(double x) {
		this.x=x;
	}
	public void setY(double y) {
		this.y=y;
	}
	public void setInformation(String information) {
		this.information=information;
	}
}
