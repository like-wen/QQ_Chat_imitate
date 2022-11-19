package com.lkw.client.Thread;

import com.tool.Message;
import com.tool.Utils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
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
	private TextArea acceptArea;

	private Text toolTips;
	ObservableList<String> fileItems;

	int fileNum=0;





	public MyClientThread(Button sendBtn, TextArea sendArea, TextArea acceptArea, Text toolTips, String username, ObservableList<String> fileItems) {
		super();
		this.sendBtn=sendBtn;
		this.sendArea=sendArea;
		this.acceptArea=acceptArea;
		this.toolTips=toolTips;
		this.username=username;
		this.fileItems=fileItems;

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
					acceptArea.appendText(username+" "+sdf.format(new Date())+"\n"+sendMsg+"\n");
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
			timer.scheduleAtFixedRate(timerTask,500l,3000l);//1秒后,每隔3秒运行




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
										System.out.println("客户端收到更新消息"+message.message);
										String[] fileNameList = message.message.split(";");//分割成字符串数组
										for (int i = 0; i < fileNameList.length; i++) {//测试输出
											System.out.println(fileNameList[i]);
										}
										if (fileNameList.length!=fileNum) {

											System.out.println("判断更新");
											Platform.runLater(()->{//保护跨线程操作UI组件

											fileItems.clear();
											for (int i = 0; i < fileNameList.length; i++) {
												fileItems.add(fileNameList[i]);
											}

											});

											fileNum=fileNameList.length;
										}
										break;
									default://文字消息
										acceptArea.appendText(message.getSendUser()+" "+sdf.format(new Date())+"\n"+message.getMessage()+"\n");
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
