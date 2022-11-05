package com.lkw.client.Controller;

import com.lkw.client.Thread.MyClientThread;
import com.lkw.client.Thread.MyFileThread;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;


public class javafxController {
    private boolean loginmode=false;

    @FXML
    private Button sendBtn;

    @FXML
    private Button sendFileBtn;

    @FXML
    private TextArea sendArea;

    @FXML
    private TextArea acceptArea;

    @FXML
    private TextArea filePathArea;

    @FXML
    private Text toolTips;


    public javafxController() {
        //初始化
        //可以继承一个监听接口
        Platform.runLater(() -> {
        //其他线程调用组件,可以用这个进行保护
            new Thread(new MyClientThread(sendBtn,sendArea,acceptArea,toolTips)).start();
            new Thread(new MyFileThread(sendFileBtn,filePathArea,toolTips)).start();
        });
    }
}

