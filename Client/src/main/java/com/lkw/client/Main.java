package com.lkw.client;

import com.lkw.client.Controller.JavafxController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class Main extends Application {
    private String username;
    public Main(String username) {
        this.username=username;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            root = loader.load();
            JavafxController controller = loader.getController();
            controller.init(username);

            primaryStage.setTitle("File-Cloud");
            primaryStage.getIcons().add(new Image("/cloud-file.png"));
            primaryStage.setScene(new Scene(root));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
