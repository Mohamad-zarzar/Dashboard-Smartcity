package com.app.controller;

import com.app.base.MainApp;
import com.app.base.OtherConstants;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

public class SideMenuController
{
    private MainApp app;

    @FXML
    private StackPane root;

    @FXML
    private JFXListView sideList;



    @FXML
    private void initialize()
    {
        this.root.setVisible(true);

        final ListView lv = sideList;
        lv.setOnMouseClicked(event ->
        {
            switch (lv.getSelectionModel().getSelectedIndex())
            {
                case OtherConstants.IS_DASHBOARD:
                    app.showDashboardOverview();
                    break;
                case OtherConstants.IS_TEMP:
                    app.showTemperatureOverview();
                    break;
                case OtherConstants.IS_AIR:
                    app.showAirQualityOverview();
                    break;
                case OtherConstants.IS_PARK:
                    app.showParkplaetzeOverview();
                    break;
                case OtherConstants.IS_MAP:
                    app.showMapOverview();
                default:
                    break;
            }
            event.consume();
        });
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param app
     */
    public void setMainApp(MainApp app)
    {
        this.app = app;
    }
}
