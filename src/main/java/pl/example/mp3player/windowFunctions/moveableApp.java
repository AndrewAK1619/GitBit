package pl.example.mp3player.windowFunctions;

/* Andrzej Kamiński */

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;

import static pl.example.mp3player.main.Main.getPrimaryStage;
import static pl.example.mp3player.main.Main.getScene;

public class moveableApp {

    private final int HOLD_FRAME_PIX = 8;
    private final int HOLD_FRAME_UP_PIX = 0;

    private final HashMap<Cursor, EventHandler<MouseEvent>> LISTENER_WINDOW = new HashMap<>();
    private double mPresSceneX, mPresSceneY;
    private double mPresScreeX, mPresScreeY;
    private double mPresStageW, mPresStageH;

    public moveableApp() {
        createListener();
        launch();
    }

    private void createListener() {
        LISTENER_WINDOW.put(Cursor.NW_RESIZE, event -> {

            double newWidth = mPresStageW - (event.getScreenX() - mPresScreeX);
            double newHeight = mPresStageH - (event.getScreenY() - mPresScreeY);
            if (newHeight > getPrimaryStage().getMinHeight()) {
                getPrimaryStage().setY(event.getScreenY() - mPresSceneY);
                getPrimaryStage().setHeight(newHeight);
            }
            if (newWidth > getPrimaryStage().getMinWidth()) {
                getPrimaryStage().setX(event.getScreenX() - mPresSceneX);
                getPrimaryStage().setWidth(newWidth);
            }
        });

        LISTENER_WINDOW.put(Cursor.NE_RESIZE, event -> {

            double newWidth = mPresStageW - (event.getScreenX() - mPresScreeX);
            double newHeight = mPresStageH + (event.getScreenY() - mPresScreeY);
            if (newHeight > getPrimaryStage().getMinHeight()) getPrimaryStage().setHeight(newHeight);
            if (newWidth > getPrimaryStage().getMinWidth()) {
                getPrimaryStage().setX(event.getScreenX() - mPresSceneX);
                getPrimaryStage().setWidth(newWidth);
            }
        });

        LISTENER_WINDOW.put(Cursor.SW_RESIZE, event -> {

            double newWidth = mPresStageW + (event.getScreenX() - mPresScreeX);
            double newHeight = mPresStageH - (event.getScreenY() - mPresScreeY);
            if (newHeight > getPrimaryStage().getMinHeight()) {
                getPrimaryStage().setHeight(newHeight);
                getPrimaryStage().setY(event.getScreenY() - mPresSceneY);
            }
            if (newWidth > getPrimaryStage().getMinWidth()) getPrimaryStage().setWidth(newWidth);
        });

        LISTENER_WINDOW.put(Cursor.SE_RESIZE, event -> {
            double newWidth = mPresStageW + (event.getScreenX() - mPresScreeX);
            double newHeight = mPresStageH + (event.getScreenY() - mPresScreeY);
            if (newHeight > getPrimaryStage().getMinHeight()) getPrimaryStage().setHeight(newHeight);
            if (newWidth > getPrimaryStage().getMinWidth()) getPrimaryStage().setWidth(newWidth);
        });

        LISTENER_WINDOW.put(Cursor.E_RESIZE, event -> {
            double newWidth = mPresStageW - (event.getScreenX() - mPresScreeX);
            if (newWidth > getPrimaryStage().getMinWidth()) {
                getPrimaryStage().setX(event.getScreenX() - mPresSceneX);
                getPrimaryStage().setWidth(newWidth);
            }
        });

        LISTENER_WINDOW.put(Cursor.W_RESIZE, event -> {
            double newWidth = mPresStageW + (event.getScreenX() - mPresScreeX);
            if (newWidth > getPrimaryStage().getMinWidth()) getPrimaryStage().setWidth(newWidth);
        });

        LISTENER_WINDOW.put(Cursor.N_RESIZE, event -> {
            double newHeight = mPresStageH - (event.getScreenY() - mPresScreeY);
            if (newHeight > getPrimaryStage().getMinHeight()) {
                getPrimaryStage().setY(event.getScreenY() - mPresSceneY);
                getPrimaryStage().setHeight(newHeight);
            }
        });

        LISTENER_WINDOW.put(Cursor.S_RESIZE, event -> {
            double newHeight = mPresStageH + (event.getScreenY() - mPresScreeY);
            if (newHeight > getPrimaryStage().getMinHeight()) getPrimaryStage().setHeight(newHeight);
        });

        LISTENER_WINDOW.put(Cursor.OPEN_HAND, event -> {
            getPrimaryStage().setX(event.getScreenX() - mPresSceneX);
            getPrimaryStage().setY(event.getScreenY() - mPresSceneY);
        });
    }

    private void launch() {

        getScene().setOnMousePressed(event -> {
            mPresSceneX = event.getSceneX();
            mPresSceneY = event.getSceneY();

            mPresScreeX = event.getScreenX();
            mPresScreeY = event.getScreenY();

            mPresStageW = getPrimaryStage().getWidth();
            mPresStageH = getPrimaryStage().getHeight();
        });

        getScene().setOnMouseMoved(event -> {
            double sceneMovedX = event.getSceneX();
            double sceneMovedY = event.getSceneY();

            boolean east_trigger = sceneMovedX > 0 && sceneMovedX < HOLD_FRAME_PIX;
            boolean west_trigger = sceneMovedX < getScene().getWidth() && sceneMovedX > getScene().getWidth() - HOLD_FRAME_PIX;
            boolean south_trigger = sceneMovedY < getScene().getHeight() && sceneMovedY > getScene().getHeight() - HOLD_FRAME_PIX;
            boolean north_trigger = sceneMovedY > 0 && sceneMovedY < HOLD_FRAME_PIX;

            if (east_trigger && north_trigger) fireAction(Cursor.NW_RESIZE);
            else if (east_trigger && south_trigger) fireAction(Cursor.NE_RESIZE);
            else if (west_trigger && north_trigger) fireAction(Cursor.SW_RESIZE);
            else if (west_trigger && south_trigger) fireAction(Cursor.SE_RESIZE);
            else if (east_trigger) fireAction(Cursor.E_RESIZE);
            else if (west_trigger) fireAction(Cursor.W_RESIZE);
            else if (north_trigger) fireAction(Cursor.N_RESIZE);
            else if (sceneMovedY < HOLD_FRAME_UP_PIX && !south_trigger) fireAction(Cursor.OPEN_HAND);
            else if (south_trigger) fireAction(Cursor.S_RESIZE);
            else fireAction(Cursor.DEFAULT);
        });
    }

    private void fireAction(Cursor c) {
        getScene().setCursor(c);
        if (c != Cursor.DEFAULT) getScene().setOnMouseDragged(LISTENER_WINDOW.get(c));
        else getScene().setOnMouseDragged(null);
    }
}
