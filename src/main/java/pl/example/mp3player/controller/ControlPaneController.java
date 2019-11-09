package pl.example.mp3player.controller;

/* Andrzej Kamiński */

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.kordamp.ikonli.javafx.FontIcon;

public class ControlPaneController {

    @FXML
    private Button previousButton;
    @FXML
    private ToggleButton playButton;
    @FXML
    private FontIcon playButtonFontIcon;
    @FXML
    private Button nextButton;
    @FXML
    private Slider progressSlider;
    @FXML
    private Label volumeLabel;
    @FXML
    private FontIcon volumeUp;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label currentSongLabel;
    @FXML
    private Label currentTimeSong;
    @FXML
    private Label maxTimeSong;
    @FXML
    private TextField messageTextField;

    Button getPreviousButton() {
        return previousButton;
    }

    ToggleButton getPlayButton() {
        return playButton;
    }

    FontIcon getPlayButtonFontIcon() {
        return playButtonFontIcon;
    }

    Button getNextButton() {
        return nextButton;
    }

    Slider getProgressSlider() {
        return progressSlider;
    }

    Label getVolumeLabel() {
        return volumeLabel;
    }

    FontIcon getVolumeUp() {
        return volumeUp;
    }

    Slider getVolumeSlider() {
        return volumeSlider;
    }

    Label getCurrentSongLabel() {
        return currentSongLabel;
    }

    Label getCurrentTimeSong() {
        return currentTimeSong;
    }

    Label getMaxTimeSong() {
        return maxTimeSong;
    }

    TextField getMessageTextField() {
        return messageTextField;
    }
}