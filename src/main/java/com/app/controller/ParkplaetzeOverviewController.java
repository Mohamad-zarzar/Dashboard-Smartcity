package com.app.controller;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.threeten.bp.OffsetDateTime;

import com.app.base.CssConstants;
import com.app.base.MainApp;
import com.app.base.OtherConstants;
import com.app.model.service.ParkplaetzeService;

import io.swagger.client.model.EntityModelParking;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * ParkplaetzeOverviewController
 * Controler KLasse für ParkplaetzeLayout
 */
public class ParkplaetzeOverviewController implements OverviewController, Initializable {

	@FXML
	private GridPane gridPane;

	@FXML
	private Label anzahlFreiLabel;

	@FXML
	private Label anzahlBelegtLabel;
	@FXML
	private TableView<Entry<Long, Entry<Integer, Integer>>> gruppenTabelle;

	@FXML
	private TableColumn<Map<Long, Entry<Integer, Integer>>, Long> gruppeIdCol;

	@FXML
	private TableColumn<Map<Long, Entry<Integer, Integer>>, Integer> besetztCol;

	@FXML
	private TableColumn<Map<Long, Entry<Integer, Integer>>, Integer> freiCol;

	@FXML
	private TableView<EntityModelParking> parkplaetzeTabelle;

	@FXML
	private TableColumn<EntityModelParking, Long> idCol;

	@FXML
	private TableColumn<EntityModelParking, Boolean> statusCol;

	@FXML
	private TableColumn<EntityModelParking, OffsetDateTime> zeitCol;

	
	
	
	
	/**
	 * initialisierungsmethode des Layouts und der Tabellen
	 * 
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gridPane.getStylesheets().add(getClass().getResource(CssConstants.PARKING_CSS_PATH).toExternalForm());

		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				javafx.application.Platform.runLater(() -> {
					initOccupiedFreeLabels();
					initGroupsTable();
					initParkingTable();

				});
			}
		}, 0, OtherConstants.EINE_MINUTE_IN_MS);

	}

	/**
	 * Belegt, Frei Labels initialisieren.
	 * 
	 */
	public void initOccupiedFreeLabels() {

		ParkplaetzeService parkplaetzeService = new ParkplaetzeService();

		int frei = parkplaetzeService.getParkFrei();
		int belegt = parkplaetzeService.getParkBelegt();

		anzahlFreiLabel.setText(String.valueOf(frei));
		anzahlBelegtLabel.setText(String.valueOf(belegt));
	}

	/**
	 * Gruppentabelle initialisieren
	 */
	public void initGroupsTable() {

		Map<Long, Entry<Integer, Integer>> belegungProGruppe = new ParkplaetzeService().getBelegungProGruppe();
		
		// Convert Map to List, so that we can add it to the tableView
		ObservableList<Entry<Long, Entry<Integer, Integer>>> items = FXCollections
				.observableArrayList(belegungProGruppe.entrySet());
		// Add the converted List to the tableView
		gruppenTabelle.setItems(items);

		gruppeIdCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Map<Long, Entry<Integer, Integer>>, Long>, ObservableValue<Long>>() {

					@SuppressWarnings("unchecked")
					@Override
					public ObservableValue<Long> call(
							TableColumn.CellDataFeatures<Map<Long, Entry<Integer, Integer>>, Long> p) {

						Entry<Long, Entry<Integer, Integer>> entry = (Entry<Long, Entry<Integer, Integer>>) p.getValue();
						Long groupId = entry.getKey();

						return new SimpleLongProperty(groupId).asObject();
					}
				});

		freiCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Map<Long, Entry<Integer, Integer>>, Integer>, ObservableValue<Integer>>() {

					@SuppressWarnings("unchecked")
					@Override
					public ObservableValue<Integer> call(
							TableColumn.CellDataFeatures<Map<Long, Entry<Integer, Integer>>, Integer> p) {

						Entry<Long, Entry<Integer, Integer>> entry = (Entry<Long, Entry<Integer, Integer>>) p.getValue();
						Integer belegt = entry.getValue().getKey();

						return new SimpleIntegerProperty(belegt).asObject();
					}
				});

		besetztCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Map<Long, Entry<Integer, Integer>>, Integer>, ObservableValue<Integer>>() {

					@SuppressWarnings("unchecked")
					@Override
					public ObservableValue<Integer> call(
							TableColumn.CellDataFeatures<Map<Long, Entry<Integer, Integer>>, Integer> p) {
						Entry<Long, Entry<Integer, Integer>> entry = (Entry<Long, Entry<Integer, Integer>>) p.getValue();
						Integer belegt = entry.getValue().getValue();

						return new SimpleIntegerProperty(belegt).asObject();
					}
				});

	

	}

	/**
	 * Parkplatztabelle initialisieren
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initParkingTable() {

		List<EntityModelParking> parkingList = new ParkplaetzeService().getParkingList();
		parkplaetzeTabelle.setItems(FXCollections.observableList(parkingList));

	//idCol.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getId()).asObject() );
		
		idCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<EntityModelParking, Long>, ObservableValue<Long>>() {
					@Override
					public ObservableValue<Long> call(CellDataFeatures<EntityModelParking, Long> param) {

						EntityModelParking entityModelParking = param.getValue();

						if (entityModelParking != null) {
							Long id = entityModelParking.getId();
							return new SimpleLongProperty(id).asObject();
						}
						return null;
					}
				});

		statusCol.setCellValueFactory(c -> {
			EntityModelParking entityModelParking = c.getValue();
			CheckBox checkBox = new CheckBox();

			// To make checkbox readonly
			checkBox.setDisable(true);
			checkBox.setStyle("-fx-opacity: 1");

			checkBox.selectedProperty().setValue(!entityModelParking.isValue());

			return new SimpleObjectProperty(checkBox);
		});

		statusCol.setStyle("-fx-alignment: CENTER;");

		zeitCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<EntityModelParking, OffsetDateTime>, ObservableValue<OffsetDateTime>>() {
					@Override
					public ObservableValue<OffsetDateTime> call(
							CellDataFeatures<EntityModelParking, OffsetDateTime> param) {

						EntityModelParking entityModelParking = param.getValue();

						if (entityModelParking != null) {
							OffsetDateTime parkzeit = entityModelParking.getTime();
							return new SimpleObjectProperty<OffsetDateTime>(parkzeit);
						}
						return null;
					}
				});

	}

	@Override
	public void setMainApp(MainApp app) {

	}

}
