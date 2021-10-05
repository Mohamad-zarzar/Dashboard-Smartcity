package com.app.model.interfaces;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import org.threeten.bp.OffsetDateTime;

public interface TemperaturImpl {

	
	LongProperty getId();
	
	DoubleProperty getValue();
	
	StringProperty getTime();
	
	void setId(long id);
	
	void setTime(OffsetDateTime time);
	
	void setValue(Double value);
}
