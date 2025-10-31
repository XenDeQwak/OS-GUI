package com.xen.oslab.managers.storage;

import com.google.gson.*;
import com.xen.oslab.objects.File;
import com.xen.oslab.objects.Folder;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class FolderStorageManager extends DesktopStorageManager {

    public void saveFolder(Folder folder) {
        Path path = baseDir.resolve(folder.getFolderName() + ".json");
        JsonArray filesArray = new JsonArray();

        for (File f : folder.getFiles()) {
            JsonObject fileObj = new JsonObject();
            fileObj.addProperty("name", f.getFileName());
            fileObj.addProperty("x", f.getLayoutX());
            fileObj.addProperty("y", f.getLayoutY());
            fileObj.addProperty("content", f.getContent());
            filesArray.add(fileObj);
        }

        JsonObject folderObj = new JsonObject();
        folderObj.addProperty("name", folder.getFolderName());
        folderObj.add("files", filesArray);
        write(path, gson.toJson(folderObj));
    }

    public void loadFolder(Folder folder, Pane folderPane) {
        Path path = baseDir.resolve(folder.getFolderName() + ".json");
        if (!Files.exists(path)) return;

        String content = read(path);
        JsonObject folderObj = JsonParser.parseString(content).getAsJsonObject();
        JsonArray filesArray = folderObj.getAsJsonArray("files");

        filesArray.forEach(e -> {
            JsonObject fileObj = e.getAsJsonObject();
            String name = fileObj.get("name").getAsString();
            double x = fileObj.get("x").getAsDouble();
            double y = fileObj.get("y").getAsDouble();
            String text = fileObj.get("content").getAsString();

            File file = new File(name);
            file.setLayoutX(x);
            file.setLayoutY(y);
            file.setContent(text);
            folder.addFile(file);
            folderPane.getChildren().add(file);
        });
    }

        public void loadAll(Pane desktopPane) {
        try (Stream<Path> paths = Files.list(baseDir)) {
            paths.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    JsonElement root = JsonParser.parseString(content);
                    if (!root.isJsonObject()) return;

                    JsonObject folderObj = root.getAsJsonObject();
                    if (!folderObj.has("name") || !folderObj.has("files")) return;

                    String folderName = folderObj.get("name").getAsString();

                    Folder folder = new Folder(folderName);

                    loadFolder(folder, new Pane());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
