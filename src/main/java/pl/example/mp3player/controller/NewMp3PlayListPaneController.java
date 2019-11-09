package pl.example.mp3player.controller;

/* Andrzej Kamiński */

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import pl.example.mp3player.mp3.Mp3PlayList;
import pl.example.mp3player.mp3.Mp3Parser;
import pl.example.mp3player.mp3.Mp3Song;

import static pl.example.mp3player.mp3.Mp3PlayListParser.*;

public class NewMp3PlayListPaneController {

    @FXML
    private ToolBar mainToolBar;
    @FXML
    private Button closeButton;
    @FXML
    private TextField nameOfPlaylistTextField;
    @FXML
    private Button acceptButton;
    @FXML
    private Button cancelButton;

    private MainController mainController;
    private ListPaneController listPaneController;

    private ListView<Mp3PlayList> playListView;
    private TableView<Mp3Song> contentTable;

    private Stage dialogStage;

    private ObservableList<Mp3PlayList> observableMp3PlayListParser;
    private Mp3PlayList currentMp3PlayList;
    private String namePlayList;

    private double xOffset = 0;
    private double yOffset = 0;

    public void initialize() {
        configureButtons();
        moveStage();
    }

    private void configureButtons() {
        Pattern pattern = Pattern.compile("[\\s]*");

        mp3ListMap.putIfAbsent("PLAYLIST_NUMBER", 0);

        acceptButton.setOnAction(event -> {
            int count = 0;
            namePlayList = "New PlayList " + (getMp3PlayListNumber() + 1);

            do {
                count++;
                if (namePlayList.equals(mp3ListMap.get("PLAYLIST_NAME" + count))) {
                    namePlayList = "New PlayList " + (count + 2);
                }
            } while (mp3ListMap.get("PLAYLIST_NAME" + count) != null);

            if (!pattern.matcher(nameOfPlaylistTextField.getText()).matches()) {
                namePlayList = nameOfPlaylistTextField.getText().trim();
            }

            if (playListView.getItems().size() == 0 && contentTable.getItems().size() != 0) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Detected songs in the track list." +
                        "\r\nAdd them to the new PlayList?");

                ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == yesButton) {
                    addNewPlayList();
                } else if (result.get() == noButton) {
                    contentTable.getItems().clear();
                    Mp3Parser.setIdSong(0);
                    mainController.clearControlPane();
                    addNewPlayList();
                } else {
                    alert.close();
                }

            } else if (playListView.getItems().size() == 0 && contentTable.getItems().size() == 0) {
                addNewPlayList();

            } else if (playListView.getItems().size() != 0) {
                currentMp3PlayList = mainController.getCurrentMp3PlayList();
                if (currentMp3PlayList != null) {
                    currentMp3PlayList.getSongListFromPlaylist().clear();
                    currentMp3PlayList.getSongListFromPlaylist().addAll(contentTable.getItems());
                }
                contentTable.getItems().clear();
                Mp3Parser.setIdSong(0);
                addNewPlayList();
            }

            playListView.setItems(observableMp3PlayListParser);
            mainController.playListFactory();
            refreshList(playListView);

            playListView.getSelectionModel().select(getMp3PlayListNumber() - 1);
            currentMp3PlayList = listPaneController.getPlayListListView().getSelectionModel().getSelectedItem();
            mainController.setCurrentMp3PlayList(currentMp3PlayList);

            dialogStage.close();
        });

        closeButton.setOnAction(actionEvent -> dialogStage.close());

        cancelButton.setOnAction(actionEvent -> dialogStage.close());
    }

    private void addNewPlayList() {
        List<Mp3Song> songList = new ArrayList<>(contentTable.getItems());
        Mp3PlayList mp3PlayList = new Mp3PlayList(getMp3PlayListNumber(), namePlayList, songList);
        createNewPlaylist(mp3PlayList);
    }

    private void moveStage() {
        mainToolBar.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        mainToolBar.setOnMouseDragged(mouseEvent -> {
            dialogStage.setX(mouseEvent.getScreenX() - xOffset);
            dialogStage.setY(mouseEvent.getScreenY() - yOffset);
        });
    }

    TextField getNameOfPlaylistTextField() {
        return nameOfPlaylistTextField;
    }

    Button getAcceptButton() {
        return acceptButton;
    }

    void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    void setListPaneController(ListPaneController listPaneController) {
        this.listPaneController = listPaneController;
    }

    void setPlayListView(ListView<Mp3PlayList> playListView) {
        this.playListView = playListView;
    }

    void setContentTable(TableView<Mp3Song> contentTable) {
        this.contentTable = contentTable;
    }

    void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
