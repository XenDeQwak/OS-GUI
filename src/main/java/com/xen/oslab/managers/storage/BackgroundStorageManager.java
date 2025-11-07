package com.xen.oslab.managers.storage;

import java.nio.file.*;
import com.google.gson.*;

public class BackgroundStorageManager {
    private static final Path FILE_PATH = Paths.get("desktop_files/background.json");
    private final Gson gson = new Gson();

    public void saveBackground(String bgFile) {
        JsonObject obj = new JsonObject();
        obj.addProperty("background", bgFile);
        try {
            Files.createDirectories(FILE_PATH.getParent());
            Files.writeString(FILE_PATH, gson.toJson(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadBackground() {
        if (!Files.exists(FILE_PATH)) return null;
        try {
            String content = Files.readString(FILE_PATH);
            JsonObject obj = gson.fromJson(content, JsonObject.class);
            if (obj.has("background")) {
                return obj.get("background").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
