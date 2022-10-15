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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
    @FXML
    private Text tips;

    public LoginController(){
        //创建ip
        //获取连接


    }



    public void loginBtn(){
        //登录事务处理
        String passwordText = password.getText();
        String usernameText = username.getText();
        //查询数据库

        if(false) {
            //存在


            //跳转
            Platform.runLater(() -> {
                System.out.println("登录成功,跳转ing");
                Stage primaryStage = (Stage) username.getScene().getWindow();
                primaryStage.hide();
                new MainStart().start(primaryStage);
            });

        }else{
            //不存在
            //输出提示信息
            tips.setText("你输入的用户名或密码不正确,请重新输入");

        }


    }

    public void registerBtn(){
        System.out.println("正在注册");

    }



}
