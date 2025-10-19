package com.xen.oslab;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class File extends VBox {

    private double offsetX;
    private double offsetY;

    public File(String name) {
        LoadImages image = new LoadImages();

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

        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
    }

    private void onMousePressed(MouseEvent e) {
        offsetX = e.getSceneX() - getLayoutX();
        offsetY = e.getSceneY() - getLayoutY();
    }

    private void onMouseDragged(MouseEvent e) {
        setLayoutX(e.getSceneX() - offsetX);
        setLayoutY(e.getSceneY() - offsetY);
    }
}
