package com.lkw.server;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 处理线程
 * @author 花大侠
 *
 */
public class handler implements Runnable {
	
	Socket socket;
	TextArea sendMsgArea;
	TextField statusText;
	Button sendButton;
	TextArea receivedMsgArea;
	ObservableList<String> clients;
	ListView<String> clientListView;
	Map<String, PrintWriter> map;
	
	public handler() {
		super();
	}
 
	public handler(Map<String, PrintWriter> map, Socket socket, TextArea sendMsgArea, TextField statusText, Button sendButton,
                   TextArea receivedMsgArea, ObservableList<String> clients, ListView<String> clientListView) {
		super();
		this.map = map;
		this.socket = socket;
		this.sendMsgArea = sendMsgArea;
		this.statusText = statusText;
		this.sendButton = sendButton;
		this.receivedMsgArea = receivedMsgArea;
		this.clients = clients;
		this.clientListView = clientListView;
	}
	
	/**
	 * 接入客户端后，更新UI界面
	 * 1.添加新接入客户端的地址信息
	 * 2.receivedMsgarea打印成功连接信息
	 * 3.statusText更新成功连接个数
	 */
	public void updateForConnect(String remoteSocketAddress) {
		Platform.runLater(()->{
			clients.add(remoteSocketAddress);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			receivedMsgArea.appendText(String.valueOf(clients.size()) + " Connected from " + remoteSocketAddress + " " + sdf.format(new Date()) + "\n");
			statusText.setText(String.valueOf(clients.size()) + " Connect success.");
		});
	}
	
	/**
	 * 断开客户端后，更新UI界面
	 * 1.移除断开客户端的地址信息
	 * 2.receivedMsgarea打印断开连接信息
	 * 3.statusText更新成功连接个数
	 * 4.移除map中对应的remoteSocketAddress
	 */
	public void updateForDisConnect(String remoteSocketAddress) {
		Platform.runLater(()->{
			clients.remove(remoteSocketAddress);
			statusText.setText(String.valueOf(clients.size()) + " Connect success.");
			receivedMsgArea.appendText(remoteSocketAddress + " out of connected.." + "\n");
			map.remove(remoteSocketAddress);
		});
	}
	
	/**
	 * 单发及群发消息
	 * 1.为clientListView设置监听器 
	 *   1.1获取已选择的项(IP:Port)
	 *   1.2从映射表中取出对应printWriter放入printWriters集合
	 * 2.为sendButton设置鼠标点击事件
	 *   2.1遍历printWriters集合
	 *   2.2写入待发送的消息
	 */
	public void sendMessage() {
		Set<PrintWriter> printWriters = new HashSet<>();
		clientListView.getSelectionModel().selectedItemProperty().addListener(ov->{
			printWriters.clear();
			for(String key: clientListView.getSelectionModel().getSelectedItems()) {
				printWriters.add(map.get(key));
			}
		});
		sendButton.setOnAction(e->{
			for (PrintWriter printWriter : printWriters) {
				printWriter.write("127.0.0.1:9999" + "  " + sendMsgArea.getText() + "\r\n");
				printWriter.flush();
			}
		});
	}
	
	@Override
	public void run() {
		String remoteSocketAddress = socket.getRemoteSocketAddress().toString().substring(1);
		updateForConnect(remoteSocketAddress);
		try {
			InputStream in = socket.getInputStream();
			BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
			OutputStream out = socket.getOutputStream();
			PrintWriter pWriter = new PrintWriter(out);
			map.put(remoteSocketAddress, pWriter);
			//发消息
			sendMessage();
			//收消息
			String message;
			while(true) {
				message = bReader.readLine();
				receivedMsgArea.appendText(message + "\n");
			}
		} catch (IOException e) {
			updateForDisConnect(remoteSocketAddress);
		}
	}
 
}