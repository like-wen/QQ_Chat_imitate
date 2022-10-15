package com.lkw.client.ClientController;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;

public class MainController {
    @FXML
    private TextArea sendArea;

    @FXML
    private TextArea acceptArea;


    public void sendTextBtn(){
        System.out.println("发送文字");
        sendArea.setText("");//清空发送台
        acceptArea.appendText("");//接受台打印


    }

    public void sendFileBtn(){

    }
}
