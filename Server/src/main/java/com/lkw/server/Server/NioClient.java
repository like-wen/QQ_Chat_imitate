package com.lkw.server.Server;



import com.tool.Message;
import com.tool.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

import static com.tool.FinalValue.*;


/**
 * @author mazouri
 * @create 2021-04-29 12:02
 */
@Slf4j
public class NioClient {
	private Selector selector;
	private SocketChannel socketChannel;
	private String username;
	private static Scanner input;

	public NioClient() throws IOException {
		selector = Selector.open();
		socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
		log. info("client启动完成......");
		log. info("请输入你的名字完成注册");
		input = new Scanner(System.in);
		username = input.next();
		log. info("欢迎{}来到聊天系统", username);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("tips: \n" +
				"1. 直接发送消息会发给当前的所有用户 \n" +
				"2. @用户名:消息  会私发给你要发送的用户 \n" +
				"3. 输入  查询在线用户  会显示当前的在线用户");
		NioClient client = new NioClient();

		//启动一个子线程接受服务器发送过来的消息
		new Thread(() -> {
			try {
				client.acceptMessageFromServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "receiveClientThread").start();

		//调用sendMessageToServer,发送消息到服务端
		client.sendMessageToServer();
	}

	/**
	 * 将消息发送到服务端
	 *
	 * @throws IOException
	 */
	private void sendMessageToServer() throws IOException {
		//先把用户名发给客户端
		Message message = new Message(MSG_NAME, username);
		byte[] bytes = Utils.encode(message);

		socketChannel.write(ByteBuffer.wrap(bytes));
		while (input.hasNextLine()) {
			String msgStr = input.next();

			Message msg;
			boolean isPrivate = msgStr.startsWith("@");
			if (isPrivate) {
				int idx = msgStr.indexOf(":");
				String targetName = msgStr.substring(1, idx);
				msgStr = msgStr.substring(idx + 1);
				msg = new Message(MSG_PRIVATE, targetName + "_" + msgStr);
			} else if ("查询在线用户".equals(msgStr)) {
				msg = new Message(MSG_ONLINE, "请求在线人数");
			} else {
				msg = new Message(MSG_GROUP, msgStr);
			}

			byte[] bytes1 = Utils.encode(msg);
			socketChannel.write(ByteBuffer.wrap(bytes1));
		}

		System.out.println("over1");
	}

	/**
	 * 接受从服务器发送过来的消息
	 */
	private void acceptMessageFromServer() throws Exception {
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
					Message message = Utils.decode(buffer.array());
					log. info(String.valueOf(message.message));
				}
			}
		}

	}
}

