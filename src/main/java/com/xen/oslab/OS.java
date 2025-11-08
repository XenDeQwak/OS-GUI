package com.xen.oslab;

import java.io.IOException;
import com.xen.oslab.managers.LoginManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OS extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("log-in.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        LoginManager loginManager = fxmlLoader.getController();
        loginManager.setStage(stage);
        stage.setScene(scene);
        stage.setTitle("OS GUI!");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
