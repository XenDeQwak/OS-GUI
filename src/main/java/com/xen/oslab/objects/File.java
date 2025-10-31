package com.xen.oslab.objects;

import com.xen.oslab.managers.LoadManager;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class File extends VBox {
    private String fileName;
    private String content = "";
    private String filePath;
    private int row = -1;
    private int col = -1;

    public File(String name) {
        this.fileName = name;

        LoadManager image = new LoadManager();

        Image fileIcon = image.load("file.png");
        ImageView fileImage = new ImageView(fileIcon);
        fileImage.setFitWidth(48);
        fileImage.setFitHeight(48);

        Label label = new Label(name);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        label.setMaxWidth(80);

        setAlignment(Pos.CENTER);
        setSpacing(5);
        getChildren().addAll(fileImage, label);
    }

    public String getFileName() { return fileName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public int getRow() { return row; }
    public int getCol() { return col; }
    
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
