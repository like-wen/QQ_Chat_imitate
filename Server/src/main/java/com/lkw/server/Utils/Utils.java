package com.lkw.server.Utils;

import lombok.extern.slf4j.Slf4j;
import com.lkw.server.Utils.Message;
import java.io.*;

/**
 * @author mazouri
 * @create 2021-05-09 22:26
 */
@Slf4j
public class Utils {
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
}

