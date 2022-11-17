package com.lkw.server.Server;

import com.tool.Message;
import com.tool.Utils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.tool.FinalValue.*;


@Slf4j
public class MyServer implements Runnable {

	private Selector selector;
	private ServerSocketChannel ssc;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	private UIhandler ui;

	//Server端监听的端口号
	public static final int PORT = 9999;
	//映射表 存放每个socket地址(IP:Port)及其对应的PrintWriter
	//为群发消息做准备

	//存放已连接socket地址(IP:Port)，用于clientListView
	ObservableList<String> clients;

	ListView<String> clientListView;

	TextField ipText;
	TextField portText;
	TextArea sendMsgArea;
	TextField statusText;
	Button sendButton;
	TextArea receivedMsgArea;

	public void init(int port) throws IOException{
		//1.获取一个ServerSocket通道
		ssc = ServerSocketChannel.open();
		// System.out.println(ssc.isBlocking());
		ssc.configureBlocking(false);////设置为非阻塞
		// System.out.println(ssc.isBlocking());
		//2.绑定监听，配置TCP参数，例如backlog大小
		ssc.socket().bind(new InetSocketAddress(port));
		//3.获取通道管理器
		selector= Selector.open();
		//将通道管理器与通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
		//只有当该事件到达时，Selector.select()会返回，否则一直阻塞。
		ssc.register(selector, SelectionKey.OP_ACCEPT);//注册channel到selector,监测接受此通道套接字的连接
	}

