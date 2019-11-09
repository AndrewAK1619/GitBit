package pl.example.mp3player.mp3;

/* Andrzej Kamiński */

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mp3Parser {
    private static int idSong = 0;

    public static Mp3Song createMp3Song(File file) throws IOException, TagException {
        MP3File mp3File = new MP3File(file);
        String absolutePath = file.getAbsolutePath();
        String title;
        String author;
        String album;
        try {
            title = mp3File.getID3v2Tag().getSongTitle();
        } catch (NullPointerException npe) {
            title = "";
        }
        title = title.replaceAll("�", "");
        if (title.equals("")) {
            title = file.getName().substring(0, file.getName().lastIndexOf('.'));
        }
        try {
            author = mp3File.getID3v2Tag().getLeadArtist();
        } catch (NullPointerException npe) {
            author = "";
        }
        author = author.replaceAll("�", "");
        if (author.equals("")) {
            author = "NO DATA";
        }
        try {
            album = mp3File.getID3v2Tag().getAlbumTitle();
        } catch (NullPointerException npe) {
            album = "";
        }
        album = album.replaceAll("�", "");
        if (album.equals("")) {
            album = "NO DATA";
        }

        addIdSong();
        return new Mp3Song(idSong, title, author, album, absolutePath);
    }

    private static void addIdSong() {
        idSong += 1;
    }

    public static List<Mp3Song> createMp3List(File dir) throws IOException, TagException {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }
        List<Mp3Song> songList = new ArrayList<>();
        File[] files = dir.listFiles();
        assert files != null;
        for (File f : files) {
            String fileExtension = f.getName().substring(f.getName().lastIndexOf(".") + 1);
            if (fileExtension.equals("mp3"))
                songList.add(createMp3Song(f));
        }
        return songList;
    }

    public static void setIdSong(int idSong) {
        Mp3Parser.idSong = idSong;
    }
}