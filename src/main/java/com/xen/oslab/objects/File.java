package com.xen.oslab.objects;

import com.xen.oslab.managers.LoadManager;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class File extends VBox {

    private String fileName;
    private double offsetX;
    private double offsetY;

    public File(String name) {
        this.fileName = name;

        LoadManager image = new LoadManager();

        Image fileIcon = image.load("file.png");
        ImageView fileImage = new ImageView(fileIcon);
        fileImage.setFitWidth(48);
        fileImage.setFitHeight(48);

        Label fileName = new Label(name);
        fileName.setAlignment(Pos.CENTER);
        fileName.setWrapText(true);
        fileName.setMaxWidth(80);

        setAlignment(Pos.CENTER);
        setSpacing(5);
        getChildren().addAll(fileImage, fileName);
    }

    public String getFileName() {
        return fileName;
    }
}
