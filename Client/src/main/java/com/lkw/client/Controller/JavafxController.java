package com.lkw.client.Controller;

import com.lkw.client.Thread.MyClientThread;
import com.lkw.client.Thread.MyFileThread;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class JavafxController  {

    @FXML
    private Button selectFileBtn;
    @FXML
    private ListView<String> fileList=new ListView<>();

    private ObservableList<String> fileItems;

    @FXML
    private ListView<String> userList=new ListView<>();

    private ObservableList<String> userItems;





    @FXML
    private Button sendBtn;

    @FXML
    private Button sendFileBtn;
    @FXML
    private Button acceptAreaFileBtn;

    @FXML
    private TextArea sendArea;

    // @FXML
    // private TextArea acceptArea;

    @FXML
    private TextArea filePathArea;

    @FXML
    private Text toolTips;

    @FXML
    private VBox msgVbox;

    @FXML
    private ImageView ic;

    public void init(String username) {

        //添加文件列表 示例
        fileItems = fileList.getItems();

        userItems=userList.getItems();
        userItems.add("");


        //初始化
        //可以继承一个监听接口
        Platform.runLater(() -> {
            //其他线程调用组件,可以用这个进行保护
            new Thread(new MyClientThread(sendBtn,sendArea,toolTips, username,fileItems,msgVbox,ic)).start();
            new Thread(new MyFileThread(sendFileBtn,acceptAreaFileBtn,filePathArea,fileList,selectFileBtn,toolTips)).start();
        });
    }

}
