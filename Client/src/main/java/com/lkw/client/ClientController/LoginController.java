package com.lkw.client.ClientController;

import com.lkw.client.MainStart;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

//登录界面控制类
public class LoginController {
    @FXML
    private TextField password;
    @FXML
    private TextField username;

    public LoginController(){

    }



    public void loginBtn(){
        //登录事务处理

        //
        Platform.runLater(()->{
            System.out.println("登录成功,跳转ing");
            Stage primaryStage = (Stage)username.getScene().getWindow();
            primaryStage.hide();
            new MainStart().start(primaryStage);
        });

    }

    public void registerBtn(){
        System.out.println("正在注册");

    }



}
