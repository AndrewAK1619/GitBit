package pl.example.mp3player.controller;

/* Andrzej Kamiński */

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import pl.example.mp3player.mp3.Mp3Song;

public class ContentPaneController {

    @FXML
    private TableView<Mp3Song> contentTable;

    private static final String ID_COLUMN = "#";
    private static final String TITLE_COLUMN = "Title";
    private static final String AUTHOR_COLUMN = "Author";
    private static final String ALBUM_COLUMN = "Album";

    public void initialize() {
        configureTableColumns();
    }

    private void configureTableColumns() {
        TableColumn<Mp3Song, Integer> idColumn = new TableColumn<>(ID_COLUMN);
        idColumn.setMinWidth(20);
        idColumn.setPrefWidth(30);
        idColumn.setMaxWidth(40);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idSong"));

        TableColumn<Mp3Song, String> titleColumn = new TableColumn<>(TITLE_COLUMN);
        titleColumn.setMinWidth(200);
        titleColumn.setPrefWidth(400);
        titleColumn.setMaxWidth(1000);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Mp3Song, String> authorColumn = new TableColumn<>(AUTHOR_COLUMN);
        authorColumn.setMinWidth(100);
        authorColumn.setPrefWidth(200);
        authorColumn.setMaxWidth(300);
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Mp3Song, String> albumColumn = new TableColumn<>(ALBUM_COLUMN);
        albumColumn.setMinWidth(100);
        albumColumn.setPrefWidth(200);
        albumColumn.setMaxWidth(300);
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));

        contentTable.getColumns().add(idColumn);
        contentTable.getColumns().add(titleColumn);
        contentTable.getColumns().add(authorColumn);
        contentTable.getColumns().add(albumColumn);
    }

    TableView<Mp3Song> getContentTable() {
        return contentTable;
    }

    int lastItemInContentTable() {
        return contentTable.getItems().size() - 1;
    }
}