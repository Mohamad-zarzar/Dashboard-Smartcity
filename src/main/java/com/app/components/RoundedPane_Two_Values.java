package com.app.components;

import com.app.base.FxmlConstants;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
/**
 * Klasse RoundedPane_Seven_Values
 * extends Pane
 * Abgerundetes Pane das eine Überschrift mit einm Werte beinhaltet
 */
public class RoundedPane_Two_Values extends Pane {

    @FXML
    public VBox box;

    @FXML
    public Label text;

    @FXML
    public Label value;


    /**
     * Konstruktor
     */
    public RoundedPane_Two_Values() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlConstants.ROUNDED_PANE_TWO_FXML));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.getStyleClass().add("dashboard-roundend-panel");
    }


    public RoundedPane_Two_Values(String text, Object value) {
        init(text, value);
    }

    /**
     * Initialisierungs Methode
     * @param text Text der über Wert dargestellt wird
     * @param value der anzuzeigende Wert
     */
    public void init(String text, Object value)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlConstants.ROUNDED_PANE_TWO_FXML));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        if(value == null)
        {
            value = "";
        }

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.text.setText(text);
        this.value.setText(value.toString());
    }

    /**
     * Methode setValue
     * @param value Object
     */
    public void setValue(Object value)
    {
        if(value == null)
        {
            value = "";
        }

        this.value.setText(value.toString());
    }

    /**
     * setText
     * @param text über Wert
     */
    public void setText(String text)
    {
        this.text.setText(text);
    }

    public void setValueColor(String styleClass)
    {
        this.value.getStyleClass().removeAll();
        this.value.getStyleClass().add(styleClass);
    }
}
