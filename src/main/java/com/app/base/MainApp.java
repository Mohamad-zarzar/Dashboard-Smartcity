package com.app.base;

import com.app.controller.*;
import com.app.model.service.TemperatureService;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.squareup.okhttp.OkHttpClient;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SensorManagementApi;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ConnectException;

/**
 * MainApp zum starten des Programms
 */
public class MainApp extends Application {

	private Stage primaryStage;
	private RootLayoutController rootController;
	private SideMenuController sideMenuController;
	private StackPane rootLayout;
	private StackPane sideMenu;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;
		this.primaryStage.setMinHeight(CssConstants.STAGE_MIN_HEIGHT);
		this.primaryStage.setMinWidth(CssConstants.STAGE_MIN_WIDTH);
		this.primaryStage.setTitle(CssConstants.APP_TITLE);

		this.initRootLayout();
		this.initSideMenu();
		SensorManagementApi sensorManagementApi = new SensorManagementApi();
		try {
			sensorManagementApi.all1(null, null, null);
			showDashboardOverview();
		} catch (ApiException e) {
			error(ErrorMessageConstants.API_UNREACHABLE_HEADER, ErrorMessageConstants.API_UNREACHABLE_BODY);
		}
		API_Beispiel api_beispiel = new API_Beispiel();
		// api_beispiel.test();
	}

	/**
	 * Initializes the root layout.
	 */

	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader rootLoader = new FXMLLoader();
			rootLoader.setLocation(MainApp.class.getResource(FxmlConstants.ROOT_LAYOUT_FXML));

			rootLayout = rootLoader.load();

			// Give the controller access to the main app.
			rootController = rootLoader.getController();
			rootController.setMainApp(this);

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			this.addStyles(scene);
			primaryStage.setScene(scene);
			// primaryStage.setMaximized(true);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Adds stylesheets
	 * 
	 * @param scene
	 */
	private void addStyles(Scene scene) {
		final ObservableList<String> stylesheets = scene.getStylesheets();
		stylesheets.add(MainApp.class.getResource(CssConstants.CSS_PATH).toExternalForm());
		// stylesheets.add(CssConstants.STYLESHEET);
	}

	/**
	 * Destroys the Side Menu when closing
	 */
	public void destroySideMenu() {
		rootController.closeSideMenu();
		rootController.setSideMenu(new StackPane());
	}

	/**
	 * Initializes the Side Menu
	 */
	public void initSideMenu() {
		try {
			FXMLLoader sideMenuLoader = new FXMLLoader();
			sideMenuLoader.setLocation(MainApp.class.getResource(FxmlConstants.SIDEMENU_FXML));
			sideMenu = sideMenuLoader.load();
			sideMenuController = sideMenuLoader.getController();
			sideMenuController.setMainApp(this);
			rootController.setSideMenu(sideMenu);
			rootController.openSideMenu();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes a given Overview Page
	 *
	 * @param pageName Name of Page
	 */
	public void showOverviewPage(String pageName) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(pageName));
			Pane overview = loader.load();

			// Set overview into the center of root layout.
			rootController.setContent(overview);

			// Give the controller access to the main app.
			OverviewController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes a given Overview Page and binds the Controller
	 *
	 * @param fxmlPath Name of Page
	 * @param obj      Object, given to the controller (optional)
	 */
	public <T> void showOverviewPage(String fxmlPath, T obj) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(fxmlPath));
			Pane overview = loader.load();

			// Set overview into the center of root layout.
			rootController.setContent(overview);

			// Give the controller access to the main app.
			ModelOverviewController<T> controller = loader.getController();
			controller.setMainApp(this);

			if (obj != null) {
				controller.setObj(obj);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the Dasboard Page
	 */
	public void showDashboardOverview() {
		showOverviewPage(FxmlConstants.DASHBOARD_FXML);
	}

	/**
	 * Shows the Temperature Page
	 */
	public void showTemperatureOverview() {
		showOverviewPage(FxmlConstants.TEMP_LAYOUT_FXML);
	}

	/**
	 * Shows the Parkingspaces Page
	 */
	public void showParkplaetzeOverview() {
		showOverviewPage(FxmlConstants.PARK_LAYOUT_FXML);
	}

	/**
	 * Shows the AirQuality Page
	 */
	public void showAirQualityOverview() {
		showOverviewPage(FxmlConstants.AIR_LAYOUT_FXML);
	}

	/**
	 * Shows the Parkingspaces Page
	 */
	public void showMapOverview() {
		showOverviewPage(FxmlConstants.KARTE_LAYOUT_FXML);
	}

	public void error(String header, String body) {
		JFXAlert alert = new JFXAlert(primaryStage);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setOverlayClose(false);
		JFXDialogLayout layout = new JFXDialogLayout();
		layout.setHeading(new Label(header));
		layout.setBody(new Label(body));
		JFXButton closeButton = new JFXButton("Ok");
		closeButton.getStyleClass().add("dialog-accept");
		closeButton.setOnAction(event -> alert.hideWithAnimation());
		layout.setActions(closeButton);
		alert.setContent(layout);
		alert.show();
	}
}
