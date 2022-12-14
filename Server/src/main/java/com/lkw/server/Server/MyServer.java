package com.lkw.server.Server;

import com.lkw.server.FileServer.ThreadPoolManager;
import com.lkw.server.Utils.MybatisPlusController;
import com.tool.Message;
import com.tool.PicContent;
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
		try {
			init(8888);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void init(int port) throws IOException{
		// 1.获取一个ServerSocket通道,设置为非阻塞
		// 2.绑定监听，配置TCP参数，例如backlog大小
		// 3.获取selector通道管理器
		// 4.将selector与serverSocketChannel绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,监测接受此通道套接字的连接,只有当该事件到达时,Selector.select()会返回,否则一直阻塞.
		ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);

		ssc.socket().bind(new InetSocketAddress(port));

		selector= Selector.open();

		ssc.register(selector, SelectionKey.OP_ACCEPT);
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
		try {
			listen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listen()  {
		while (true) {
			try{
				// select 方法,没有事件发生,线程阻塞
				// 通过Selector的select（）方法可以选择已经准备就绪的通道 （这些通道包含你感兴趣的的事件）
				// select 在事件未处理时，它不会阻塞, 事件发生后要么处理,要么取消,不能置之不理
				selector.select();
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				// 处理事件, selectedKeys 内部包含了所有发生的事件
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					// 处理key 时,要从 selectedKeys 集合中删除,否则下次处理就会有问题
					iterator.remove();
					// 区分事件类型
					if (key.isAcceptable()) {
						// 客户端的套接字连接 事件
						ServerSocketChannel channel = (ServerSocketChannel) key.channel();
						//在连接到对应的客户端channel后,服务器会创建一个对应的socketchannel与客户端通信
						SocketChannel sc = channel.accept();
						//配置此对应的channel为非阻塞
						sc.configureBlocking(false);
						//注册此channel对 可读 感兴趣, 即 对客户端发送信息时,感兴趣
						sc.register(selector, SelectionKey.OP_READ);

					} else if (key.isReadable()) {
						// 客户端发送信息 事件
						dealReadEvent(key);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 对 发送信息的客户端 对应的channel进行处理
	 * @param key 有 可读事件发生 的key
	 * @throws IOException
	 */
	private void dealReadEvent(SelectionKey key) {
		SocketChannel channel = null;
		try {
			//获取到对应的channel
			channel = (SocketChannel) key.channel();
			//分配 缓冲区 空间

			ByteBuffer bbInt = ByteBuffer.allocate(4);    //读取INT头信息的缓存池
			ByteBuffer bbObj = null;
			//将发送的东西,读取到缓冲区中
			try {
				if (channel.read(bbInt)!= 4){
					// 如果是正常断开，read 的方法的返回值是 -1
					key.cancel();
					return;
				}
				int objLength = bbInt.getInt(0);
				bbObj = ByteBuffer.allocate(objLength);

				int readObj = channel.read(bbObj);

				if(readObj==-1)
				{
					key.cancel();
					return;
				}
				while (readObj != objLength) {
					int read = channel.read(bbObj);
					if(read==-1)
					{// 如果是正常断开，read 的方法的返回值是 -1
						key.cancel();
						return;
					}
					readObj += read;
				}
				bbObj.flip();
			}catch (IOException e)
			{
				//强迫中断
				key.cancel();
				MybatisPlusController.getController().RemoveUser((String) key.attachment());
				System.out.println((key.attachment() == null ? "匿名用户" : key.attachment()) + " 离线了..");
				dealMessage(new Message(MSG_SYSTEM, key.attachment() + " 离线了.."), key, channel);
				ui.updateForDisConnect((String) key.attachment());
				//取消注册
				key.cancel();
				//出现异常,关闭通道,断开与对应客户端的连接
				try {
					channel.close();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
				return;
			}



			Message msg = Utils.decode(bbObj.array());
			log.debug(String.valueOf(msg.getType()));

			dealMessage(msg, key, channel);


		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();

		}
	}

	protected void dealMessage(Message msg, SelectionKey key, SocketChannel channel) {
		switch (msg.getType()) {
			case MSG_NAME:
				key.attach(msg.getContent());
				log.debug("用户{}已上线", msg.getContent());
				ui.updateForConnect((String) msg.getContent());
				// ui.addClients(msg.message);
				getConnectedChannel(channel).forEach(selectionKey -> {
					SocketChannel sc = (SocketChannel) selectionKey.channel();
					sendMsgToClient(new Message( MSG_SYSTEM,"SYSTEM","",Utils.base64decode(String.valueOf(msg.getContent())) + "已上线"), sc);
				});
				break;
			case MSG_GROUP:
				getConnectedChannel(channel).forEach(selectionKey ->{
					SocketChannel sc = (SocketChannel) selectionKey.channel();
							Message message = new Message(MSG_GROUP,msg.getSendUser(),"all", msg.getContent());
							sendMsgToClient(message, sc);
							// System.out.println(message);/
						});
				receivedMsgArea.appendText(sdf.format(new Date())+"  "+key.attachment()+" : "+Utils.base64decode((String) msg.getContent()) + "  " +" \n");
				break;
			case MSG_PRIVATE:
				String[] s = ((String) msg.getContent()).split("_");
				AtomicBoolean flag = new AtomicBoolean(false);
				getConnectedChannel(channel).stream()
						.filter(sk -> s[0].equals(sk.attachment()))
						.forEach(selectionKey ->{
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
				getConnectedChannel(channel).forEach(selectionKey ->
								onlineList.add((String) selectionKey.attachment()));
				sendMsgToClient(new Message(MSG_ONLINE,"SYSTEM","all",String.valueOf(onlineList.size())), channel);
				break;
			case MSG_SYSTEM:
				getConnectedChannel(channel).forEach(selectionKey -> {
					SocketChannel sc = (SocketChannel) selectionKey.channel();
					sendMsgToClient(new Message( MSG_SYSTEM,"SYSTEM","",msg.getContent()), sc);
				});
				break;
			case MSG_GetFileList:
				log.info("发送文件更新信息");
				//Todo:文件表对应
				File file = new File(System.getProperty("user.dir") + "\\MyFile\\");
				File[] files = file.listFiles();
				if(file==null)
					break;
				//拼接成string
				String updateMsg="";
				for (int i = 0; i < files.length-1; i++) {
					updateMsg+=files[i].getName()+";";
				}
				updateMsg+=files[files.length-1].getName();
				sendMsgToClient(new Message( MSG_GetFileList,"SYSTEM",msg.getGetUser(),updateMsg), channel);

				break;
			case MSG_PICTURE:
				System.out.println("<图片>");
				// PicContent picContent = ((PicContent) msg.getContent());
				// picContent.setPicName(Utils.SetFileName(picContent.getPicName()));
				Utils.ToDownload(msg);
				getConnectedChannel(channel).forEach(selectionKey ->{
					SocketChannel sc = (SocketChannel) selectionKey.channel();
					sendMsgToClient(msg,sc);

					// Message message = new Message(MSG_PICTURE,msg.getSendUser(),"all", msg.getContent());
					// sendMsgToClient(message, sc);
					System.out.println(msg.getType());
				});
				receivedMsgArea.appendText(sdf.format(new Date())+"  "+key.attachment()+" : " + "<图片信息>  " +" \n");
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
					sendMsgToClient(new Message( MSG_SYSTEM,"SYSTEM","",Utils.base64encode(sendMsgArea.getText())), sc);//编码
					//显示
					receivedMsgArea.appendText(sdf.format(new Date())+"  SYSTEM"+" : "+sendMsgArea.getText() + "  " +" \n");
				});
		sendMsgArea.clear();

	}

	class UIhandler  {

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
					addListener(ov-> {
						selected.clear();
						for(String key: clientListView.getSelectionModel().getSelectedItems()) {
							selected.add(key);
						}
					});
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
				receivedMsgArea.appendText(remoteSocketAddress + " Connected " + " " + sdf.format(new Date()) + "\n");
				statusText.setText(clients.size() + " Connect success.");
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
				statusText.setText(clients.size() + " Connect success.");
				receivedMsgArea.appendText(remoteSocketAddress + " out of connected.." + "\n");
			});
		}

	}
}
