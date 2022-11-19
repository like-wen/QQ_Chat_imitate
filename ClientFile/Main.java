package com.lkw.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;

import java.io.IOException;
import java.util.Objects;


public class Mian  extends Application {
    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {


            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/main.fxml")));
            primaryStage.setTitle("QQ");
            primaryStage.getIcons().add(new Image("/QQ.png"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();



    }
}