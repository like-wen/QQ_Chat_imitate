package com.lkw.client.Thread;

import com.tool.Message;
import com.tool.Utils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

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
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private Button sendBtn;

	private TextArea sendArea;

	private VBox vBox;

	private Text toolTips;
	ObservableList<String> fileItems;

	int fileNum=0;

	void showMsg(String name,String msg,String time) {
		AnchorPane an = new AnchorPane();
		an.setPrefWidth(891);


		Label nameL = new Label(name);
		nameL.setFont(new Font(13));
		nameL.setTextAlignment(TextAlignment.RIGHT);
		nameL.setMaxWidth(400);
		nameL.setTextFill(Color.web("#0000cd"));

		Label msgL = new Label(msg);
		msgL.setFont(new Font(18));
		msgL.setStyle("-fx-background-color: #fff12f");
		msgL.setPadding(new Insets(5, 10, 5, 10));
		msgL.setWrapText(true);
		msgL.setMaxWidth(400);

		an.getChildren().addAll(nameL,msgL);


		AnchorPane.setRightAnchor(nameL, 46.0);
		AnchorPane.setTopAnchor(nameL, 15.0);

		AnchorPane.setRightAnchor(msgL, 46.0);
		AnchorPane.setTopAnchor(msgL, 45.0);

		AnchorPane timePane = new AnchorPane();
		Label timeL = new Label(time);
		timeL.setFont(new Font(8));
		timeL.setTextAlignment(TextAlignment.RIGHT);
		timeL.setMaxWidth(100);
		timeL.setTextFill(Color.web("#1c1c1c"));
		timePane.setPrefWidth(891.0);
		timePane.getChildren().add(timeL);
		AnchorPane.setRightAnchor(timeL, 48.0);
		AnchorPane.setBottomAnchor(timeL,2.0);


		vBox.getChildren().addAll(an,timePane);
	}
	void showReceiveMsg(String name,String msg,String time){

		AnchorPane an = new AnchorPane();
		an.setPrefWidth(891);


		Label nameL = new Label(name);
		nameL.setFont(new Font(13));
		nameL.setTextAlignment(TextAlignment.LEFT);
		nameL.setMaxWidth(400);
		if("SYSTEM".equals(name))
			nameL.setTextFill(Color.web("#b22222"));
		else
			nameL.setTextFill(Color.web("#8f4789"));


		Label msgL = new Label(msg);
		msgL.setFont(new Font(18));
		msgL.setStyle("-fx-background-color: #ff137f");
		msgL.setPadding(new Insets(5, 10, 5, 10));
		msgL.setWrapText(true);
		msgL.setMaxWidth(400);

		an.getChildren().addAll(nameL,msgL);



		AnchorPane.setLeftAnchor(nameL, 26.0);
		AnchorPane.setTopAnchor(nameL, 15.0);

		AnchorPane.setLeftAnchor(msgL, 26.0);
		AnchorPane.setTopAnchor(msgL, 45.0);

		AnchorPane timePane = new AnchorPane();
		Label timeL = new Label(time);
		timeL.setFont(new Font(8));
		timeL.setTextAlignment(TextAlignment.LEFT);
		timeL.setMaxWidth(100);
		timeL.setTextFill(Color.web("#1c1c1c"));
		timePane.setPrefWidth(891.0);
		timePane.getChildren().add(timeL);
		AnchorPane.setLeftAnchor(timeL, 28.0);
		AnchorPane.setBottomAnchor(timeL,2.0);


		vBox.getChildren().addAll(an,timePane);
	}


	public MyClientThread(Button sendBtn, TextArea sendArea, Text toolTips, String username, ObservableList<String> fileItems,VBox vBox) {
		super();
		this.sendBtn=sendBtn;
		this.sendArea=sendArea;

		this.toolTips=toolTips;
		this.username=username;
		this.fileItems=fileItems;
		this.vBox=vBox;
	}



	@Override
	public void run() {
		try {
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
					sendArea.setText("");


					// acceptArea.appendText(username+" "+sdf.format(new Date())+"\n"+sendMsg+"\n");
					showMsg(username,sendMsg,sdf.format(new Date()));

				}
			});
			selector = Selector.open();
			socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ);

			Message message = new Message(MSG_NAME, username,"",username);
			byte[] bytes = Utils.encode(message);

			//登录
			socketChannel.write(ByteBuffer.wrap(bytes));


			//定时器
			TimerTask timerTask=new TimerTask() {
				@Override
				public void run() {//定时更新文件列表

					System.out.println("发送更新请求");
					Message msg;
					msg=new Message(MSG_GetFileList,username,username,username);
					byte[] bytes1 = new byte[0];
					try {
						bytes1 = Utils.encode(msg);
						socketChannel.write(ByteBuffer.wrap(bytes1));
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
			};
			Timer timer=new Timer();
			timer.scheduleAtFixedRate(timerTask,500l,3000l);//0.5秒后开始,每隔3秒运行


			while (true) {
				//阻塞,检测数据
				selector.select();
				//获取所有数据进行遍历
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					//移出已经检查的数据
					iterator.remove();
					log.info(String.valueOf(message.message));
					//如果可读
					if (key.isReadable()) {
						//获取socket通道->字节缓冲区->String数据
						SocketChannel sc = (SocketChannel) key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						sc.read(buffer);
						message = Utils.decode(buffer.array());

						switch (message.getType()){
							case MSG_GetFileList://收到文件列表更新信息
								String[] fileNameList = message.message.split(";");//分割成字符串数组
								if (fileNameList.length!=fileNum) {
									Platform.runLater(()->{//保护跨线程操作UI组件
										//清理文件列表
										fileItems.clear();
										for (int i = 0; i < fileNameList.length; i++) {
											fileItems.add(fileNameList[i]);//添加文件列表
										}

									});
									fileNum=fileNameList.length;
								}
								break;
							default://文字消息
								// acceptArea.appendText(message.getSendUser()+" "+sdf.format(new Date())+"\n"+message.getMessage()+"\n");
								Message finalMessage = message;
								Platform.runLater(()->{
									showReceiveMsg(finalMessage.getSendUser(), finalMessage.getMessage(),sdf.format(new Date()));
								});

								break;
						}
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