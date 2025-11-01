package com.xen.oslab.managers.storage;

import com.google.gson.*;
import com.xen.oslab.managers.FileManager;
import com.xen.oslab.managers.FolderManager;
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
        folderObj.addProperty("x", folder.getLayoutX());
        folderObj.addProperty("y", folder.getLayoutY());
        write(path, gson.toJson(folderObj));
    }

    public void loadFolder(Folder folder) {
        Path path = baseDir.resolve(folder.getFolderName() + ".json");
        if (!Files.exists(path)) return;

        String content = read(path);
        JsonObject folderObj = JsonParser.parseString(content).getAsJsonObject();
        JsonArray filesArray = folderObj.getAsJsonArray("files");

        for (JsonElement e : filesArray) {
            JsonObject fileObj = e.getAsJsonObject();
            File fileData = new File(fileObj.get("name").getAsString());
            fileData.setContent(fileObj.get("content").getAsString());
            fileData.setLayoutX(fileObj.get("x").getAsDouble());
            fileData.setLayoutY(fileObj.get("y").getAsDouble());
            folder.addFile(fileData);
        }
    }


    public void loadAll(Pane desktopPane, FolderManager folderManager) {
        try (Stream<Path> paths = Files.list(baseDir)) {
            paths.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    JsonElement root = JsonParser.parseString(content);
                    if (!root.isJsonObject()) return;

                    JsonObject folderObj = root.getAsJsonObject();
                    if (!folderObj.has("name") || !folderObj.has("files")) return;

                    String folderName = folderObj.get("name").getAsString();
                    double x = folderObj.get("x").getAsDouble();
                    double y = folderObj.get("y").getAsDouble();

                    Folder folder = folderManager.createFolderAt(folderName, x, y);
                    loadFolder(folder);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
