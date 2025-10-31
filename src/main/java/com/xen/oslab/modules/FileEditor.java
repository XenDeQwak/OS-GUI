package com.xen.oslab.modules;

import com.xen.oslab.objects.File;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FileEditor {
    public static void open(File file, Runnable onSave) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(file.getFileName());

        TextArea textArea = new TextArea(file.getContent());
        textArea.setWrapText(true);
        textArea.setPrefSize(400, 300);

        textArea.setOnKeyReleased(e->file.setContent(textArea.getText()));

        VBox root = new VBox(textArea);
        stage.setScene(new Scene(root));
        stage.show();
        stage.setOnCloseRequest(e-> {
            file.setContent(textArea.getText());
            if (onSave != null) onSave.run();
        });
        
    }
}
