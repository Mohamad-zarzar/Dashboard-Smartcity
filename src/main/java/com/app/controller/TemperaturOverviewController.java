package com.app.controller;

import java.util.*;
import java.util.function.Function;

import com.app.base.ErrorMessageConstants;
import com.app.base.MainApp;
import com.app.components.RoundedPane_Two_Values;
import com.app.model.implementation.Temperature;
import com.app.model.service.TemperatureService;
import com.jfoenix.controls.*;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.swagger.client.model.PageMetadata;
import io.swagger.client.model.PagedModelTemperature;
import io.swagger.client.model.Sensor;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Modality;
import org.threeten.bp.*;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * TemperaturOverviewController
 * Controler KLasse für TemperaturLayout
 */
public class TemperaturOverviewController implements OverviewController{

	private MainApp app;
	
	@FXML
	private Label heading;
	@FXML
	private LineChart<String, Number> tempLineChart;
	
	@FXML
	private JFXButton heuteBtn;
	@FXML
	private JFXButton wocheBtn;
	@FXML
	private JFXButton monatBtn;
	
	@FXML
    private RoundedPane_Two_Values heighestTemp;
	@FXML
	private RoundedPane_Two_Values avgTemp;

	@FXML
	private JFXDatePicker startDate;
	@FXML
	private JFXDatePicker endDate;

	@FXML
	private Label sensorID;
	@FXML
	private JFXButton loadMore;


	@FXML
    private JFXTreeTableView<Temperature> tempSensorListe;
	@FXML
	private JFXTreeTableColumn<Temperature, Double> data;
	@FXML
	private JFXTreeTableColumn<Temperature, String> datum;

	private Map<Sensor, PagedModelTemperature> temperatureMap;
	private ListIterator<Sensor> sensorListIterator;
	private PageMetadata currentPage;
	private Sensor currentSensor;
	private ObservableList<Temperature> temperatureObservableList;

	/**
	 * initialisierungs Methode des Layouts
	 * initialisiert Graphen
	 */
	@FXML
	public void initialize() {
			intGraph();
			initMapAndIterator();
			initListe();
			avgTemp.setText("Durchschnitt");
			heighestTemp.setText("Höchste");
	}

	/**
	 * tempJederSensor
	 * zeigt alle Temperatur Daten der Sensoren in einer Liste an
	 * Daten sind Paged und können nach und nach geladen werden
	 * immer nur Daten für einen Sensor
	 */
	private ObservableList<Temperature> appendNextPage(PagedModelTemperature temperature) {


		for (io.swagger.client.model.Temperature t: temperature.page(currentPage).getEmbedded().getTemperatures()) {
			Temperature temp = new Temperature(t.getId(), t.getValue(), t.getTime());
			temperatureObservableList.add(temp);
		}

		return temperatureObservableList;
	}

	/**
	 * initListe
	 * initialisiert Liste von Sensoren und lädt erste Page des ersten Sensors
	 */
	private void initListe()
	{
		setupCellValueFactory(data, t -> t.getValue().asObject());
		setupCellValueFactory(datum, Temperature::getTime);

		temperatureObservableList = FXCollections.observableArrayList();

		PagedModelTemperature temperature = temperatureMap.get(currentSensor);
		ObservableList<Temperature> werte = appendNextPage(temperature);

		tempSensorListe.setRoot(new RecursiveTreeItem<>(werte, RecursiveTreeObject::getChildren));
		tempSensorListe.setShowRoot(false);

	}

