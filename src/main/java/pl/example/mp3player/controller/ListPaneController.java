package pl.example.mp3player.controller;

/* Andrzej Kamiński */

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import pl.example.mp3player.mp3.Mp3PlayList;

import java.io.Serializable;

public class ListPaneController implements Serializable {

    @FXML
    private Button createNewPlaylistButton;
    @FXML
    private ListView<Mp3PlayList> playListListView;

    Button getCreateNewPlaylistButton() {
        return createNewPlaylistButton;
    }

    ListView<Mp3PlayList> getPlayListListView() {
        return playListListView;
    }
}