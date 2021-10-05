package com.app.controller;

import com.app.base.MainApp;
import com.jfoenix.controls.*;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;

/**
 * RootLayoutController
 * Controller Klasse für UI-Grundgerüst
 */
public class RootLayoutController
{

	private AnchorPane anchorPane;
    private MainApp app;

    @FXML
    private StackPane root;

    @FXML
    private StackPane titleBurgerContainer;
    @FXML
    private JFXHamburger titleBurger;

    @FXML
    private StackPane optionsBurger;
    @FXML
    private JFXRippler optionsRippler;
    @FXML
    private JFXDrawer drawer;

    private JFXPopup toolbarPopup;

    @FXML
    private void initialize() throws IOException
    {
        // init the title hamburger icon
        final JFXTooltip burgerTooltip = new JFXTooltip("Open drawer");

        titleBurgerContainer.setOnMouseClicked(e ->
        {
            if (drawer.isClosed() || drawer.isClosing()) {
                drawer.open();
            } else {
                drawer.close();
            }
        });


        this.optionsRippler.setOnMouseClicked(e -> {

        });

        JFXTooltip.setVisibleDuration(Duration.millis(3000));
        JFXTooltip.install(titleBurgerContainer, burgerTooltip, Pos.BOTTOM_CENTER);
    }

    /**
     * setzten des Seitenmenüs
     * @param sideMenu
     */
    public void setSideMenu(StackPane sideMenu)
    {
        drawer.setSidePane(sideMenu);
        drawer.setOverLayVisible(false);
        drawer.setResizeContent(true);
    }

    public void openSideMenu()
    {
        drawer.open();
    }

    public void closeSideMenu()
    {
        drawer.close();
    }

    public void setContent(Node content)
    {
        drawer.setContent(content);
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

    public void handleClickTitle(MouseEvent mouseEvent)
    {
        this.app.showDashboardOverview();
    }
}

