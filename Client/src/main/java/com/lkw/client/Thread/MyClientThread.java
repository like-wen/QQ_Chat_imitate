package com.lkw.client.Thread;

import com.tool.Message;
import com.tool.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
	private ScrollPane scroll;

	//页面自动滚动逻辑的判定
	private boolean updateFlag = false;

	private Text toolTips;
	private ImageView ic;
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
		AnchorPane.setRightAnchor(timeL, 46.0);
		AnchorPane.setBottomAnchor(timeL,2.0);


		vBox.getChildren().addAll(an,timePane);
		updateFlag = true;
	}

	/**
	 * 展示接收的信息
	 * @param name
	 * @param msg
	 * @param time
	 */
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


		//base64去盐解码
		log.info("将"+msg+"解密");
		Label msgL = new Label(Utils.base64decode(msg));
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
		updateFlag = true;
	}



	void showPic(String name,Object pic,String time){
		AnchorPane an = new AnchorPane();
		an.setPrefWidth(891);


		Label nameL = new Label(name);
		nameL.setFont(new Font(13));
		nameL.setTextAlignment(TextAlignment.RIGHT);
		nameL.setMaxWidth(400);
		nameL.setTextFill(Color.web("#0000cd"));
		FileInputStream fileInputStream=null;
		try {
			fileInputStream = new FileInputStream((File) pic);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ImageView iv = new ImageView(new Image(fileInputStream));
		// ImageView iv = new ImageView(new Image("/QQ.png"));
		iv.setPreserveRatio(true);

		double iWidth = iv.getImage().getWidth();


		if(iWidth>891.0*0.4)
		{
			iv.setFitWidth(891.0*0.4);
		}


		an.getChildren().addAll(nameL,iv);


		AnchorPane.setRightAnchor(nameL, 46.0);
		AnchorPane.setTopAnchor(nameL, 15.0);

		AnchorPane.setRightAnchor(iv, 46.0);
		AnchorPane.setTopAnchor(iv, 45.0);

		AnchorPane timePane = new AnchorPane();
		Label timeL = new Label(time);
		timeL.setFont(new Font(8));
		timeL.setTextAlignment(TextAlignment.RIGHT);
		timeL.setMaxWidth(100);
		timeL.setTextFill(Color.web("#1c1c1c"));
		timePane.setPrefWidth(891.0);
		timePane.getChildren().add(timeL);
		AnchorPane.setRightAnchor(timeL, 46.0);
		AnchorPane.setBottomAnchor(timeL,2.0);


		vBox.getChildren().addAll(an,timePane);
		updateFlag = true;
	}
	void showReceivePic(String name,Object pic,String time){
		AnchorPane an = new AnchorPane();
		an.setPrefWidth(891);


		Label nameL = new Label(name);
		nameL.setFont(new Font(13));
		nameL.setTextAlignment(TextAlignment.RIGHT);
		nameL.setMaxWidth(400);
		nameL.setTextFill(Color.web("#0000cd"));
		FileInputStream fileInputStream=null;
		try {
			fileInputStream = new FileInputStream((File) pic);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ImageView iv = new ImageView(new Image(fileInputStream));
		// ImageView iv = new ImageView(new Image("/QQ.png"));
		iv.setPreserveRatio(true);

		double iWidth = iv.getImage().getWidth();


		if(iWidth>891.0*0.4)
		{
			iv.setFitWidth(891.0*0.4);
		}


		an.getChildren().addAll(nameL,iv);


		AnchorPane.setLeftAnchor(nameL, 26.0);
		AnchorPane.setTopAnchor(nameL, 15.0);

		AnchorPane.setLeftAnchor(iv, 46.0);
		AnchorPane.setTopAnchor(iv, 45.0);

		AnchorPane timePane = new AnchorPane();
		Label timeL = new Label(time);
		timeL.setFont(new Font(8));
		timeL.setTextAlignment(TextAlignment.RIGHT);
		timeL.setMaxWidth(100);
		timeL.setTextFill(Color.web("#1c1c1c"));
		timePane.setPrefWidth(891.0);
		timePane.getChildren().add(timeL);
		AnchorPane.setLeftAnchor(timeL, 26.0);
		AnchorPane.setBottomAnchor(timeL,2.0);
		vBox.getChildren().addAll(an,timePane);
		updateFlag = true;
	}

	// Todo:缓存图片到本地
	void downloadPic(Message PicMsg){
		if(PicMsg.getType()==MSG_PICTURE)
		{

		}
	}
	public MyClientThread(Button sendBtn, TextArea sendArea, Text toolTips, String username, ObservableList<String> fileItems, VBox vBox, ImageView ic, ScrollPane scrollPane) {
		super();
		this.sendBtn=sendBtn;
		this.sendArea=sendArea;
		this.ic=ic;
		this.toolTips=toolTips;
		this.username=username;
		this.fileItems=fileItems;
		this.vBox=vBox;
		this.scroll=scrollPane;
	}



	@Override
	public void run() {
		try {

			//"消息界面"的自动滚动,根据updateFlag进行
			scroll.vvalueProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					if(updateFlag) {
						scroll.setVvalue(1.0);
						updateFlag = false;
					}
				}
			});

			ic.setImage(new Image("/QQ.png"));
			//"发送图片"对点击事件的处理,发送图片信息
			ic.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("选择发送图片");
					fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
					FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Images Only","*.jpg","*.png","*.jpeg","*.gif");
					fileChooser.getExtensionFilters().add(filter);
					fileChooser.setSelectedExtensionFilter(filter);
					File file = fileChooser.showOpenDialog(new Stage());

					if(file==null)
						return;
					ImageView imageView = null;
					try {
						FileInputStream fileInputStream = new FileInputStream(file);
						imageView = new ImageView(new Image(fileInputStream));

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					showPic(username,file,sdf.format(new Date()));

					//发送到聊天系统中
					Message PicMsg = new Message(MSG_PICTURE, username, "", file);


					try {
						byte[] bytes1 = Utils.encode(PicMsg);
						socketChannel.write(ByteBuffer.wrap(bytes1));
					} catch (IOException e) {
						e.printStackTrace();
					}


				}
			});


			//"发送"按钮的点击事件,即发送文字信息
			sendBtn.setOnAction(e->{
				String sendMsg=sendArea.getText();
				//带盐的base64加密
				String sendBase64Msg = Utils.base64encode(sendMsg);
				log.info("信息加密为"+sendBase64Msg);
				if(sendMsg.equals("")){
					toolTips.setText("没有内容");
				}else {

					//Todo 判断是群发 还是 私发

					Message msg;
					boolean isPrivate = false;
					if (isPrivate) {
						msg = new Message(MSG_PRIVATE, username, "", sendBase64Msg);
					}
					else {
						msg = new Message(MSG_GROUP, username,"all",sendBase64Msg);
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

			//1.先使用Selector的open()方法,创建selector
			//2.使用SocketChannel的open()方法,连接到指定位置,返回连接的socketchannel
			//3.配置socketchannel为阻塞
			//4.为这个socketchannel,注册为 对读 感兴趣,即 能通过 selector 获取 服务器传过来的消息
			selector = Selector.open();
			socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ);

			//登录步骤
			// 1.先创建指定类型的消息类型
			// 2.对消息进行编码
			// 3.然后通过socketchannel的write方法将消息发给服务端
			Message message = new Message(MSG_NAME, username,"",username);
			byte[] bytes = Utils.encode(message);
			socketChannel.write(ByteBuffer.wrap(bytes));


			//主线程处于死循环中,处理服务器发送消息
			while (true) {
				//阻塞函数,检测是否有数据进行处理
				selector.select();
				//获取所有数据进行遍历
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					//将此次检查的数据,从selector中移除(因为是迭代器中深拷贝),避免让下次处理出现混乱
					iterator.remove();
					log.info(String.valueOf(message.content));
					//如果有可读事件,则表明服务器端向客户端发送消息
					if (key.isReadable()) {

						//1.获取此可读的 socketchannel
						//2.分配字节缓冲区,将发送过来的东西,读取到缓冲区
						//3.缓冲区进行解码

						SocketChannel sc = (SocketChannel) key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(4000);
						sc.read(buffer);
						message = Utils.decode(buffer.array());

						//4.根据发送过来的消息类型 进行判定
						switch (message.getType()){
							case MSG_GetFileList://收到文件列表更新信息
								String[] fileNameList = ((String)message.content).split(";");//分割成字符串数组
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
							case MSG_PICTURE:
								Message picMessage = message;
								Platform.runLater(()->{
									showReceivePic(picMessage.getSendUser(),picMessage.getContent(),sdf.format(new Date()));
								});
								break;
							default://文字消息

								Message finalMessage = message;
								Platform.runLater(()->{
									showReceiveMsg(finalMessage.getSendUser(), (String) finalMessage.getContent(),sdf.format(new Date()));
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
