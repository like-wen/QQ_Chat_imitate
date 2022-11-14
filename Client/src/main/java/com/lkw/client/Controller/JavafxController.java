package com.lkw.client.Controller;

import com.lkw.client.Thread.MyClientThread;
import com.lkw.client.Thread.MyFileThread;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;


public class JavafxController {

    @FXML
    private ListView<String> listFile=new ListView<>();

    private ObservableList<String> items;
    @FXML
    private Button sendBtn;

    @FXML
    private Button sendFileBtn;


    @FXML
    private Button acceptAreaFileBtn;

    @FXML
    private TextArea sendArea;

    @FXML
    private TextArea acceptArea;

    @FXML
    private TextArea filePathArea;

    @FXML
    private Text toolTips;


    private String username="lkw";


    public void initialize() {



        items = listFile.getItems();
        items.add("1");
        items.add("Main.java");



        //初始化
        //可以继承一个监听接口
        Platform.runLater(() -> {
        //其他线程调用组件,可以用这个进行保护
            new Thread(new MyClientThread(sendBtn,sendArea,acceptArea,toolTips,username)).start();
            new Thread(new MyFileThread(sendFileBtn,acceptAreaFileBtn,filePathArea,listFile,items,toolTips)).start();
        });
    }


}

