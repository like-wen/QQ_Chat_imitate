package com.lkw.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

//主页面启动类
public class MainStart extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {

        Parent root1 = null;
        try {
            root1 = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/main.fxml")));
            primaryStage.setTitle("QQ");
            primaryStage.getIcons().add(new Image("/QQ.png"));
            primaryStage.setScene(new Scene(root1));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
