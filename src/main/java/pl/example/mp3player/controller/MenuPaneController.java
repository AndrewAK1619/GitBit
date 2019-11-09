package pl.example.mp3player.controller;

/* Andrzej Kamiński */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

import static pl.example.mp3player.main.Main.getPrimaryStage;

public class MenuPaneController {

    @FXML
    private MenuItem fileMenuItem;
    @FXML
    private MenuItem dirMenuItem;
    @FXML
    private MenuItem closeMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private ToolBar mainToolBar;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button maximizeButton;
    @FXML
    private Button closeButton;

    private MainController mainController;

    private boolean maximized = false;
    private double xOffset = 0;
    private double yOffset = 0;
    private double mXStore = 0;
    private double mYStore = 0;
    private double mWidthStore;
    private double mHeightStore;

    public void initialize() {
        configureMenu();
        configureButtons();
        maximizeDoubleClick();
        moveStage();
    }

    private void configureMenu() {
        closeMenuItem.setOnAction(x -> {
            mainController.exit();
            Platform.exit();
        });

        aboutMenuItem.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {
                    Parent parent = FXMLLoader.load(getClass().getResource("/fxml/aboutPane.fxml"));
                    Scene scene = new Scene(parent);
                    Stage stage = new Stage();
                    stage.setTitle("GitBit v1.0 - about");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void configureButtons() {
        closeButton.setOnAction(actionEvent -> {
            mainController.exit();
            Platform.exit();
            System.exit(0);
        });
        minimizeButton.setOnAction(actionEvent -> getPrimaryStage().setIconified(true));
    }

    @FXML
    private void maximizeButtonOnAction() {
        if (maximized) {
            maximized = false;
            getPrimaryStage().setMaximized(false);
            getPrimaryStage().setX(mXStore);
            getPrimaryStage().setY(mYStore);
            getPrimaryStage().setHeight(mHeightStore);
            getPrimaryStage().setWidth(mWidthStore);

            maximizeButton.setId("button-maximize");

        } else {
            maximized = true;
            mXStore = getPrimaryStage().getX();
            mYStore = getPrimaryStage().getY();
            mHeightStore = getPrimaryStage().getHeight();
            mWidthStore = getPrimaryStage().getWidth();

            getPrimaryStage().setMaximized(true);

            maximizeButton.setId("button-maximize-change");
        }
    }

    private void maximizeDoubleClick() {
        mainToolBar.setOnMouseClicked((MouseEvent click) -> {
            if (click.getClickCount() == 2) {
                maximizeButtonOnAction();
            }
        });
    }

    private void moveStage() {
        mainToolBar.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        mainToolBar.setOnMouseDragged(mouseEvent -> {
            getPrimaryStage().setX(mouseEvent.getScreenX() - xOffset);
            getPrimaryStage().setY(mouseEvent.getScreenY() - yOffset);
        });
    }

    MenuItem getFileMenuItem() {
        return fileMenuItem;
    }

    MenuItem getDirMenuItem() {
        return dirMenuItem;
    }

    void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}