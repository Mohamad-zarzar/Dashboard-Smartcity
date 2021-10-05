package com.app.model.interfaces;

import com.app.model.implementation.ParkplaetzeGruppe;
import com.app.model.implementation.ParkplaetzeGruppeCounter;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

public interface ParkplaetzeGruppeCounterImpl {

	int getId();
	
	String getTime();
	
	int getBesitzt();
	
	int getFrei();
	
	ParkplaetzeGruppe getParkplaetzeGruppe();
	
	void setId(int id);
	
	void setTime(String time);
	
	void setBesitzt(int besitzt);
	
	void setFrei(int frei);
	
	void setParkplaetzeGruppe(ParkplaetzeGruppe parkplaetzeGruppe);
}
