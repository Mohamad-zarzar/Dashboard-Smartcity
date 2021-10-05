package com.app.model.service;

import com.app.base.OtherConstants;
import io.swagger.client.ApiException;
import io.swagger.client.api.ParkingMeasuresApi;
import io.swagger.client.api.SensorManagementApi;
import io.swagger.client.model.*;

import java.util.*;

/**
 * ParkplaetzeService greift über API-Schnittstelle die Parkplaetzedaten ab
 * bietet vorverarbeitete Methoden an
 */
public class ParkplaetzeService {

	public Integer parkFrei;
	public Integer parkBelegt;
	private List<EntityModelParking> parkingList;
	private Map<Long, Map.Entry<Integer, Integer>> belegungProGruppe;

	public ParkplaetzeService() {
		getTempSensoren();

		parkingList = new ArrayList<>();
		belegungProGruppe = new HashMap<>();
	}

	/**
	 * getParkFrei
	 * 
	 * @return Anzahl freier Parkplaetze
	 */
	public Integer getParkFrei() {
		belegungParkplaetze();
		return this.parkFrei;
	}

	/**
	 * getParkBelegt
	 * 
	 * @return Anzahl belegter Parkplaetze
	 */
	public Integer getParkBelegt() {
		belegungParkplaetze();
		belegungparkplaetzeProGruppe();
		return this.parkBelegt;
	}

	/**
	 * getBelegungProGruppe
	 * 
	 * @return gruppenId, Anzahl freier Parkplaetze innerhalb dieser Gruppe, Anzahl
	 *         belegter Parkplaetze innerhalb dieser Gruppe
	 */
	public Map<Long, Map.Entry<Integer, Integer>> getBelegungProGruppe() {
		belegungparkplaetzeProGruppe();
		return belegungProGruppe;
	}

	/**
	 * getParkingList
	 * 
	 * @return Liste aller Parkplaetze
	 */
	public List<EntityModelParking> getParkingList() {
		parkingList = belegungParkplaetze();
		return parkingList;
	}

	/**
	 * belegungParkplaetze
	 * 
	 * Anzahl freier und belegter Parkplaetze berechnen und aktualisieren
	 * 
	 * @return Liste aller Parkplaetze
	 */
	private List<EntityModelParking> belegungParkplaetze() {
		Integer frei = 0;
		Integer belegt = 0;
		List<EntityModelParking> myParkingList = new ArrayList<>();

		try {
			ParkingMeasuresApi parkingMeasuresApi = new ParkingMeasuresApi();
			PagedModelParkingGroup pagedModelParkingGroup = parkingMeasuresApi.allGroups(null, null, null);
			PagedModelParkingGroupEmbedded parkingGroupEmbedded = pagedModelParkingGroup.getEmbedded();

			for (ParkingGroup group : parkingGroupEmbedded.getParkingGroups()) {
				myParkingList.addAll(parkingMeasuresApi.groupMeasuresLatest(group.getId(), null, null, null));
			}

			for (EntityModelParking p : myParkingList) {
				if (!p.isValue()) {
					frei++;
				} else {
					belegt++;
				}
			}
		} catch (ApiException apiException) {
			System.out.println(apiException);
		}

		parkFrei = frei;
		parkBelegt = belegt;
		
		return myParkingList;
	}

	/**
	 * Map<Long, Map.Entry<Integer, Integer>> Long = GruppenId Map.Entry key = frei,
	 * value = belegt
	 * 
	 * @return
	 */
	private Map<Long, Map.Entry<Integer, Integer>> belegungparkplaetzeProGruppe() {

		try {
			ParkingMeasuresApi parkingMeasuresApi = new ParkingMeasuresApi();
			PagedModelParkingGroup pagedModelParkingGroup = parkingMeasuresApi.allGroups(null, null, null);
			PagedModelParkingGroupEmbedded parkingGroupEmbedded = pagedModelParkingGroup.getEmbedded();

			for (ParkingGroup group : parkingGroupEmbedded.getParkingGroups()) {
				List<EntityModelParking> parkingList = new ArrayList<>();
				Integer frei = 0;
				Integer belegt = 0;
				parkingList.addAll(parkingMeasuresApi.groupMeasuresLatest(group.getId(), null, null, null));

				for (EntityModelParking p : parkingList) {
					if (!p.isValue()) {
						frei++;
					} else {
						belegt++;
					}
				}

				belegungProGruppe.put(group.getId(), new AbstractMap.SimpleEntry(frei, belegt));
			}

		} catch (ApiException apiException) {
			apiException.getStackTrace();
		}

		return belegungProGruppe;

	}

	private PagedModelSensor getTempSensoren() {
		SensorManagementApi sensorManagementApi = new SensorManagementApi();
		PagedModelSensor pagedModelSensor = null;
		try {
			pagedModelSensor = sensorManagementApi.byType(OtherConstants.SENSOR_TYPE_PARKING, null, Integer.MAX_VALUE,
					null);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return pagedModelSensor;
	}
}
