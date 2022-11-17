package com.lkw.client.Thread;

import com.lkw.client.Utils.Json2Object;
import com.lkw.client.Utils.Message;
import com.lkw.client.Utils.Object2Json;
import com.sun.xml.internal.bind.v2.TODO;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.lkw.client.Utils.FinalValue.*;

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

	private String username;


	public MyClientThread(Button sendBtn, TextArea sendArea, TextArea acceptArea, Text toolTips, String username) {
		super();
		this.sendBtn=sendBtn;
		this.sendArea=sendArea;
		this.acceptArea=acceptArea;
		this.toolTips=toolTips;
		this.username=username;
	}

	@Override
	public void run() {
		try {
			socket = new Socket("127.0.0.1", 9999);

			//socket的输入/输出,直接转换为对象
			ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

			//socket的输入输出
			// BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//
			// PrintWriter pWriter = new PrintWriter(socket.getOutputStream());

			//"发送"按钮的点击事件
			sendBtn.setOnAction(e->{
				String sendMsg=sendArea.getText();
				if(sendMsg.equals("")){
					toolTips.setText("没有内容");
				}else {

					//形式
					String json = Object2Json.creat(username, "text").addObject("text", sendMsg).buildJson();
					// Todo 判定群发以及私发
					//暂时为群发
					Message message = new Message(1, sendMsg);
					//使用发送对象的流发送消息
					try {
						objectOut.writeObject(message);
						objectOut.flush();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}

					// pWriter.write( message + "\r\n");
					// pWriter.flush();
					sendArea.setText("");
				}
			});

			//发消息
			// String json;

			// //收消息
			// while(true) {
			// 	// json = bReader.readLine();
			// 	String msg = bReader.readLine();
			// 	//解析json
			// 	Json2Object json2Object = new Json2Object(msg);
			// 	//判断mode
			// 	if(json2Object.getType()==MSG_GROUP||json2Object.getType()==MSG_PRIVATE){
			// 		String message = json2Object.getMessage();
			// 		acceptArea.appendText(message);
			// 		System.out.println("收到"+message);
			// 	}else if(false){//其他情况
			// 		//TODO 编写其他mode的
			// 	}

				//使用读取对象中的流 来获取消息
				while(true) {
					// json = bReader.readLine();
					Message msg = (Message)objectIn.readObject();

					if (msg.getType()==MSG_GROUP|| msg.getType()==MSG_PRIVATE||msg.getType()==MSG_SYSTEM)
					{
						String message = msg.getMessage();
						acceptArea.appendText(message);
						System.out.println(message);
					}else if(false){
						//TODO 编写其他type的
					}



				// if(json2Object.getMode().equals("text")){
				// 	String message = json2Object.Json2Text();
				// 	acceptArea.appendText(message);
				// 	System.out.println("收到"+message);
				// }else if(false){//其他情况
				//
				// }
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
