package com.lkw.server;

import com.lkw.server.FileServer.FindFile;
import com.lkw.server.FileServer.MyFileServer;
import com.lkw.server.FileServer.ThreadPoolManager;
import com.lkw.server.Server.LoginServer;
import com.lkw.server.Server.MyServer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerUI extends Application{

	TextArea receivedMsgArea = new TextArea();
	TextField ipText = new TextField();
	TextField portText = new TextField();
	TextArea sendMsgArea = new TextArea();
	TextField statusText = new TextField();
	Button sendButton = new Button(" 发送");
	ObservableList<String> clients = FXCollections.observableArrayList();
	ListView<String> clientListView = new ListView<>(clients);


	ObservableList<String> fileList = FXCollections.observableArrayList();
	ListView<String> fileListView = new ListView<>(fileList);


	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {

		//右边 Received Message
		GridPane rightPane = new GridPane();
		rightPane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
		rightPane.setHgap(5.5);
		rightPane.setVgap(5.5);
		rightPane.add(new Label("接收数据:"), 0, 0);
		receivedMsgArea.setWrapText(true);
		receivedMsgArea.setEditable(false);
		receivedMsgArea.setMaxWidth(350);
		receivedMsgArea.setPrefHeight(400);
		rightPane.add(receivedMsgArea, 0, 1);

		//左边 IPAdress+Port
		GridPane leftPane1 = new GridPane();
		leftPane1.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
		leftPane1.setHgap(5.5);
		leftPane1.setVgap(5.5);
		leftPane1.add(new Label("IPAdress:"), 0, 0);
		ipText.setEditable(false);
		leftPane1.add(ipText, 1, 0);
		leftPane1.add(new Label("Port:"), 0, 1);
		portText.setEditable(false);
		leftPane1.add(portText, 1, 1);

		//左边 Choose Client
		GridPane leftPane2 = new GridPane();
		leftPane2.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
		leftPane2.setHgap(5.5);
		leftPane2.setVgap(5.5);
		leftPane2.add(new Label("已上线用户"), 0, 0);
		clientListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		clientListView.setMaxHeight(80);
		clientListView.setMaxWidth(275);
		leftPane2.add(clientListView, 0, 1);
		//左边 Send Message
		leftPane2.add(new Label("发送消息:"), 0, 2);
		sendMsgArea.setMaxHeight(150);
		sendMsgArea.setMaxWidth(275);
		sendMsgArea.setWrapText(true);
		leftPane2.add(sendMsgArea, 0, 3);

		//左边 Connect Status + button
		GridPane leftPane3 = new GridPane();
		leftPane3.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
		leftPane3.setHgap(5.5);
		leftPane3.setVgap(5.5);
		statusText.setEditable(false);
		leftPane3.add(statusText, 0, 0);
		leftPane3.add(sendButton, 1, 0);

		//文件的Pane
		GridPane filePane=new GridPane();
		filePane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
		filePane.setHgap(5.5);
		filePane.setVgap(5.5);
		filePane.add(new Label("云文件"), 0, 0);
		fileListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		fileListView.setMaxHeight(410);
		fileListView.setMaxWidth(200);
		filePane.add(fileListView,0,1);




		//组合
		VBox vBox = new VBox();
		vBox.getChildren().addAll(leftPane1, leftPane2, leftPane3);
		HBox hBox = new HBox();
		hBox.getChildren().addAll(vBox, rightPane,filePane);

		Scene scene = new Scene(hBox);
		primaryStage.setTitle("server");
		primaryStage.setScene(scene);
		//关闭UI线程时同时关闭各子线程
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

		new Thread(new LoginServer()).start();
		//启动server线程
		new Thread(new MyServer(ipText, portText, sendMsgArea, statusText, sendButton, receivedMsgArea, clients, clientListView)).start();
		//缝合进的文件server线程
		ThreadPoolManager.getFileServerExecutor().submit(new MyFileServer());
		//文件更新线程
		ThreadPoolManager.getFileServerExecutor().submit(new FindFile(fileList));
		primaryStage.show();
	}
}
