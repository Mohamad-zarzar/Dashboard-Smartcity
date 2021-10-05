package com.app.model.interfaces;

import com.app.model.implementation.Sensor;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;

public interface LuftqualaetitImpl {

	LongProperty getId();
	
	IntegerProperty getValue();
	
	StringProperty getTime();

	void setId(long id);
	
	void setValue(int value);
	
	void setTime(String time);
}
