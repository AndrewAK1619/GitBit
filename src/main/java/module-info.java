

/* Andrzej Kamiński */

module gitbit {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires jid3lib;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires java.desktop;

    exports pl.example.mp3player.main to javafx.graphics;
    opens pl.example.mp3player.controller to javafx.fxml, org.kordamp.ikonli.javafx, org.kordamp.ikonli.fontawesome;
    opens pl.example.mp3player.mp3 to javafx.base;
}