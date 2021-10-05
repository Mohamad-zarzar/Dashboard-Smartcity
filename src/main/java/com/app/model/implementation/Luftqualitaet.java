package com.app.model.implementation;

import com.app.model.interfaces.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import org.threeten.bp.OffsetDateTime;

public class Luftqualitaet extends RecursiveTreeObject<Luftqualitaet> implements LuftqualaetitImpl {

	
	
	private LongProperty id;
	private IntegerProperty value;
	private StringProperty time;

	public Luftqualitaet(long id, Integer value, OffsetDateTime time) {

		this.id = new SimpleLongProperty(id);
		this.value = new SimpleIntegerProperty(value);
		this.time = new SimpleStringProperty(time.toString());
	}

	// get-methods
	public LongProperty getId() {return id;}
	public IntegerProperty getValue() {return value;}
	public StringProperty getTime() {return time;}

	
	//set-methods
	public void setId(long id) {
		this.id = new SimpleLongProperty(id);
	}
	public void setValue(int value) {
		this.value = new SimpleIntegerProperty(value);
	}
	public void setTime(String time) {
		this.time = new SimpleStringProperty(time.toString());
	}
}
