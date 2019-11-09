package pl.example.mp3player.mp3;

/* Andrzej Kamiński */

import javafx.beans.property.*;

import java.util.List;

public class Mp3PlayList {

    private IntegerProperty idPlayList;
    private StringProperty namePlayList;
    private List<Mp3Song> songListFromPlaylist;

    public Mp3PlayList(Integer idPlayList, String namePlayList, List<Mp3Song> songListFromPlaylist) {
        this.idPlayList = new SimpleIntegerProperty(idPlayList);
        this.namePlayList = new SimpleStringProperty(namePlayList);
        this.songListFromPlaylist = songListFromPlaylist;
    }

    int getIdPlayList() {
        return idPlayList.get();
    }

    public String getNamePlayList() {
        return namePlayList.get();
    }

    public List<Mp3Song> getSongListFromPlaylist() {
        return songListFromPlaylist;
    }
}
