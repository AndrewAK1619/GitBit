package pl.example.mp3player.player;

/* Andrzej Kamiński */

import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

import pl.example.mp3player.mp3.Mp3Song;

import java.io.File;

public class Mp3Player {

    private ObservableList<Mp3Song> songList;
    private Media media;
    private MediaPlayer mediaPlayer;
    private String title;

    public Mp3Player(ObservableList<Mp3Song> songList) {
        this.songList = songList;
    }

    public void play() {
        if (mediaPlayer != null && (mediaPlayer.getStatus() == Status.READY || mediaPlayer.getStatus() == Status.PAUSED)) {
            mediaPlayer.play();
        }
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING) {
            mediaPlayer.pause();
        }
    }

    public double getLoadedSongLength() {
        if (media != null) {
            return media.getDuration().toSeconds();
        } else {
            return 0;
        }
    }

    public void loadSong(int index) {
        if (mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING) {
            mediaPlayer.stop();
        }
        Mp3Song mp3s = songList.get(index);
        title = mp3s.getTitle();
        media = new Media(new File(mp3s.getFilePath()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.statusProperty().addListener((observable, oldStatus, newStatus) -> {
            if (newStatus == Status.READY)
                mediaPlayer.setAutoPlay(true);
        });
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public String getTitle() {
        return title;
    }
}