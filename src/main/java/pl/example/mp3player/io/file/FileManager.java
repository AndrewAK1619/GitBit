package pl.example.mp3player.io.file;

/* Andrzej Kamiński */

import java.util.HashMap;

public interface FileManager {
    HashMap importData();

    void exportData(HashMap mp3ListMap);
}
