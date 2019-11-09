package pl.example.mp3player.io.file;

/* Andrzej Kamiński */

import pl.example.mp3player.exeption.DataExportException;
import pl.example.mp3player.exeption.DataImportException;

import java.io.*;
import java.util.HashMap;

public class SerializableFileManager implements FileManager {
    private static final String FILE_NAME = "GitBitPlayList.o";

    @Override
    public void exportData(HashMap mp3ListMap) {
        try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(mp3ListMap);
        } catch (FileNotFoundException e) {
            throw new DataExportException("Lack of files " + FILE_NAME);
        } catch (IOException e) {
            throw new DataExportException("Error writing the data into a file " + FILE_NAME);
        }
    }

    @Override
    public HashMap importData() {
        try (FileInputStream fis = new FileInputStream(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return (HashMap) ois.readObject();
        } catch (FileNotFoundException e) {
            throw new DataImportException("Lack of files " + FILE_NAME);
        } catch (IOException e) {
            throw new DataImportException("File reading error " + FILE_NAME);
        } catch (ClassNotFoundException e) {
            throw new DataImportException("Incompatible data type in the file " + FILE_NAME);
        }
    }
}
