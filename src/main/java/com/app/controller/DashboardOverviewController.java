package com.app.controller;

import com.app.base.CssConstants;
import com.app.base.MainApp;
import com.app.components.RoundedPane_Seven_Values;
import com.app.components.RoundedPane_Two_Values;
import com.app.model.service.LuftqualitaetService;
import com.app.model.service.ParkplaetzeService;
import com.app.model.service.TemperatureService;
import io.swagger.client.ApiException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.threeten.bp.OffsetDateTime;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * DashboardOverviewController
 * Controller Klasse für Dashboard View
 */
public class DashboardOverviewController implements OverviewController{

    private MainApp app;
    private double tempAktuell;
    private double tempHoechste;
    private Integer parkFrei;
    private Integer parkBesetzt;
    private Double luftAktuell;
    private Map<OffsetDateTime, Double> siebenTage;

    @FXML
    public HBox park;

    @FXML
    public HBox air;

    @FXML
    public HBox temp;

    @FXML
    public VBox tempV;

    @FXML
    public Label parkHeader;

    @FXML
    public Label tempHeader;

    @FXML
    public Label airHeader;

    @FXML
    public RoundedPane_Two_Values aktuell;

    @FXML
    public RoundedPane_Two_Values hoechste;

    @FXML
    public RoundedPane_Seven_Values sevenValues;

    @FXML
    public RoundedPane_Two_Values belegt;

    @FXML
    public RoundedPane_Two_Values frei;

   // @FXML
   // public RoundedPane_Two_Values stoss;

    @FXML
    public RoundedPane_Two_Values qualiAkt;


    /**
     * allgemeine Initialisierungs Methode
     * initialisiert die einzelnen Sektionen des Dashboards
     */
    @FXML
    public void initialize() throws ApiException {
        initPark();
        initLuftQuali();
        initTemp();
    }

    /**
     * Initialisierungs Methode für Parking Sektion
     * setzt initiale Werte für frei und belegte Parkplätze
     * sowie die Stoßzeiten
     */
    private void initPark() {

        ParkplaetzeService parkplaetzeService = new ParkplaetzeService();
        parkFrei = parkplaetzeService.getParkFrei();
        parkBesetzt = parkplaetzeService.getParkBelegt();


        frei.init("Frei", parkFrei);
        belegt.init("Belegt", parkBesetzt);
        //stoss.init("Stoßzeiten", null);
        parkHeader.setText("Parkplätze");
    }

    /**
     * Initialisierungs Methode für Temperatur Sektion
     * setzt initiale Werte für aktuelle & höchste Temperatur
     * sowie die durchschnitts temperaturen der letzten 7 tage
     * startet einen Timer zum aktualisieren der aktuellen Temperatur
     */
    private void initTemp() {

        TemperatureService tempService = new TemperatureService();
        tempAktuell = tempService.getTempAktuell();
        tempHoechste = tempService.getHoesteAktuell();
        siebenTage = tempService.getTemp7Tage();


        aktuell.init("Aktuell", tempAktuell + "°");
        hoechste.init("Höchste", tempHoechste + "°");

        aktuell.setValueColor("dashboard-white-text");
        hoechste.setValueColor("dashboard-white-text");

        sevenValues.init("Letzten 7 Tage", siebenTage, CssConstants.SYLE_CLASS_ROUNDED_PANE_TEMP);
        tempHeader.setText("Temperatur");

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    TemperatureService tempService = new TemperatureService();
                    tempAktuell = tempService.getTempAktuell();
                    aktuell.setValue(tempAktuell + "°");
                });
            }
        }, 0, 5000);
    }


    /**
     * Initialisierungs Methode für Luftqualität Sektion
     * setzt aktuelle Luftqualität
     */
    private void initLuftQuali() {

        LuftqualitaetService luftqualitaetService =  new LuftqualitaetService();
        luftAktuell = luftqualitaetService.getAirAktuell();
        String rating = luftqualitaetService.getAirQualityRating(luftAktuell);
        qualiAkt.setValueColor(rating);
        qualiAkt.init("Aktuell", luftAktuell);
        airHeader.setText("Luftqualität");
    }

    @FXML
    private void mouseClickedTemp()
    {
        app.showTemperatureOverview();
    }

    @Override
    public void setMainApp(MainApp app) {
        this.app = app;
    }
}