	private <T> void setupCellValueFactory(JFXTreeTableColumn<Temperature, T> column, Function<Temperature, ObservableValue<T>> mapper) {
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Temperature, T> param) -> {
			if (column.validateValue(param)) {
				return mapper.apply(param.getValue().getValue());
			} else {
				return column.getComputedValue(param);
			}
		});
	}

	/**
	 * initMapAndIterator
	 * initialisiert Map mit Sensordaten und ListIterator
	 */
	private void initMapAndIterator(){
		TemperatureService temperatureService = new TemperatureService();
		temperatureMap = temperatureService.getPagedTemperaturePerSensor();

		Iterator<Sensor> it = temperatureMap.keySet().iterator();
		List<Sensor> sensorList = new LinkedList<>();
		while (it.hasNext())
		{
			sensorList.add(it.next());
		}

		this.sensorListIterator = sensorList.listIterator();
		currentSensor = sensorListIterator.next();
		currentPage = temperatureMap.get(currentSensor).getPage();
		sensorID.setText("ID: " + currentSensor.getId());

	}

	/**
	 * tempLineChart
	 * @param temperaturen Map mit den darzustellenden Temperatur Werten und ihrem Datum
	 * nimmt darzustellende Werte und zeichnet diese in einem LineChart
	 */
    private void tempLineChartDraw(Map<OffsetDateTime, Double> temperaturen) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		Iterator<OffsetDateTime> it = temperaturen.keySet().iterator();

		while(it.hasNext())
		{
			OffsetDateTime key = it.next();
			String day = key.getDayOfMonth() + "." + (key.getMonth().ordinal() + 1);
			series.getData().add(new XYChart.Data<>(day, temperaturen.get(key)));
		}
		tempLineChart.getData().clear();
    	tempLineChart.getData().add(series);
    }

	/**
	 * Holt die Durchschnittswerte pro Tag die im Graphen angezigt werden.
	 * @return Map mit Datum und Wert
	 */
	private Map<OffsetDateTime, Double> getTempFuerGraph(OffsetDateTime start, OffsetDateTime  end)
	{
		TemperatureService temperatureService = new TemperatureService();
		Map<OffsetDateTime, Double> temperaturen = temperatureService.getTempZeitraumAvereageDay(start, end);
		long anzahlTage = ChronoUnit.DAYS.between(start, end);
		tempLineChart.setTitle(anzahlTage + " Tage");
		return temperaturen;
	}

	/**
	 * initialisierung des Graphen
	 * standard letzten 30 Tage
	 */
	private void intGraph()
	{
		tempLineChart.setAnimated(false);
		tempLineChart.setCreateSymbols(false);
		setGraph();
	}

	/**
	 *
	 * @throws NullPointerException
	 */
	private void setGraph() throws NullPointerException
	{
		OffsetDateTime start;
		OffsetDateTime end;
		TemperatureService temperatureService = new TemperatureService();

		if(startDate.getValue() == null || endDate.getValue() == null)
		{
			end = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
			start = end.minusDays(30);

			tempLineChartDraw(getTempFuerGraph(start, end));
			avgTemp.setValue(temperatureService.getTempAverageMitAnzahlTagen(30));
			heighestTemp.setValue(temperatureService.getHoechsteTemp30().getValue());


			return;
		}

		ZoneOffset zoneOffset = ZoneId.of("Z").getRules().getOffset(Instant.now());

		start = OffsetDateTime.of(startDate.getValue().getYear(), startDate.getValue().getMonth().getValue(), startDate.getValue().getDayOfMonth(),0,0, 0, 0, zoneOffset);
		end = OffsetDateTime.of(endDate.getValue().getYear(), endDate.getValue().getMonth().getValue(), endDate.getValue().getDayOfMonth(),0,0, 0, 0, zoneOffset);

		tempLineChartDraw(getTempFuerGraph(start, end));
		avgTemp.setValue(temperatureService.getAirAverageMitZeitraum(start, end));
		heighestTemp.setValue(temperatureService.getHoechsteTempZeitraum(start, end).getValue());
	}

	@FXML
	private void startTemp()
	{
		if(startDate.getValue() != null || endDate.getValue() != null) {
			try {
				setGraph();
			}
			catch (NullPointerException nullPointerException)
			{
				app.error(ErrorMessageConstants.NO_DATA_HEADER, ErrorMessageConstants.NO_DATA_BODY);
			}
		}
	}

	/**
	 * wird aufgerufen wenn Button "Heute" gedrückt wurde
	 * ruft die Temperaturwerte des jetzigen Tages pro Stunde ab und stellt diese in dem Graphe dar
	 * zeigt aktuelle und aktull hoechste Temperatur an
	 */
	@FXML
	private void heuteClickedTemp()
	{
		try {
			TemperatureService temperatureService = new TemperatureService();
			Map<OffsetDateTime, Double> temperaturen = temperatureService.getTempHourly();
			avgTemp.setValue(temperatureService.getTempAktuell());
			heighestTemp.setValue(temperatureService.getHoesteAktuell());

			XYChart.Series<String, Number> series = new XYChart.Series<>();
			Iterator<OffsetDateTime> it = temperaturen.keySet().iterator();

			while(it.hasNext())
			{
				OffsetDateTime key = it.next();
				String day = key.getHour() + "";
				series.getData().add(new XYChart.Data<>(day, temperaturen.get(key)));
			}

			tempLineChart.setTitle("Heute");
			tempLineChart.getData().clear();
			tempLineChart.getData().add(series);
		}
		catch (NullPointerException exception)
		{
			app.error(ErrorMessageConstants.NO_DATA_HEADER, ErrorMessageConstants.NO_DATA_BODY);
		}

	}

	/**
	 * wird aufgerufen wenn Button "Woche" gedrückt wurde
	 * ruft die durchschnittlichen Temperaturwerte der letzten sieben Tages ab und stellt diese in dem Graphe dar
	 * ermittelt gesamten Durchschnitt und hoechste Temperatur für die letzten 7 Tage
	 */
	@FXML
	private void wocheClickedTemp()
	{
		try {
			TemperatureService temperatureService = new TemperatureService();
			Map<OffsetDateTime, Double> temperaturen = temperatureService.getTempZeitraumAverageMitAnzahlTagen(7);

			avgTemp.setValue(temperatureService.getTempAverageMitAnzahlTagen(7));
			heighestTemp.setValue(temperatureService.geHoechstetTempZeitraumMitAnzahlTagen(7).getValue());

			tempLineChart.setTitle("letzten" + 7 + " Tage");
			tempLineChartDraw(temperaturen);
		}
		catch (NullPointerException nullPointerException)
		{
			app.error(ErrorMessageConstants.NO_DATA_HEADER, ErrorMessageConstants.NO_DATA_BODY);
		}
	}

	/**
	 * wird aufgerufen wenn Button "Monat" gedrückt wurde
	 * ruft die durchschnittlichen Temperaturwerte der letzten dreißig Tages ab und stellt diese in dem Graphe dar
	 * ermittelt gesamten Durchschnitt und hoechste Temperatur für die letzten 30 Tage
	 */
	@FXML
	private void monatClickedtemp()
	{
		TemperatureService temperatureService = new TemperatureService();
		Map<OffsetDateTime, Double> temperaturen = temperatureService.getTempZeitraumAverageMitAnzahlTagen(30);

		avgTemp.setValue(temperatureService.getTempAverageMitAnzahlTagen(30));
		heighestTemp.setValue(temperatureService.geHoechstetTempZeitraumMitAnzahlTagen(30).getValue());
		tempLineChart.setTitle("letzten" + 30 + " Tage");
		tempLineChartDraw(temperaturen);
	}

	@FXML
	private void backArrow()
	{
		if(sensorListIterator.hasPrevious())
		{
			currentSensor = sensorListIterator.previous();
			PagedModelTemperature temperature = temperatureMap.get(currentSensor);
			sensorID.setText("ID: " + currentSensor.getId());
			currentPage = temperature.getPage().number(0l);
			temperatureObservableList.clear();
			appendNextPage(temperature);
		}
	}

	@FXML
	private void forwardArrow()
	{
		if(sensorListIterator.hasNext())
		{
			currentSensor = sensorListIterator.next();
			PagedModelTemperature temperature = temperatureMap.get(currentSensor);
			sensorID.setText("ID: " + currentSensor.getId());
			currentPage = temperature.getPage().number(0l);
			temperatureObservableList.clear();
			appendNextPage(temperature);

		}
	}

	@FXML
	private void loadMore()
	{
		PagedModelTemperature temperature = temperatureMap.get(currentSensor);
		long totalPages = temperature.getPage().getTotalPages();
		if(currentPage.getNumber()+1 != totalPages)
		{
			currentPage = temperature.getPage().number(currentPage.getNumber()+1);
			appendNextPage(temperature);
		}
	}

	@Override
	public void setMainApp(MainApp app) {
		this.app = app;
	}
}
