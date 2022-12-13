package com.tool;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Base64;

/**
 * @author mazouri
 * @create 2021-05-09 22:26
 */
@Slf4j
public class Utils {

	private static final String SALT = "MySaLt";

	private static final int REPEAT = 3;
	/**
	 * 将二进制数据转为对象
	 *
	 * @param buf
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Message decode(byte[] buf) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bas = new ByteArrayInputStream(buf);
		ObjectInputStream ois = new ObjectInputStream(bas);
		return (Message) ois.readObject();
	}

	/**
	 * 将对象转为二进制数据
	 *
	 * @param message
	 * @return
	 */
	public static byte[] encode(Message message) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(message);
		oos.flush();
		return baos.toByteArray();
	}


	public static String base64encode(String str) {
		// 加盐处理
		String temp = str + "{" + SALT + "}";
		byte data[] = temp.getBytes();
		for (int i = 0; i < REPEAT; i++) {
			// 重复加密

			data = Base64.getEncoder().encode(data);
		}
		return new String(data);
	}


	public static String base64decode(String str) {
		// 获取加密的内容
		byte data[] = str.getBytes();
		for (int i = 0; i < REPEAT; i++) {
			// 多次解密
			data = Base64.getDecoder().decode(data);
		}
		// 删除盐值格式
		return new String(data).replaceAll("\\{\\w+\\}", "");
	}



}

