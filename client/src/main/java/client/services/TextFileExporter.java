package client.services;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TextFileExporter {

    public static void save(String text, Window owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Recipe");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")
        );

        File file = chooser.showSaveDialog(owner);
        if (file == null) {
            return;
        }

        try {
            Files.writeString(file.toPath(), text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
