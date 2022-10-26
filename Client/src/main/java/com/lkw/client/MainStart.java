package com.lkw.client;

import com.lkw.client.ClientController.MainController;
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
    private String username;
    public static void main(String[] args) {
        launch(args);
    }


    public MainStart(String username) {
        this.username=username;
    }

    @Override
    public void start(Stage primaryStage) {






        Parent root1 = null;
        try {



            //root1 = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/main.fxml")));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
             root1 = loader.load();
            MainController mainController = loader.getController();
            mainController.init(username);

            primaryStage.setTitle("QQ");
            primaryStage.getIcons().add(new Image("/QQ.png"));
            primaryStage.setScene(new Scene(root1));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
