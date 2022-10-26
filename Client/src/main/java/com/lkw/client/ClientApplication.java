package com.lkw.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Objects;
//主启动类
@SpringBootApplication
public class ClientApplication extends Application {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {


            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));
            primaryStage.setTitle("QQ");
            primaryStage.getIcons().add(new Image("/QQ.png"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
