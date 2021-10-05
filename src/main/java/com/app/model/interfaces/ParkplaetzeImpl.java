package com.app.model.interfaces;

import com.app.model.implementation.Sensor;

public interface ParkplaetzeImpl {

	int getId();
	
	String getValue();
	
	String getTime();
	
	Sensor getSensor();

	void setId(int id);
	
	void setValue(String value);
	
	void setTime(String time);
	
	void setSensor(Sensor sensor);
}
