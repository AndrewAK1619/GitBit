package pl.example.mp3player.controller;

/* Andrzej Kamiński */

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.MediaException;
import javafx.stage.*;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.util.List;

import pl.example.mp3player.exeption.DataExportException;
import pl.example.mp3player.io.file.FileManager;
import pl.example.mp3player.io.file.SerializableFileManager;
import pl.example.mp3player.mp3.Mp3PlayList;
import pl.example.mp3player.mp3.Mp3PlayListParser;
import pl.example.mp3player.mp3.Mp3Parser;
import pl.example.mp3player.mp3.Mp3Song;
import pl.example.mp3player.player.Mp3Player;

import static pl.example.mp3player.main.Main.getPrimaryStage;
import static pl.example.mp3player.mp3.Mp3PlayListParser.*;

public class MainController {

    @FXML
    private MenuPaneController menuPaneController;
    @FXML
    private ListPaneController listPaneController;
    @FXML
    private ContentPaneController contentPaneController;
    @FXML
    private ControlPaneController controlPaneController;
    @FXML
    private NewMp3PlayListPaneController newMp3PlayListPaneController;

    private FileManager fileManager;

    private ListView<Mp3PlayList> playListView;
    private TableView<Mp3Song> contentTable;

    private Mp3Player player;

    private Stage createPlayListStage;
    private Scene scene;
    private Button createNewPlaylistButton;

    private Mp3PlayList currentMp3PlayList;

    private boolean firstValue = true;

    public void initialize() {
        onShowingPrimaryStage();
        file();
        createPlayer();
        configureMenu();
        configureTableClick();
        configureDeleteMp3Song();
        mouseDragOver();
        createNewPlayList();
        configureListViewClick();
        configureDeletePlayList();
        configureButtons();
        contentTableBackground();
    }

    private void onShowingPrimaryStage() {
        getPrimaryStage().setOnShowing(windowEvent -> createNewPlaylistButton.requestFocus());
    }

    private void file() {
        fileManager = new SerializableFileManager();
        try {
            Mp3PlayListParser.mp3ListMap = fileManager.importData();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private void createPlayer() {
        ObservableList<Mp3Song> items = contentPaneController.getContentTable().getItems();
        player = new Mp3Player(items);
    }

    private void configureMenu() {
        MenuItem openFile = menuPaneController.getFileMenuItem();
        MenuItem openDir = menuPaneController.getDirMenuItem();
        menuPaneController.setMainController(MainController.this);

        openFile.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mp3", "*.mp3"));
            List<File> listFile = fc.showOpenMultipleDialog(new Stage());

            addNewMp3ToContentTable(listFile);
        });

        openDir.setOnAction(event -> {
            DirectoryChooser dc = new DirectoryChooser();
            File dir = dc.showDialog(new Stage());
            try {
                int currentSizeTable = contentTable.getItems().size();

                contentTable.getItems().addAll(Mp3Parser.createMp3List(dir));
                showMessage("Loaded data from the folder: " + dir.getName());
                if (currentSizeTable == 0) {
                    playSelectedSong(0);
                    contentTable.getSelectionModel().selectFirst();
                }
            } catch (IndexOutOfBoundsException e) {
                showMessage("No mp3 files found in the folder: " + dir.getName());
//                e.printStackTrace();
            } catch (Exception e) {
                showMessage("An error occurred while reading the folder");
//                e.printStackTrace();
            }
        });
    }

