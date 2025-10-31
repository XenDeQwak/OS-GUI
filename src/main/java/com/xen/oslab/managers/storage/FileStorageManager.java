package com.xen.oslab.managers.storage;

import com.google.gson.*;
import com.xen.oslab.managers.FileManager;
import com.xen.oslab.objects.File;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.nio.file.*;

public class FileStorageManager extends DesktopStorageManager {
    private final Path savePath = baseDir.resolve("file.json");

    public FileStorageManager() {
        try {
            if (!Files.exists(savePath)) Files.writeString(savePath, "[]");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAll(Pane desktop) {
        JsonArray jsonArray = new JsonArray();

        desktop.getChildren().stream()
                .filter(n -> n instanceof File)
                .map(n -> (File) n)
                .forEach(f -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("name", f.getFileName());
                    obj.addProperty("x", f.getLayoutX());
                    obj.addProperty("y", f.getLayoutY());
                    obj.addProperty("content", f.getContent());
                    jsonArray.add(obj);
                });

        write(savePath, gson.toJson(jsonArray));
    }

    public void loadAll(Pane desktop, FileManager fileManager) {
        if (!Files.exists(savePath)) return;

        String content = read(savePath);
        JsonArray arr = JsonParser.parseString(content).getAsJsonArray();

        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            String name = obj.get("name").getAsString();
            double x = obj.get("x").getAsDouble();
            double y = obj.get("y").getAsDouble();
            String txt = obj.has("content") ? obj.get("content").getAsString() : "";
            fileManager.createFileAt(name, x, y, txt);
        }
    }
}
