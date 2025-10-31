package com.xen.oslab.modules;

import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FolderWindow {
    private final Folder folder;
    private final FlowPane contentPane = new FlowPane(20, 20);

    public FolderWindow(Folder folder) {
        this.folder = folder;

        for (File f : folder.getFiles())
            contentPane.getChildren().add(f);

        ScrollPane scroll = new ScrollPane(contentPane);
        scroll.setFitToWidth(true);

        Stage stage = new Stage();
        stage.setTitle(folder.getFolderName());
        stage.initModality(Modality.NONE);
        stage.setScene(new Scene(scroll, 500, 400));
        stage.show();
    }

    public void addFile(File file) {
        folder.addFile(file);
        contentPane.getChildren().add(file);
    }
}
