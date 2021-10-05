package com.app.controller;

import java.util.*;
import java.util.function.Function;

import com.app.base.ErrorMessageConstants;
import com.app.base.MainApp;
import com.app.base.OtherConstants;
import com.app.components.RoundedPane_Two_Values;
import com.app.model.implementation.Luftqualitaet;
import com.app.model.service.LuftqualitaetService;
import com.jfoenix.controls.*;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.swagger.client.model.*;
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
 * LuftqualitaetOverviewController
 * Controler KLasse für LuftqualitätLayiut
 */
public class LuftqualitaetOverviewController implements OverviewController{

    private MainApp app;

    @FXML
    private Label heading;
    @FXML
    private LineChart<String, Number> airLineChart;

    @FXML
    private JFXButton heuteBtn;
    @FXML
    private JFXButton wocheBtn;
    @FXML
    private JFXButton monatBtn;

    @FXML
    private RoundedPane_Two_Values heighestAir;
    @FXML
    private RoundedPane_Two_Values avgAir;

    @FXML
    private JFXDatePicker startDate;
    @FXML
    private JFXDatePicker endDate;

    @FXML
    private Label sensorID;
    @FXML
    private JFXButton loadMore;

    @FXML
    private JFXTreeTableView airSensorListe;
    @FXML
    private JFXTreeTableColumn<Luftqualitaet, Integer> data;
    @FXML
    private JFXTreeTableColumn<Luftqualitaet, String> datum;

    private Map<Sensor, PagedModelAirQuality> airQualityMap;
    private ListIterator<Sensor> sensorListIterator;
    private PageMetadata currentPage;
    private Sensor currentSensor;
    private ObservableList<Luftqualitaet> luftqualitaetObservableList;

    /**
     * initialisierungs Methode des Layouts
     * initialisiert Graphen
     */
    @FXML
    public void initialize() {
        intGraph();
        initMapAndIterator();
        initListe();
        avgAir.setText("Durchschnitt");
        heighestAir.setText("Höchste");
    }

    /**
     * airJederSensor
     * zeigt alle AirQuality Daten der Sensoren in einer Liste an
     * Daten sind Paged und können nach und nach geladen werden
     * immer nur Daten für einen Sensor
     */
    private ObservableList<Luftqualitaet> appendNextPage(PagedModelAirQuality airQuality) {

        for (AirQuality quality: airQuality.page(currentPage).getEmbedded().getAirQualities()) {
            Luftqualitaet air = new Luftqualitaet(quality.getId(), quality.getValue(), quality.getTime());
            luftqualitaetObservableList.add(air);
        }

        return luftqualitaetObservableList;
    }

    /**
     * initListe
     * initialisiert Liste von Sensoren und lädt erste Page des ersten Sensors
     */
    private void initListe()
    {
        setupCellValueFactory(data, a -> a.getValue().asObject());
        setupCellValueFactory(datum, Luftqualitaet::getTime);

        luftqualitaetObservableList = FXCollections.observableArrayList();

        PagedModelAirQuality airQuality = airQualityMap.get(currentSensor);
        ObservableList<Luftqualitaet> werte = appendNextPage(airQuality);

        airSensorListe.setRoot(new RecursiveTreeItem<>(werte, RecursiveTreeObject::getChildren));
        airSensorListe.setShowRoot(false);

    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<Luftqualitaet, T> column, Function<Luftqualitaet, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Luftqualitaet, T> param) -> {
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
        LuftqualitaetService luftqualitaetService = new LuftqualitaetService();
        airQualityMap = luftqualitaetService.getPagedAirQualityPerSensor();

        Iterator<Sensor> it = airQualityMap.keySet().iterator();
        List<Sensor> sensorList = new LinkedList<>();
        while (it.hasNext())
        {
            sensorList.add(it.next());
        }

        this.sensorListIterator = sensorList.listIterator();
        currentSensor = sensorListIterator.next();
        currentPage = airQualityMap.get(currentSensor).getPage();
        sensorID.setText("ID: " + currentSensor.getId());
    }

    /**
     * airLineChart
     * @param airQualities Map mit den darzustellenden AirQuality Werten und ihrem Datum
     * nimmt darzustellende Werte und zeichnet diese in einem LineChart
     */
    private void airLineChartDraw(Map<OffsetDateTime, Double> airQualities) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Iterator<OffsetDateTime> it = airQualities.keySet().iterator();

