package com.lkw.client.Thread;

import com.lkw.client.Utils.MyObjectOutputStream;
import com.lkw.client.Utils.Object2Json;
import com.tool.Message;
import com.tool.Utils;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

import static com.tool.FinalValue.*;


/**
 * 客户端
 * @author 花大侠
 *
 */
@Slf4j
public class MyClientThread implements Runnable {

	private Selector selector;
	private SocketChannel socketChannel;
	private String username;

	private Socket socket;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private Button sendBtn;

	private TextArea sendArea;
	private TextArea acceptArea;

	private Text toolTips;




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
			// socket = new Socket("127.0.0.1", 8888);
			//socket的输入/输出,直接转换为对象

			// ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

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

					//Todo 判断是群发 还是 私发

					Message msg;
					boolean isPrivate = false;
					if (isPrivate) {
						 msg = new Message(MSG_PRIVATE, username, "", sendMsg);

					}
					 else {
						msg = new Message(MSG_GROUP, username,"all",sendMsg);
					}

					byte[] bytes1 = new byte[0];
					try {
						bytes1 = Utils.encode(msg);
						socketChannel.write(ByteBuffer.wrap(bytes1));
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}




					//形式
					// String json = Object2Json.creat(username, "text").addObject("text", sendMsg).buildJson();
					// // Todo 判定群发以及私发
					// //暂时为群发
					// Message message = new Message(1, sendMsg);
					// //使用发送对象的流发送消息
					// try {
					// 	objectOut.writeObject(message);
					// 	// objectOut.flush();
					// } catch (IOException ioException) {
					// 	ioException.printStackTrace();
					// }

					// pWriter.write( message + "\r\n");
					// pWriter.flush();
					sendArea.setText("");
					acceptArea.appendText(username+" "+sdf.format(new Date())+"\n"+sendMsg+"\n");
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
				// while(true) {
					// json = bReader.readLine();
					// ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
					// Message msg = (Message)objectIn.readObject();
					// Thread.sleep(1000);
					// if (msg.getType()==MSG_GROUP|| msg.getType()==MSG_PRIVATE||msg.getType()==MSG_SYSTEM)
					// {
					// 	String message = msg.getMessage();
					// 	acceptArea.appendText(message);
					// 	System.out.println(message);
					// }else if(false){
					// 	//TODO 编写其他type的
					// }



				// if(json2Object.getMode().equals("text")){
				// 	String message = json2Object.Json2Text();
				// 	acceptArea.appendText(message);
				// 	System.out.println("收到"+message);
				// }else if(false){//其他情况
				//
				// }

					selector = Selector.open();
					socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
					socketChannel.configureBlocking(false);
					socketChannel.register(selector, SelectionKey.OP_READ);

					Message message = new Message(MSG_NAME, username,"",username);
					byte[] bytes = Utils.encode(message);

					socketChannel.write(ByteBuffer.wrap(bytes));
					while (true) {
						selector.select();
						Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
						while (iterator.hasNext()) {
							SelectionKey key = iterator.next();
							iterator.remove();
							if (key.isReadable()) {
								SocketChannel sc = (SocketChannel) key.channel();
								ByteBuffer buffer = ByteBuffer.allocate(1024);
								sc.read(buffer);
								message = Utils.decode(buffer.array());
								log. info(String.valueOf(message.message));

								acceptArea.appendText(message.getSendUser()+" "+sdf.format(new Date())+"\n"+message.getMessage()+"\n");


							}
						}
					}


		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
