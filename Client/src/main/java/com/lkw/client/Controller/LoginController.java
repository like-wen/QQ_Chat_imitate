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

                   String usernameText=username.getText();
                   String passwordText = password.getText();
                   String msg=usernameText+":"+passwordText;
                   System.out.println(msg);
                   pWriter.write( msg+ "\r\n");
                   pWriter.flush();

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
