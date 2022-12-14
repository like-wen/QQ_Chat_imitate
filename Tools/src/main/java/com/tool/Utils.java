package com.tool;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

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

		//构造发送数据:整型数据头+有效数据段
		byte[] arr = baos.toByteArray();
		int ObjLength = arr.length;   //获取有效数据段长度
		ByteBuffer bb = ByteBuffer.allocate(ObjLength+4);

		bb.clear();

		bb.putInt(ObjLength);	//存放一个int值在缓冲池头,表示有效数据段
		bb.put(arr);
		bb.flip();           //调整重置读写指针
		//转换为 写出 模式,通过变换 limit值,同时position值置0,写出[position,limit]区间的byte值

		return bb.array();
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

	public static String SetFileName(String suffix){
		return UUID.randomUUID().toString().substring(0,8)+suffix;
	}
	/**
	 * 将图片转换为字节存放在消息中发送
	 * @param pic 图片
	 * @param PicMsg 图片消息
	 * @return
	 */
	public static void fileToByteArray(File pic,Message PicMsg) {
		// 1、创建源与目的地

		// byte[] dest = null;
		// 2、选择流
		InputStream is = null;
		// 有新增方法不能发生多态
		ByteArrayOutputStream baos = null;
		try {
			is = new FileInputStream(pic);
			baos = new ByteArrayOutputStream();
			// 3、操作(分段读取)
			byte[] flush = new byte[1024 * 10];// 缓冲容器
			int len = -1;// 接收长度
			try {
				while ((len = is.read(flush)) != -1) {
					//将字节读取到数组flush中
					// 写出到ByteArrayOutputStream中的字节数组中
					baos.write(flush,0,len);
				}
				//
				baos.flush();
				// 返回回来，上面调用时就有了
				byte[] dest = baos.toByteArray();
				PicContent picContent = new PicContent().setData(dest)
						.setPicName("."+pic.getName()
								.substring(pic.getName().lastIndexOf(".")+1));//设置文件名后缀,文件名由服务器统一设置
				PicMsg.setContent(picContent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			// 4、释放资源
			try {
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 客户端将图片消息中的字节 转换为 图片存放在本地
	 * @param picMsg
	 */
	public static void ByteArrayToFile(Message picMsg){
		//1.通过picName,将字节数组以picName输入到对应文件夹
		//2.同时修改picName为相对路径,作为访问目标
		//3.将字节数组清空
		PicContent pic = (PicContent) picMsg.getContent();
		String picName = pic.getPicName();
		byte[] data = pic.getData();

		FileOutputStream outputStream=null;
		try {
			outputStream = new FileOutputStream(System.getProperty("user.dir") + "\\ClientFile\\RecvPic\\" +picName);
			outputStream.write(data);
			outputStream.flush();
			pic.setPicName(System.getProperty("user.dir") + "\\ClientFile\\RecvPic\\" +picName).setData(new byte[0]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			// 4、释放资源
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 服务器 将照片文件 留作备份
	 * @param picMsg
	 */
	public static void ToDownload(Message picMsg){
		PicContent pic = (PicContent) picMsg.getContent();
		pic.setPicName(SetFileName(pic.getPicName()));
		byte[] data = pic.getData();

		FileOutputStream outputStream=null;
		try {
			outputStream = new FileOutputStream(System.getProperty("user.dir") + "\\ServerPic\\" +pic.getPicName());
			outputStream.write(data);
			outputStream.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			// 4、释放资源
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

