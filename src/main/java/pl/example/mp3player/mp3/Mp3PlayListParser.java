package pl.example.mp3player.mp3;

/* Andrzej Kamiński */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Mp3PlayListParser implements Serializable {

    private static final String MP3_PLAY_LIST_NUMBER = "PLAYLIST_NUMBER";
    private static final String MP3_PLAY_LIST_NAME = "PLAYLIST_NAME";
    private static final String MP3_PLAY_LIST_LIST = "PLAYLIST_LIST";

    public static HashMap<String, Object> mp3ListMap = new HashMap<>();

    public static void createNewPlaylist(Mp3PlayList mp3PlayList) {
        int mp3PlayListNumber = getMp3PlayListNumber() + 1;
        mp3ListMap.put(MP3_PLAY_LIST_NUMBER, mp3PlayListNumber);
        mp3ListMap.put(MP3_PLAY_LIST_NAME + mp3PlayListNumber, mp3PlayList.getNamePlayList());
        mp3ListMap.put(MP3_PLAY_LIST_LIST + mp3PlayListNumber, mp3PlayList.getSongListFromPlaylist());
    }

    public static void refreshList(ListView<Mp3PlayList> listView) {
        ObservableList<Mp3PlayList> items = getAll();
        listView.setItems(null);
        listView.setItems(items);
    }

    private static ObservableList<Mp3PlayList> getAll() {
        ObservableList<Mp3PlayList> observableMp3PlayList = FXCollections.observableArrayList();

        for (int index = 1; index <= getMp3PlayListNumber(); index++) {
            String listName = (String) mp3ListMap.get(MP3_PLAY_LIST_NAME + index);
            List<Mp3Song> songList = (List<Mp3Song>) mp3ListMap.get(MP3_PLAY_LIST_LIST + index);
            Mp3PlayList mp3PlayList = new Mp3PlayList(index, listName, songList);
            observableMp3PlayList.add(mp3PlayList);
        }
        return observableMp3PlayList;
    }

    public static void delete(Mp3PlayList mp3PlayList) {
        mp3ListMap.put(MP3_PLAY_LIST_NUMBER, (int) mp3ListMap.get(MP3_PLAY_LIST_NUMBER) - 1);
        mp3ListMap.remove(MP3_PLAY_LIST_LIST + mp3PlayList.getIdPlayList());
        mp3ListMap.remove(MP3_PLAY_LIST_NAME + mp3PlayList.getIdPlayList());
    }

    public static void updateValueAfterDelete(int selectedIndex) {
        for (int index = selectedIndex + 1; index <= getMp3PlayListNumber(); index++) {

            index++;

            String listName = (String) mp3ListMap.get(MP3_PLAY_LIST_NAME + index);
            List<Mp3Song> songList = (List<Mp3Song>) mp3ListMap.get(MP3_PLAY_LIST_LIST + index);

            mp3ListMap.remove(MP3_PLAY_LIST_NAME + index);
            mp3ListMap.remove(MP3_PLAY_LIST_LIST + index);

            index--;

            Mp3PlayList mp3PlayList = new Mp3PlayList(index, listName, songList);

            mp3ListMap.put(MP3_PLAY_LIST_NAME + index, mp3PlayList.getNamePlayList());
            mp3ListMap.put(MP3_PLAY_LIST_LIST + index, mp3PlayList.getSongListFromPlaylist());
        }
        mp3ListMap.size();
    }

    public static int getMp3PlayListNumber() {
        return (int) mp3ListMap.get(MP3_PLAY_LIST_NUMBER);
    }
}