package com.lkw.client.ClientController;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MainController {
    @FXML
    private TextArea sendArea;


    public void sendTextBtn(){
        System.out.println("发送文字");

    }

    public void sendFileBtn(){

    }
}
