package com.app.model.interfaces;

public interface SensorImpl {

	int getId();
	
	String getName();
	
	String getSensorType();
	
	double getX();
	
	double getY();
	
	String getInformation();
	
	void setId(int id);
	
	void setName(String name);
	
	void setSensorType(String sensorType);
	
	void setX(double x);
	
	void setY(double y);
	
	void setInformation(String information);
}