    private void addNewMp3ToContentTable(List<File> listFile) {
        int currentSizeTable = contentTable.getItems().size();
        int loadedSong = 0;

        if (listFile != null) {
            for (File file : listFile) {
                try {
                    contentTable.getItems().add(Mp3Parser.createMp3Song(file));
                    loadedSong++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (currentSizeTable == 0 && loadedSong != 0) {
                playSelectedSong(0);
                contentTable.getSelectionModel().selectFirst();
            }
            if (loadedSong == 1) {
                showMessage("File loaded: " + listFile.get(0).getName());
                playSelectedSong(contentPaneController.lastItemInContentTable());
                contentTable.getSelectionModel().select(contentPaneController.lastItemInContentTable());
            } else if (loadedSong == 0 && listFile.size() == 1) {
                showMessage("Could not open file: " + listFile.get(0).getName());
            } else if (loadedSong == listFile.size()) {
                showMessage("Selected files loaded - " + loadedSong);
            } else {
                showMessage("Selected files loaded - " + loadedSong + " / " + listFile.size()
                        + ".   An attempt to load " + (listFile.size() - loadedSong)
                        + " files failed.");
            }
        }
    }

    private void configureTableClick() {
        contentTable = contentPaneController.getContentTable();
        contentTable.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                if (event.getClickCount() == 2) {
                    int selectedIndex = contentTable.getSelectionModel().getSelectedIndex();
                    playSelectedSong(selectedIndex);
                }
            } catch (IndexOutOfBoundsException e) {
                showMessage("No song selected");
            }
        });
    }

    private void configureDeleteMp3Song() {
        contentTable.setOnKeyPressed(keyEvent -> {
            int selectedItem = contentTable.getSelectionModel().getSelectedIndex();
            String deleteNameSong;

            try {
                deleteNameSong = contentTable.getItems().get(selectedItem).getTitle();
            } catch (Exception e) {
                deleteNameSong = "ERROR";
            }

            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                int sizeBeforeDelete = contentTable.getItems().size();
                try {
                    contentTable.getItems().remove(selectedItem);
                    showMessage("Song deleted: " + deleteNameSong);
                    for (int i = selectedItem; i <= sizeBeforeDelete - 2; i++) {
                        contentTable.getItems().get(i).setIdSong(i + 1);
                    }
                    Mp3Parser.setIdSong(sizeBeforeDelete - 1);
                    if (deleteNameSong.equals(player.getTitle())) {
                        playSelectedSong(contentTable.getSelectionModel().getSelectedIndex());
                    }
                } catch (IndexOutOfBoundsException e) {
                    Mp3Parser.setIdSong(0);
                    if (sizeBeforeDelete == 1) {
                        showMessage("Song deleted: " + deleteNameSong);
                        clearControlPane();
                    } else if (sizeBeforeDelete == 0) {
                        showMessage("No songs to delete");
                    }
                }
            }
        });
    }

    private void mouseDragOver() {
        contentTable.setOnDragOver(dragEvent -> {
            final Dragboard dragboard = dragEvent.getDragboard();

            if (dragboard.hasFiles()) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            } else {
                dragEvent.consume();
            }
        });

        addMp3DragDropped();
    }

    private void addMp3DragDropped() {
        contentTable.setOnDragDropped(event -> {
            final Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                success = true;
                List<File> listFile = db.getFiles();
                addNewMp3ToContentTable(listFile);
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void createNewPlayList() {
        createNewPlaylistButton = listPaneController.getCreateNewPlaylistButton();
        playListView = listPaneController.getPlayListListView();

        createNewPlaylistButton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/fxml/newMp3PlayListPane.fxml"));

                    createPlayListStage = new Stage();
                    createPlayListStage.setTitle("New playlist");
                    createPlayListStage.initStyle(StageStyle.TRANSPARENT);
                    createPlayListStage.initModality(Modality.WINDOW_MODAL);
                    createPlayListStage.initOwner(getPrimaryStage());
                    createPlayListStage.centerOnScreen();
                    scene = new Scene(loader.load());
                    createPlayListStage.setScene(scene);

                    newMp3PlayListPaneController = loader.getController();
                    newMp3PlayListPaneController.setDialogStage(createPlayListStage);
                    newMp3PlayListPaneController.setListPaneController(listPaneController);
                    newMp3PlayListPaneController.setPlayListView(playListView);
                    newMp3PlayListPaneController.setContentTable(contentTable);
                    newMp3PlayListPaneController.setMainController(MainController.this);

                    onShowingCreatePlayListStage();

                    createPlayListStage.showAndWait();

                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        });
        playListFactory();
        if (mp3ListMap.get("PLAYLIST_NUMBER") != null) {
            refreshList(playListView);
        }
    }

    private void onShowingCreatePlayListStage() {
        createPlayListStage.onShowingProperty();
        createPlayListStage.setOnShowing(windowEvent -> {
            newMp3PlayListPaneController.getNameOfPlaylistTextField().requestFocus();

            scene.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER)
                    newMp3PlayListPaneController.getAcceptButton().fire();
            });
        });
    }

    void playListFactory() {
        playListView.setCellFactory((ListView<Mp3PlayList> p) -> new ListCell<>() {
            @Override
            protected void updateItem(Mp3PlayList mp3PlayList, boolean empt) {
                super.updateItem(mp3PlayList, empt);
                if (empt || mp3PlayList == null) {
                    setText(null);
                } else {
                    setText(mp3PlayList.getNamePlayList());
                }
            }
        });
    }

    private void configureListViewClick() {
        playListView.setOnMouseClicked((MouseEvent click) -> {

            if (click.getClickCount() == 2) {
                if (currentMp3PlayList != null) {
                    currentMp3PlayList.getSongListFromPlaylist().clear();
                    currentMp3PlayList.getSongListFromPlaylist().addAll(contentTable.getItems());
                }
                currentMp3PlayList = playListView.getSelectionModel().getSelectedItem();

                if (currentMp3PlayList != null) {

                    contentTable.getItems().clear();

                    List<Mp3Song> songList = currentMp3PlayList.getSongListFromPlaylist();
                    for (Mp3Song song : songList) {
                        contentTable.getItems().add(song);
                    }
                    int sizeContentTable = contentTable.getItems().size();
                    Mp3Parser.setIdSong(sizeContentTable);
                    for (int i = 0; i <= sizeContentTable - 1; i++) {
                        contentTable.getItems().get(i).setIdSong(i + 1);
                    }
                }
                contentTable.requestFocus();
            }
        });
    }

    private void configureDeletePlayList() {
        playListView.setOnKeyPressed(keyEvent -> {
            int selectedIndex;

            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                if (playListView.getItems().size() != 0) {
                    String deletePlayListName = null;

                    Mp3PlayList mp3PlayList3 = playListView.getSelectionModel().getSelectedItem();
                    try {
                        deletePlayListName = mp3PlayList3.getNamePlayList();
                    } catch (NullPointerException e) {
                        showMessage("No PlayLists selected");
                    }
                    selectedIndex = playListView.getSelectionModel().getSelectedIndex();

                    try {
                        delete(mp3PlayList3);
                        showMessage("PlayList removed: " + deletePlayListName);
                        updateValueAfterDelete(selectedIndex);
                        refreshList(playListView);
                    } catch (NullPointerException e) {
                        if (playListView.getItems().size() == 0) {
                            showMessage("No PlayList to delete");
                        } else {
                            showMessage("No PlayLists selected");
                        }
                    }

                    if (playListView.getItems().size() != 0) {

                        if (selectedIndex > 0) {
                            playListView.getSelectionModel().select(selectedIndex - 1);
                            if (currentMp3PlayList.getNamePlayList().equals(mp3PlayList3.getNamePlayList())) {
                                refreshContentTableAfterDelete();
                                playSelectedSong(0);
                            }
                        } else {
                            playListView.getSelectionModel().selectFirst();
                            if (currentMp3PlayList.getNamePlayList().equals(mp3PlayList3.getNamePlayList())) {
                                refreshContentTableAfterDelete();
                                playSelectedSong(0);
                            }
                        }
                    }
                    if (playListView.getItems().size() == 0) {
                        contentTable.getItems().clear();
                        clearControlPane();
                    }
                } else {
                    showMessage("No PlayList to delete");
                }
            }
        });
    }

    private void refreshContentTableAfterDelete() {
        contentTable.getItems().clear();
        currentMp3PlayList = playListView.getSelectionModel().getSelectedItem();
        List<Mp3Song> songList = currentMp3PlayList.getSongListFromPlaylist();
        for (Mp3Song song : songList) {
            contentTable.getItems().add(song);
        }
    }

    private void configureProgressBarAndTimeLabels() {
        Slider progressSlider = controlPaneController.getProgressSlider();
        Label maxTimeSong = controlPaneController.getMaxTimeSong();
        Label currentTimeSong = controlPaneController.getCurrentTimeSong();

        player.getMediaPlayer().setOnReady(() -> {
            progressSlider.setMax(player.getLoadedSongLength());
            maxTimeSong.setText(formatDuration(player.getMediaPlayer().getTotalDuration()));
        });

        player.getMediaPlayer().currentTimeProperty().addListener((arg, oldVal, newVal) -> {
            progressSlider.setValue(newVal.toSeconds());

            double currentTimeSlider = progressSlider.getValue() / progressSlider.getMax();
            String formatCurrentTimeSlider = String.format("%.12f", currentTimeSlider).replaceAll(",", ".");

            StackPane trackPane = (StackPane) controlPaneController.getProgressSlider().lookup(".track");
            String style = "-fx-background-color: linear-gradient(to right, #284fc9 "
                    + formatCurrentTimeSlider + ", rgba(255, 255, 255, 0.2) " + formatCurrentTimeSlider + ");";
            trackPane.setStyle(style);

            currentTimeSong.setText(formatDuration(player.getMediaPlayer().getCurrentTime()));
        });

        progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (progressSlider.isValueChanging()) {
                player.getMediaPlayer().seek(Duration.seconds(newValue.doubleValue()));
            }
        });

        progressSlider.setOnMouseEntered(mouseEvent -> {
            StackPane trackPane = (StackPane) controlPaneController.getProgressSlider().lookup(".thumb");
            String style = "-fx-background-color: white;";
            trackPane.setStyle(style);
        });

        progressSlider.setOnMouseExited(mouseEvent -> {
            StackPane trackPane = (StackPane) controlPaneController.getProgressSlider().lookup(".thumb");
            String style = "-fx-background-color: transparent;";
            trackPane.setStyle(style);
        });
    }

    private String formatDuration(Duration duration) {
        double millis = duration.toMillis();
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) (millis / (1000 * 60));
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void configureVolume() {
        Slider volumeSlider = controlPaneController.getVolumeSlider();

        volumeSlider.valueProperty().unbind();
        double choosenValueSlider = volumeSlider.getValue();
        volumeSlider.setMax(1.0);
        volumeSlider.valueProperty().bindBidirectional(player.getMediaPlayer().volumeProperty());

        Label volumeLabel = controlPaneController.getVolumeLabel();
        FontIcon volumeUp = controlPaneController.getVolumeUp();
        volumeLabel.setGraphic(volumeUp);
        volumeUp.setId("volume-up");

        if (volumeSlider.getValue() == 1.0) {
            volumeUp.setIconLiteral("fa-volume-up");
        }

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            String formatCurrentTimeSlider = String.format("%.12f", newValue.floatValue()).replaceAll(",", ".");

            StackPane trackPane = (StackPane) controlPaneController.getVolumeSlider().lookup(".track");
            String style = "-fx-background-color: linear-gradient(to right, #284fc9 "
                    + formatCurrentTimeSlider + ", rgba(255, 255, 255, 0.2) " + formatCurrentTimeSlider + ");";
            trackPane.setStyle(style);

            if (newValue.floatValue() > 0.75) {
                volumeUp.setIconLiteral("fa-volume-up");
            } else if (newValue.floatValue() < 0.75 && newValue.floatValue() > 0.25) {
                volumeUp.setIconLiteral("fa-volume-down");
            } else if (newValue.floatValue() < 0.25) {
                volumeUp.setIconLiteral("fa-volume-off");
            }
        });

        volumeSlider.setOnMouseEntered(mouseEvent -> {
            StackPane trackPane = (StackPane) controlPaneController.getVolumeSlider().lookup(".thumb");
            String style = "-fx-background-color: white;";
            trackPane.setStyle(style);
        });

        volumeSlider.setOnMouseExited(mouseEvent -> {
            StackPane trackPane = (StackPane) controlPaneController.getVolumeSlider().lookup(".thumb");
            String style = "-fx-background-color: transparent;";
            trackPane.setStyle(style);
        });

        volumeSlider.setValue(choosenValueSlider);

        if (firstValue) {
            volumeSlider.setValue(0.0);
            firstValue = false;
            volumeSlider.setValue(1.0);
        }
    }

    private void configureButtons() {
        TableView<Mp3Song> contentTable = contentPaneController.getContentTable();
        ToggleButton playButton = controlPaneController.getPlayButton();
        Button prevButton = controlPaneController.getPreviousButton();
        Button nextButton = controlPaneController.getNextButton();
        FontIcon playButtonFontIcon = controlPaneController.getPlayButtonFontIcon();

        playButton.setOnAction(event -> {
            if (playButton.isSelected()) {
                player.play();
                playButtonFontIcon.setIconLiteral("fa-pause-circle-o");
            } else {
                player.stop();
                playButtonFontIcon.setIconLiteral("fa-play-circle-o");
            }
        });

        nextButton.setOnAction(event -> {
            contentTable.getSelectionModel().select(contentTable.getSelectionModel().getSelectedIndex() + 1);
            playSelectedSong(contentTable.getSelectionModel().getSelectedIndex());
        });

        prevButton.setOnAction(event -> {
            try {
                contentTable.getSelectionModel().select(contentTable.getSelectionModel().getSelectedIndex() - 1);
                playSelectedSong(contentTable.getSelectionModel().getSelectedIndex());
            } catch (IndexOutOfBoundsException ioobe) {
                contentTable.getSelectionModel().select(0);
                playSelectedSong(0);
            }
        });
    }

    void clearControlPane() {
        showCurrentSong("Song Name");
        controlPaneController.getMaxTimeSong().setText("00:00");
        controlPaneController.getCurrentTimeSong().setText("00:00");
        controlPaneController.getProgressSlider().setValue(0);
        if (currentMp3PlayList != null) {
            if (currentMp3PlayList.getSongListFromPlaylist().size() != 0) {
                player.getMediaPlayer().dispose();
            }
        } else if (contentTable.getItems().size() == 0) {
            player.getMediaPlayer().dispose();
        }
        Mp3Parser.setIdSong(0);
    }

    private void playSelectedSong(int selectedIndex) {
        try {
            player.loadSong(selectedIndex);
            showCurrentSong(player.getTitle());
            configureProgressBarAndTimeLabels();
            configureAutoPlayNextSong();
            configureVolume();
            controlPaneController.getPlayButtonFontIcon().setIconLiteral("fa-pause-circle-o");
        } catch (MediaException e) {
            showMessage("File not found");
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private void configureAutoPlayNextSong() {
        player.getMediaPlayer().setOnEndOfMedia(() -> {
            if (contentTable.getSelectionModel().getFocusedIndex() == contentTable.getItems().size() - 1) {
                player.stop();
            } else {
                contentTable.getSelectionModel().select(contentTable.getSelectionModel().getSelectedIndex() + 1);
                playSelectedSong(contentTable.getSelectionModel().getSelectedIndex());
            }
        });
    }

    private void contentTableBackground() {
        contentTable.getItems().addListener((ListChangeListener<Mp3Song>) c -> {

            if (contentTable.getItems().size() == 0) {
                contentTable.getStyleClass().clear();
                contentTable.getStyleClass().add("table-view-no-item");
            } else {
                contentTable.getStyleClass().clear();
                contentTable.getStyleClass().add("table-view-with-item");
            }
        });
    }

    void exit() {
        if (currentMp3PlayList != null) {
            currentMp3PlayList.getSongListFromPlaylist().clear();
            currentMp3PlayList.getSongListFromPlaylist().addAll(contentTable.getItems());
        }

        try {
            fileManager.exportData(Mp3PlayListParser.mp3ListMap);
        } catch (DataExportException e) {
//            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        controlPaneController.getMessageTextField().setText(message);
    }

    private void showCurrentSong(String message) {
        controlPaneController.getCurrentSongLabel().setText(message);
    }

    Mp3PlayList getCurrentMp3PlayList() {
        return currentMp3PlayList;
    }

    void setCurrentMp3PlayList(Mp3PlayList currentMp3PlayList) {
        this.currentMp3PlayList = currentMp3PlayList;
    }
}