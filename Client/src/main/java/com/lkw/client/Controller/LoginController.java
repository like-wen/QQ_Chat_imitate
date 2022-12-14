package com.lkw.client.Controller;

import com.lkw.client.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
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

   @FXML
   private Hyperlink link;

   private Socket socket;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(new MyLoginThread()).start();
    }

   public class MyLoginThread implements Runnable{

       @Override
       public void run() {


           link.setOnAction(e -> {
               System.out.println("This link is clicked");
               Alert alert = new Alert(Alert.AlertType.INFORMATION);
               alert.setWidth(600.00);
               alert.setTitle("服务条款");
               alert.setHeaderText("服务条款");
               alert.setContentText("本版本生效日期：2022年7月13日\n" +
                       "\n" +
                       "欢迎来到QQChat！\n" +
                       "\n" +
                       "QQChat是由四川贰凌物联壹伴信息技术有限公司（下称“20物联1班”）提供的互联网软件服务。"+
                       "本服务条款（下称“服务条款”）是您与大麦地之间关于您（“您”或“用户”）访问和使用" +
                       "QQChat以及20物联1班提供的其他服务（下称“服务”）的主要协议。您注册、登录QQChat或" +
                       "使用20物联1班提供的服务，即表示您同意接受服务条款，如您不同意该协议，请不要使用本服务。" +
                       "因此，敬请仔细阅读。\n" +
                       "\n" +
                       "大麦地有权不时地对服务条款做任何修改和补充，并在QQChat网站上公布。" +
                       "服务条款的修改和补充将在公布时立即生效。您继续访问和使用QQChat即视为您接受修订后的服务条款。" +
                       "如您不同意修订后的服务条款，您应停止访问QQChat并拒绝使用服务或删除您在QQChat上的信息和帐户来终止接受服务。" +
                       "如果您代表某个机构而非您个人注册、登录和使用QQChat和/或我们其他的服务，" +
                       "则您将被认为获得充分授权代表该机构同意本服务条款以及不时修订和补充的服务条款。");

               alert.showAndWait();
           });


           try {
               socket = new Socket("localhost", 9999);//192.168.199.100
               //socket的输入输出
               BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
               PrintWriter pWriter = new PrintWriter(socket.getOutputStream());

               password.setPromptText("密码");
               username.setPromptText("用户名");

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
                   }else  if(json.equals("false_logged")){
                       Platform.runLater(()->{
                           tips.setText("该用户已登录");
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