	public MyServer() {
		try {
			init(8888);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MyServer(TextField ipText, TextField portText, TextArea sendMsgArea, TextField statusText,
			Button sendButton, TextArea receivedMsgArea, ObservableList<String> clients, ListView<String> clientListView) {
		this();
		this.ui=new UIhandler(sendMsgArea,statusText,sendButton,receivedMsgArea,clients,clientListView);
		this.ipText = ipText;
		this.portText = portText;
		this.sendMsgArea = sendMsgArea;
		this.statusText = statusText;
		this.sendButton = sendButton;
		this.sendButton.setOnAction(event -> ToSelected());
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
		// updateIpAndPort();
		// ServerSocket server;
		// Socket socket;
		// try {
		// 	server = new ServerSocket(PORT);
		// 	while(true) {
		// 		socket = server.accept();
		// 		//一个客户端接入就启动一个handler线程去处理
		// 		new Thread(new UIhandler(map, socket, sendMsgArea, statusText, sendButton, receivedMsgArea, clients, clientListView)).start();
		// 	}
		// } catch (IOException e) {
		// 	// TODO 自动生成的 catch 块
		// 	e.printStackTrace();
		// }

		try {
			listen();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	private void listen() throws Exception {
		boolean first=true;
		while (true) {
			// select 方法, 没有事件发生，线程阻塞，有事件，线程才会恢复运行, 通过Selector的select（）方法可以选择已经准备就绪的通道 （这些通道包含你感兴趣的的事件）
			//通过Selector的select（）方法可以选择已经准备就绪的通道 （这些通道包含你感兴趣的的事件）
			// select 在事件未处理时，它不会阻塞, 事件发生后要么处理，要么取消，不能置之不理
			selector.select();
			// 处理事件, selectedKeys 内部包含了所有发生的事件
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				// 处理key 时，要从 selectedKeys 集合中删除，否则下次处理就会有问题
				iterator.remove();

				// 区分事件类型
				if (key.isAcceptable()) {
					ServerSocketChannel channel = (ServerSocketChannel) key.channel();

					SocketChannel sc = channel.accept();

					sc.configureBlocking(false);

					sc.register(selector, SelectionKey.OP_READ);

				} else if (key.isReadable()) {
					//客户端是否发送信息

						dealReadEvent(key);



				}
			}
		}
	}

	private void dealReadEvent(SelectionKey key) throws IOException {
		SocketChannel channel = null;
		try {
			channel = (SocketChannel) key.channel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			int read = channel.read(buffer);

			// 如果是正常断开，read 的方法的返回值是 -1
			if (read == -1) {
				//cancel 会取消注册在 selector 上的 channel，并从 keys 集合中删除 key 后续不会再监听事件
				key.cancel();
			} else if(read>0) {
				buffer.flip();
				System.out.println(new String(buffer.array()));
				Message msg =  Utils.decode(buffer.array());
				log.debug(msg.toString());
				System.out.println("SSS");
				dealMessage(msg, key, channel);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println((key.attachment() == null ? "匿名用户" : key.attachment()) + " 离线了..");
			dealMessage(new Message(MSG_SYSTEM, key.attachment() + " 离线了.."), key, channel);
			ui.updateForDisConnect((String) key.attachment());
			//取消注册
			key.cancel();

			//关闭通道
			try {
				channel.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	protected void dealMessage(Message msg, SelectionKey key, SocketChannel channel) {
		switch (msg.type) {
			case MSG_NAME:
				key.attach(msg.message);
				log.debug("用户{}已上线", msg.message);
				ui.updateForConnect(msg.message);

				// ui.addClients(msg.message);
				getConnectedChannel(channel).forEach(
						selectionKey -> {
					SocketChannel sc = (SocketChannel) selectionKey.channel();
					sendMsgToClient(new Message( MSG_SYSTEM,"SYSTEM","",msg.message + "已上线"), sc);
				});
				break;
			case MSG_GROUP:
				getConnectedChannel(channel).forEach(
						selectionKey ->
						{
					SocketChannel sc = (SocketChannel) selectionKey.channel();
							Message message = new Message(MSG_GROUP,msg.getSendUser(),"all", msg.message);
							sendMsgToClient(message, sc);
							System.out.println(message);
						});
				receivedMsgArea.appendText(key.attachment()+" : "+msg.message + "  " +sdf.format(new Date())+" \n");
				break;
			case MSG_PRIVATE:
				String[] s = msg.message.split("_");
				AtomicBoolean flag = new AtomicBoolean(false);
				getConnectedChannel(channel).stream()
						.filter(sk -> s[0].equals(sk.attachment()))
						.forEach(selectionKey ->
						{
					SocketChannel sc = (SocketChannel) selectionKey.channel();
					sendMsgToClient(new Message(MSG_PRIVATE,key.attachment() + "给你发送了一条消息: " + s[1]), sc);
					flag.set(true);
				});
				if (!flag.get()){
					sendMsgToClient(new Message(s[1]+"用户不存在,请重新输入！！！"), channel);
				}
				break;
			case MSG_ONLINE:
				ArrayList<String> onlineList = new ArrayList<>();
				onlineList.add((String) key.attachment());
				getConnectedChannel(channel).forEach(
						selectionKey ->
								onlineList.add((String) selectionKey.attachment()));
				sendMsgToClient(new Message(onlineList.toString()), channel);

				break;
			case MSG_SYSTEM:
				getConnectedChannel(channel).forEach(selectionKey -> {
					SocketChannel sc = (SocketChannel) selectionKey.channel();
					sendMsgToClient(new Message( MSG_SYSTEM,"SYSTEM","",msg.message), sc);
				});
				break;
			default:
				break;
		}
	}

	protected void sendMsgToClient(Message msg, SocketChannel sc) {
		try {
			byte[] bytes =  Utils.encode(msg);
			sc.write(ByteBuffer.wrap(bytes));
		} catch (IOException e) {
			log.debug("sendMsgToClient出现了一些问题");
		}
	}

	private Set<SelectionKey> getConnectedChannel(SocketChannel channel) {
		return selector.keys().stream()//流
				.filter(item -> item.channel() instanceof SocketChannel && item.channel().isOpen() && item.channel() != channel)
				//留下 除 调用者外的 打开的 socketchannel
		.collect(Collectors.toSet());//汇集
	}
	private void ToSelected(){
		if(ui.getSelected().size()<=0)
			return;
		List<SelectionKey> collect = selector.keys().stream()
				.filter(i -> ui.getSelected().contains((String) i.attachment()))
				.collect(Collectors.toList());
		int size = collect.size();
		System.out.println("给几个同名发 "+size);
		collect.forEach(sk ->
				{
					SocketChannel sc = (SocketChannel) sk.channel();
					sendMsgToClient(new Message( MSG_SYSTEM,"SYSTEM","",sendMsgArea.getText()), sc);

				});
		sendMsgArea.clear();

	}

	class UIhandler  {
		public void addClients(String name) {
			this.clients.add(name);
		}

		public Set<String> getSelected() {
			return selected;
		}

		// SocketChannel socket;
		TextArea sendMsgArea;
		TextField statusText;
		Button sendButton;
		TextArea receivedMsgArea;
		ObservableList<String> clients;
		ListView<String> clientListView;

		Set<String> selected = new HashSet<>();

		public UIhandler() {



		}

		public UIhandler (TextArea sendMsgArea, TextField statusText, Button sendButton,
					   TextArea receivedMsgArea, ObservableList<String> clients, ListView<String> clientListView) {


			//存取 socket, soncket的输出流
			this.sendMsgArea = sendMsgArea;
			this.statusText = statusText;
			this.sendButton = sendButton;
			this.receivedMsgArea = receivedMsgArea;
			this.clients = clients;
			this.clientListView = clientListView;
			clientListView.getSelectionModel().
					selectedItemProperty().
					addListener(ov->
					{ 	selected.clear();
						for(String key: clientListView.getSelectionModel().getSelectedItems()) {
							selected.add(key);
						}
					});
		}

		// @Override
		// public void run() {
		// 	String remoteSocketAddress = null;
		// 	try {
		// 		remoteSocketAddress = socket.getRemoteAddress().toString().substring(1);
		// 	} catch (IOException e) {
		// 		e.printStackTrace();
		// 	}
		// 	updateForConnect(remoteSocketAddress);
		// 	try {
		//
		//
		// 		InputStream in = socket.getInputStream();
		// 		BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
		// 		OutputStream out = socket.getOutputStream();
		// 		PrintWriter pWriter = new PrintWriter(out);
		// 		map.put(remoteSocketAddress, pWriter);
		// 		//发消息
		// 		sendMessage();
		// 		//收消息
		// 		String json;
		// 		while(true) {
		// 			json = bReader.readLine();
		// 			//个人响应
		// 			System.out.println("收到"+json);
		//
		// 			//解析json
		// 			Json2Object json2Object = new Json2Object(json);
		// 			//判断mode
		// 			if(json2Object.getMode().equals("login")){
		// 				String returnJson = json2Object.Json2PasswordCheck();
		// 				System.out.println("发送"+returnJson);
		// 				pWriter.write(returnJson);
		// 				pWriter.flush();
		// 			}else if(false){//其他情况
		// 				//TODO 编写其他mode的
		// 			}
		//
		//
		// 			//群转发
		// 			//][]\]==
		// 			// sendMessage2All(json,pWriter);
		//
		//
		// 			receivedMsgArea.appendText(json + "\n");
		// 		}
		// 	} catch (IOException e) {
		// 		updateForDisConnect(remoteSocketAddress);
		// 	}
		// }

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
				receivedMsgArea.appendText(String.valueOf(remoteSocketAddress + " Connected " + " " + sdf.format(new Date()) + "\n"));
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
			});
		}

		/**
		 * 给服务器选择的客户端进行发送
		 * 1.为clientListView设置监听器
		 *   1.1获取已选择的项(IP:Port)
		 *   1.2从映射表中取出对应printWriter放入printWriters集合
		 * 2.为sendButton设置鼠标点击事件
		 *   2.1遍历printWriters集合
		 *   2.2写入待发送的消息
		 */

		public void sendMessage() {
			Set<PrintWriter> printWriters = new HashSet<>();

			clientListView.getSelectionModel().
					selectedItemProperty().
					addListener(ov->
					{ 	printWriters.clear();
						for(String key: clientListView.getSelectionModel().getSelectedItems()) {

				}
			});

			sendButton.setOnAction(e->{
				for (PrintWriter printWriter : printWriters) {
					printWriter.write(sendMsgArea.getText() + "\r\n");
					printWriter.flush();
				}
			});
		}


		// /**
		//  * 单独发送以及群发
		//  * @param message
		//  * @param pWriter
		//  */
		// public void sendMessage2All(String message, PrintWriter pWriter){
		// 	for (PrintWriter printWriter:map.values()){
		// 		if(!pWriter.equals(printWriter)) {
		// 			printWriter.write(message);
		// 			printWriter.flush();
		// 		}
		// 	}
		//
		// }






	}
}
