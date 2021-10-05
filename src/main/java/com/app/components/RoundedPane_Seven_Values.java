package com.app.components;

import com.app.base.FxmlConstants;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.threeten.bp.OffsetDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Klasse RoundedPane_Seven_Values
 * extends Pane
 * Abgerundetes Pane das sieben Werte beinhaltet
 */
public class RoundedPane_Seven_Values extends Pane {

    @FXML
    public VBox box;

    @FXML
    public Label text;

    @FXML
    public Label value1;

    @FXML
    public Label value2;

    @FXML
    public Label value3;

    @FXML
    public Label value4;

    @FXML
    public Label value5;

    @FXML
    public Label value6;

    @FXML
    public Label value7;

    @FXML
    public Label day1;

    @FXML
    public Label day2;

    @FXML
    public Label day3;

    @FXML
    public Label day4;

    @FXML
    public Label day5;

    @FXML
    public Label day6;

    @FXML
    public Label day7;


    /**
     * leerer Konstruktor
     *
     */
    public RoundedPane_Seven_Values() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlConstants.ROUNDED_PANE_SEVEN_FXML));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.getStyleClass().add("dashboard-roundend-panel");
    }

    /**
     * Konstruktor mit Werten
     * @param text Text der über den Werten dargestellt wird
     * @param values Map mit den Werten die benutzt werde sollen
     * @param styleclass StyleClass die benutzt werden soll
     * ruft Methode init(String text, Map<OffsetDateTime, Double> values, String styleclass) auf
     */
    public RoundedPane_Seven_Values(String text, Map<OffsetDateTime, Double> values, String styleclass) {
        init(text,values, styleclass);
    }

    /**
     * Initialisierungs Methode
     * @param text Text der über den Werten dargestellt wird
     * @param values Map mit den Werten die benutzt werde sollen
     * @param styleclass StyleClass die benutzt werden soll
     */
    public void init(String text, Map<OffsetDateTime, Double> values, String styleclass){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlConstants.ROUNDED_PANE_SEVEN_FXML));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.text.setText(text);
        List<OffsetDateTime> tage = new ArrayList<>();

        Iterator<OffsetDateTime> it = values.keySet().iterator();
        while (it.hasNext())
        {
            tage.add(it.next());
        }

        this.value1.setText(values.get(tage.get(0)).toString());
        this.value2.setText(values.get(tage.get(1)).toString());
        this.value3.setText(values.get(tage.get(2)).toString());
        this.value4.setText(values.get(tage.get(3)).toString());
        this.value5.setText(values.get(tage.get(4)).toString());
        this.value6.setText(values.get(tage.get(5)).toString());
        this.value7.setText(values.get(tage.get(6)).toString());
        this.day1.setText(tage.get(0).getDayOfWeek().toString().substring(0,3));
        this.day2.setText(tage.get(1).getDayOfWeek().toString().substring(0,3));
        this.day3.setText(tage.get(2).getDayOfWeek().toString().substring(0,3));
        this.day4.setText(tage.get(3).getDayOfWeek().toString().substring(0,3));
        this.day5.setText(tage.get(4).getDayOfWeek().toString().substring(0,3));
        this.day6.setText(tage.get(5).getDayOfWeek().toString().substring(0,3));
        this.day7.setText(tage.get(6).getDayOfWeek().toString().substring(0,3));

        this.getStyleClass().add(styleclass);
    }
}
