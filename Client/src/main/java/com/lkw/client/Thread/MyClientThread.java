package com.lkw.client.Thread;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 客户端
 * @author 花大侠
 *
 */
public class MyClientThread implements Runnable {

	private Socket socket;

	private Button sendBtn;

	private TextArea sendArea;
	private TextArea acceptArea;

	private Text toolTips;


	public MyClientThread(Button sendBtn, TextArea sendArea, TextArea acceptArea, Text toolTips) {
		super();
		this.sendBtn=sendBtn;
		this.sendArea=sendArea;
		this.acceptArea=acceptArea;
		this.toolTips=toolTips;
	}

	@Override
	public void run() {
		try {
			socket = new Socket("127.0.0.1", 9999);


			//socket的输入输出
			BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pWriter = new PrintWriter(socket.getOutputStream());

			sendBtn.setOnAction(e->{

				if(sendArea.getText().equals("")){
					toolTips.setText("没有内容");

				}else {
					pWriter.write(sendArea.getText() + "\r\n");
					pWriter.flush();
					sendArea.setText("");
				}
			});



			//发消息
			String message;
			//收消息
			while(true) {
				message = bReader.readLine();
				acceptArea.appendText(message);
				System.out.println("收到"+message);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}