        while(it.hasNext())
        {
            OffsetDateTime key = it.next();
            String day = key.getDayOfMonth() + "." + (key.getMonth().ordinal() + 1);
            series.getData().add(new XYChart.Data<>(day, airQualities.get(key)));
        }
        airLineChart.getData().clear();
        airLineChart.getData().add(series);
    }

    /**
     * Holt die Durchschnittswerte pro Tag die im Graphen angezigt werden.
     * @return Map mit Datum und Wert
     */
    private Map<OffsetDateTime, Double> getAirFuerGraph(OffsetDateTime start, OffsetDateTime  end)
    {
        LuftqualitaetService luftqualitaetService = new LuftqualitaetService();
        Map<OffsetDateTime, Double> airQualitlies = luftqualitaetService.getAirZeitraumAvereageDay(start, end);
        long anzahlTage = ChronoUnit.DAYS.between(start, end);
        airLineChart.setTitle(anzahlTage + " Tage");
        return airQualitlies;
    }

    /**
     * initialisierung des Graphen
     * standard letzten 30 Tage
     */
    private void intGraph()
    {
        airLineChart.setAnimated(false);
        airLineChart.setCreateSymbols(false);
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
        LuftqualitaetService luftqualitaetService = new LuftqualitaetService();

        if(startDate.getValue() == null || endDate.getValue() == null)
        {
            end = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
            start = end.minusDays(30);

            airLineChartDraw(getAirFuerGraph(start, end));
            setAvgAir(luftqualitaetService.getAirAverageMitAnzahlTagen(30));
            setHeighestAir(luftqualitaetService.geHoechstetAirZeitraumMitAnzahlTagen(30).getValue());


            return;
        }

        ZoneOffset zoneOffset = ZoneId.of("Z").getRules().getOffset(Instant.now());
        start = OffsetDateTime.of(startDate.getValue().getYear(), startDate.getValue().getMonth().getValue(), startDate.getValue().getDayOfMonth(),0,0, 0, 0, zoneOffset);
        end = OffsetDateTime.of(endDate.getValue().getYear(), endDate.getValue().getMonth().getValue(), endDate.getValue().getDayOfMonth(),0,0, 0, 0, zoneOffset);

        airLineChartDraw(getAirFuerGraph(start, end));
        setAvgAir(luftqualitaetService.getAirAverageMitZeitraum(start, end));
        setHeighestAir(luftqualitaetService.getHoechsteAirZeitraum(start, end).getValue());

    }

    @FXML
    private void startAir()
    {
        if(startDate.getValue() != null || endDate.getValue() != null) {
            try {
                setGraph();
            }
            catch (NullPointerException nullPointerException)
            {
                JFXAlert alert = new JFXAlert(app.getPrimaryStage());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setOverlayClose(false);
                JFXDialogLayout layout = new JFXDialogLayout();
                layout.setHeading(new Label("Keine Daten"));
                layout.setBody(new Label("Es gibt leider keine Daten für diesen Zeitraum!"));
                JFXButton closeButton =  new JFXButton("Ok");
                closeButton.getStyleClass().add("dialog-accept");
                closeButton.setOnAction(event -> alert.hideWithAnimation());
                layout.setActions(closeButton);
                alert.setContent(layout);
                alert.show();
            }
        }
    }

    /**
     * wird aufgerufen wenn Button "Heute" gedrückt wurde
     * ruft die Luftqualitätwerte des jetzigen Tages pro Stunde ab und stellt diese in dem Graphe dar
     * zeigt aktuelle und aktull hoechste AirQuality an
     */
    @FXML
    private void heuteClickedAir()
    {

        try {
            LuftqualitaetService luftqualitaetService = new LuftqualitaetService();
            Map<OffsetDateTime, Integer> airQualities = luftqualitaetService.getAirHourly();

            setAvgAir(luftqualitaetService.getAirAktuell());
            setHeighestAir(luftqualitaetService.getHoesteAktuell());

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            Iterator<OffsetDateTime> it = airQualities.keySet().iterator();

            while(it.hasNext())
            {
                OffsetDateTime key = it.next();
                String day = key.getHour() + "";
                series.getData().add(new XYChart.Data<>(day, airQualities.get(key)));
            }
            airLineChart.setTitle("Heute");
            airLineChart.getData().clear();
            airLineChart.getData().add(series);
        }
        catch (NullPointerException nullPointerException)
        {
            app.error(ErrorMessageConstants.NO_DATA_HEADER, ErrorMessageConstants.NO_DATA_BODY);
        }



    }

    /**
     * wird aufgerufen wenn Button "Woche" gedrückt wurde
     * ruft die durchschnittlichen Luftqualitätwerte der letzten sieben Tages ab und stellt diese in dem Graphe dar
     * ermittelt gesamten Durchschnitt und hoechste AirQuality für die letzten 7 Tage
     */
    @FXML
    private void wocheClickedAir()
    {

        try {
            LuftqualitaetService luftqualitaetService = new LuftqualitaetService();
            Map<OffsetDateTime, Double> airQualities = luftqualitaetService.getAirZeitraumAverageMitAnzahlTagen(7);

            setAvgAir(luftqualitaetService.getAirAverageMitAnzahlTagen(7));
            setHeighestAir(luftqualitaetService.geHoechstetAirZeitraumMitAnzahlTagen(7).getValue());

            airLineChart.setTitle("letzten" + 7 + " Tage");
            airLineChartDraw(airQualities);
        }
        catch (NullPointerException nullPointerException)
        {
            app.error(ErrorMessageConstants.NO_DATA_HEADER, ErrorMessageConstants.NO_DATA_BODY);
        }



    }

    /**
     * wird aufgerufen wenn Button "Monat" gedrückt wurde
     * ruft die durchschnittlichen Luftqualitätwerte der letzten dreißig Tages ab und stellt diese in dem Graphe dar
     * ermittelt gesamten Durchschnitt und hoechste AirQuality für die letzten 30 Tage
     */
    @FXML
    private void monatClickedAir()
    {

        try {
            LuftqualitaetService luftqualitaetService = new LuftqualitaetService();
            Map<OffsetDateTime, Double> airQualities = luftqualitaetService.getAirZeitraumAverageMitAnzahlTagen(30);

            setAvgAir(luftqualitaetService.getAirAverageMitAnzahlTagen(30));
            setHeighestAir(luftqualitaetService.geHoechstetAirZeitraumMitAnzahlTagen(30).getValue());
            airLineChart.setTitle("letzten" + 30 + " Tage");
            airLineChartDraw(airQualities);
        }
        catch (NullPointerException nullPointerException)
        {
            app.error(ErrorMessageConstants.NO_DATA_HEADER, ErrorMessageConstants.NO_DATA_BODY);
        }


    }

    @FXML
    private void backArrow()
    {
        if(sensorListIterator.hasPrevious())
        {
            Sensor sensor = sensorListIterator.previous();
            PagedModelAirQuality airQuality = airQualityMap.get(sensor);
            sensorID.setText("ID: " + sensor.getId());
            currentPage = airQuality.getPage().number(0l);
            luftqualitaetObservableList.clear();
            appendNextPage(airQuality);
        }
    }

    @FXML
    private void forwardArrow()
    {
        if(sensorListIterator.hasNext())
        {
            Sensor sensor = sensorListIterator.next();
            PagedModelAirQuality airQuality = airQualityMap.get(sensor);
            sensorID.setText("ID: " + sensor.getId());
            currentPage = airQuality.getPage().number(0l);
            luftqualitaetObservableList.clear();
            appendNextPage(airQuality);
        }
    }

    @FXML
    private void loadMore()
    {
        PagedModelAirQuality airQuality = airQualityMap.get(currentSensor);
        long totalPages = airQuality.getPage().getTotalPages();
        if(currentPage.getNumber()+1 != totalPages)
        {
            currentPage = airQuality.getPage().number(currentPage.getNumber()+1);
            appendNextPage(airQuality);
        }
    }

    private void setAvgAir(Double air)
    {
        LuftqualitaetService luftqualitaetService = new LuftqualitaetService();
        String rating = luftqualitaetService.getAirQualityRating(air);
        this.avgAir.setValueColor(rating);
        this.avgAir.setValue(air);
    }

    private void setHeighestAir(Integer air)
    {
        LuftqualitaetService luftqualitaetService = new LuftqualitaetService();
        String rating = luftqualitaetService.getAirQualityRating(Double.valueOf(air));
        this.heighestAir.setValueColor(rating);
        this.heighestAir.setValue(air);
    }

    @Override
    public void setMainApp(MainApp app) {
        this.app = app;
    }
}
