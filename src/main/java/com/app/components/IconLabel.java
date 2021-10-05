package com.app.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import java.io.IOException;
import com.app.base.FxmlConstants;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Class IconLabel
 * extends HBox
 * zum Darstellen von Labeln mit Fontawesome Icons
 */
public class IconLabel extends HBox
{
    @FXML
    public Label label;

    @FXML
    public FontIcon icon;

    public IconLabel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlConstants.ICON_LABEL_FXML));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.label.setText("Empty");
        this.icon.setIconSize(20);
        this.icon.setIconColor(Color.web("black"));
        this.icon.setIconLiteral("fa-cube");
    }

    /**
     * Method getLabel
     * @return Label Text as String
     */
    public String getLabel() {
        return this.label.getText();
    }

    /**
     * Method setLabel
     * @param value Label Text as String
     */
    public void setLabel(String value) {
        this.label.setText(value);
    }

    /**
     * Method getIconLiteral
     * @return IconLiteral as String
     */
    public String getIconLiteral() {
        return this.icon.getIconLiteral();
    }

    /**
     * Method setIconLiteral
     * @param value IconLiteral as String
     */
    public void setIconLiteral(String value) {
        this.icon.setIconLiteral(value);
    }

    /**
     * Method getIconSize
     * @return IconSize as Integer
     */
    public Integer getIconSize() {
        return this.icon.getIconSize();
    }
    /**
     * Method getIconSize
     * @param value IconSize as Integer
     */
    public void setIconSize(Integer value) {
        this.icon.setIconSize(value);
    }

    /**
     * Method getIconColor
     * @return Icon Color as String
     */
    public String getIconColor() {
        return this.icon.getIconColor().toString();
    }

    /**
     * Method setIconColor
     * @param value Icon Color as String
     */
    public void setIconColor(String value) {
        this.icon.setIconColor(Color.web(value));
    }
}
