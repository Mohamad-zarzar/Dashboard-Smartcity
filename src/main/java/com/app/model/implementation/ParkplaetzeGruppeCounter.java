package com.app.model.implementation;

import com.app.model.interfaces.ParkplaetzeGruppeCounterImpl;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

public class ParkplaetzeGruppeCounter extends RecursiveTreeObject<ParkplaetzeGruppeCounter> implements ParkplaetzeGruppeCounterImpl {

	private int id;
	private int besitzt;
	private int frei;
	private String time;
	private ParkplaetzeGruppe parkplaetzeGruppe;
	
	//zum TESTEN
	public ParkplaetzeGruppeCounter(int id,int besitzt, int frei) {
		this.id = id;
		this.besitzt = besitzt;
		this.frei = frei;
	}
	
	public int getId() {
		return id;
	}
	public String getTime() {
		return time;
	}
	public int getBesitzt() {
		return besitzt;
	}
	public int getFrei() {
		return frei;
	}
	public ParkplaetzeGruppe getParkplaetzeGruppe() {
		return parkplaetzeGruppe;
	}
	
	public void setId(int id) {
		this.id = id;		
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setBesitzt(int besitzt) {
		this.besitzt = besitzt;		
	}
	public void setFrei(int frei) {
		this.frei = frei;		
	}
	public void setParkplaetzeGruppe(ParkplaetzeGruppe parkplaetzeGruppe) {
		this.parkplaetzeGruppe =parkplaetzeGruppe;
	}

}
