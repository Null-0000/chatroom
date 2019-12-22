package client.model;

import javafx.stage.FileChooser;

import java.io.File;

public class MFileChooser {
    public static File showFileChooser(String title, String... formats) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        for (String format: formats)
            fileChooser.getExtensionFilters().
                    add(new FileChooser.ExtensionFilter(format, "*." + format));
        return fileChooser.showOpenDialog(null);
    }
}
