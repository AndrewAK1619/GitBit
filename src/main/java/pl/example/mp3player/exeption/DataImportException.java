package pl.example.mp3player.exeption;

/* Andrzej Kamiński */

public class DataImportException extends RuntimeException {
    public DataImportException(String message) {
        super(message);
    }
}