package com.lkw.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Login extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));
            primaryStage.setTitle("File-Cloud");
            primaryStage.getIcons().add(new Image("/cloud-file.png"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setOnCloseRequest(e->{
                System.exit(0);
            });
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
