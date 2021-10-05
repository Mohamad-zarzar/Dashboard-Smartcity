package com.app.model.implementation;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.*;
import javafx.fxml.Initializable;

import com.app.model.interfaces.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import org.threeten.bp.OffsetDateTime;

public class Temperature extends RecursiveTreeObject<Temperature> implements Initializable,TemperaturImpl{
	
	
	private LongProperty id;
	private StringProperty time;
	private DoubleProperty value;
	

	public Temperature(long id, Double value, OffsetDateTime time) {

		long factor = (long) Math.pow(10, 1);
		value = value * factor;
		long tmp = Math.round(value);
		value = (double) tmp / factor;

		this.id = new SimpleLongProperty(id);
		this.value = new SimpleDoubleProperty(value);
		this.time = new SimpleStringProperty(time.toString());
	}
	// get methods
	public LongProperty getId() {
		return id;
	}
	public StringProperty getTime() {
		return time;
	}
	public DoubleProperty getValue() {
		return value;
	}

	// set methods
	public void setId(long id) {
		this.id = new SimpleLongProperty(id);
	}
	public void setTime(OffsetDateTime time) {
		this.time = new SimpleStringProperty(time.toString());
	}
	public void setValue(Double value) {
		this.value = new SimpleDoubleProperty(value);
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
}
