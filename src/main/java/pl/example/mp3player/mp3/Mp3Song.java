package pl.example.mp3player.mp3;

/* Andrzej Kamiński */

import java.io.Serializable;

public class Mp3Song implements Serializable {
    private Integer idSong;
    private String title;
    private String author;
    private String album;
    private String filePath;

    public Mp3Song(Integer idSong, String title, String author, String album, String filePath) {
        this.idSong = idSong;
        this.title = title;
        this.author = author;
        this.album = album;
        this.filePath = filePath;
    }

    public Integer getIdSong() {
        return idSong;
    }

    public void setIdSong(Integer idSong) {
        this.idSong = idSong;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "Mp3Song{" +
                "idSong=" + idSong +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", album='" + album + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}