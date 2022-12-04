package com.lkw.client.Controller;

import com.lkw.client.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Text tips;

   @FXML
   private Button loginBtn;


   @FXML
   private  Button signUpBtn;

   @FXML
   private CheckBox selectBool;

   private Socket socket;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(new MyLoginThread()).start();
    }

   public class MyLoginThread implements Runnable{

       @Override
       public void run() {
           try {
               socket = new Socket("127.0.0.1", 9999);

               //socket的输入输出
               BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
               PrintWriter pWriter = new PrintWriter(socket.getOutputStream());

               loginBtn.setOnAction(e->{
                   if(!selectBool.isSelected())
                       tips.setText("请阅读并同意协议");
                   else {


                       String usernameText = username.getText();
                       String passwordText = password.getText();
                       String msg = "login:"+usernameText + ":" + passwordText;
                       System.out.println(msg);
                       pWriter.write(msg + "\r\n");
                       pWriter.flush();
                   }


               });


               signUpBtn.setOnAction(e->{
                   if(!selectBool.isSelected())
                       tips.setText("请阅读并同意协议");
                   else {
                       String usernameText = username.getText();
                       String passwordText = password.getText();
                       String msg = "signUp:"+usernameText + ":" + passwordText;
                       System.out.println(msg);
                       pWriter.write(msg + "\r\n");
                       pWriter.flush();
                   }
               });
               //发消息
               String json;
               //收消息
               while(true) {
                   json = bReader.readLine();
                   System.out.println(json);
                   if (json.equals("true")) {
                       Platform.runLater(()->{
                       //关闭窗口
                       Stage stage = (Stage)signUpBtn.getScene().getWindow();
                       new Main(username.getText()).start(stage);
                       return;
                   });
                   }else if(json.equals("false")){
                       Platform.runLater(()->{
                           tips.setText("用户名或密码错误");
                       });
                   }else if(json.equals("true_signUp")){
                       Platform.runLater(()->{
                           tips.setText("注册成功");
                       });
                   } else if (json.equals("false_signUp")) {
                       Platform.runLater(()->{
                           tips.setText("该用户名已注册");
                       });
                   }
               }
           } catch (UnknownHostException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }




   }
}
