package com.lkw.server;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端
 * @author 花大侠
 *
 */
public class MyServer implements Runnable {
	
	//Server端监听的端口号
	public static final int PORT = 9999;
	//映射表 存放每个socket地址(IP:Port)及其对应的PrintWriter
	//为群发消息做准备
	Map<String, PrintWriter> map = new HashMap<>();
	//存放已连接socket地址(IP:Port)，用于clientListView
	ObservableList<String> clients;
	ListView<String> clientListView;
	
	TextField ipText;
	TextField portText;
	TextArea sendMsgArea;
	TextField statusText;
	Button sendButton;
	TextArea receivedMsgArea;
 
	public MyServer() {
		
	}
	
	public MyServer(TextField ipText, TextField portText, TextArea sendMsgArea, TextField statusText, 
			Button sendButton, TextArea receivedMsgArea, ObservableList<String> clients, ListView<String> clientListView) {
		super();
		this.ipText = ipText;
		this.portText = portText;
		this.sendMsgArea = sendMsgArea;
		this.statusText = statusText;
		this.sendButton = sendButton;
		this.receivedMsgArea = receivedMsgArea;
		this.clients = clients;
		this.clientListView = clientListView;
	}
	
	/**
	 * 更新UI界面的IP和Port
	 */
	public void updateIpAndPort() {
		//用于在非UI线程更新UI界面
		Platform.runLater(()->{
			ipText.setText("127.0.0.1");
			portText.setText(String.valueOf(PORT));
		});
	}
	
	@Override
	public void run() {
		updateIpAndPort();
		ServerSocket server;
		Socket socket;
		try {
			server = new ServerSocket(PORT);
			while(true) {
				socket = server.accept();
				//一个客户端接入就启动一个handler线程去处理
				new Thread(new handler(map, socket, sendMsgArea, statusText, sendButton, receivedMsgArea, clients, clientListView)).start();
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
 
}