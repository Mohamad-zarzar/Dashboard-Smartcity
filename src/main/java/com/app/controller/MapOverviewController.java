package com.app.controller;

import com.app.base.MainApp;
import com.app.base.OtherConstants;
import javafx.fxml.FXML;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;

public class MapOverviewController implements OverviewController
{
    private MainApp app;

    @FXML
    private ImageView map;

    @FXML
    public void initialize()
    {
        initMap();
    }

    private void initMap()
    {
        Image image = new Image(OtherConstants.MAP_URL);
        map = new ImageView();
        map.setImage(image);
    }


    @Override
    public void setMainApp(MainApp app) {
        this.app = app;
    }
}
