package com.lkw.client.Thread;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.io.*;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;

/**
 * 客户端
 * @author 花大侠
 *
 */
public class MyFileThread implements Runnable {

	private Text toolTips;
	private ObservableList<String> items;
	private Socket socketFile;

	private Button sendFileBtn;
	private TextArea filePathArea;

	private ListView<String> listFile;
	private DataOutputStream writerFile;
	private FileInputStream fileInputStream;

	private Button acceptAreaFileBtn;

	private DataInputStream dis;

	private FileOutputStream fos;



	private static DecimalFormat df = null;

	static {
		// 设置数字格式，保留一位有效小数
		df = new DecimalFormat("#0.0");
		df.setRoundingMode(RoundingMode.HALF_UP);
		df.setMinimumFractionDigits(1);
		df.setMaximumFractionDigits(1);
	}



	public MyFileThread(Button sendFileBtn, Button acceptAreaFileBtn, TextArea filePathArea, ListView<String> listFile, ObservableList<String> items, Text toolTips) {
		super();
		this.filePathArea = filePathArea;
		this.sendFileBtn = sendFileBtn;
		this.acceptAreaFileBtn=acceptAreaFileBtn;
		this.listFile=listFile;
		this.toolTips = toolTips;
	}

	@Override
	public void run() {
		try {
			socketFile = new Socket("127.0.0.1", 9998);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//socketFile的输入输出
		//BufferedReader ReaderF = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		try {
			writerFile = new DataOutputStream(socketFile.getOutputStream());

			dis = new DataInputStream(socketFile.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		sendFileBtn.setOnAction(e -> {
			//读取地址输入框内容并转换成文件
			File file = new File(filePathArea.getText());
			try {
				fileInputStream = new FileInputStream(file);
				//传输文件名和长度

				writerFile.writeUTF(file.getName());
				writerFile.flush();
				writerFile.writeLong(file.length());
				writerFile.flush();
				//传输文件
				byte[] bytes = new byte[1024];
				int length = 0;
				long progress = 0;
				while ((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
					writerFile.write(bytes, 0, length);
					writerFile.flush();
					progress += length;
					toolTips.setText("");
					System.out.print("| " + (100 * progress / file.length()) + "% |");
				}
			} catch (Exception ex) {
				toolTips.setText("文件找不到!");
				throw new RuntimeException(ex);
			}
		});

		acceptAreaFileBtn.setOnAction(e->{
			try {
				String selectedItem = listFile.getSelectionModel().getSelectedItem();
				System.out.println("获取的文件名是"+selectedItem);
				writerFile.writeUTF("<get>"+selectedItem);
				writerFile.flush();





			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}


		});

		//收消息
		while (true) {
			try {

				// 文件名和长度
				String fileName = null;
				fileName = dis.readUTF();
				long fileLength = dis.readLong();
				File directory = new File(System.getProperty("user.dir") + "\\ClientFile");//指定目录
				if (!directory.exists()) {
					directory.mkdir();
				}
				System.out.println("文件名" + fileName);
				File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
				fos = new FileOutputStream(file);

				// 开始接收文件
				byte[] bytes = new byte[1024];//读取缓冲区
				int count = 0;//计总数
				int length = 0;//读取长度
				System.out.println("文件字节数" + fileLength);
				while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
					fos.write(bytes, 0, length);
					fos.flush();
					count += length;//总计数增加

					if (count == fileLength)
						break;
				}
				fos.close();

				System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}


	/**
	 * 格式化文件大小
	 * @param length
	 * @return
	 */
	private String getFormatFileSize(long length) {
		double size = ((double) length) / (1 << 30);
		if(size >= 1) {
			return df.format(size) + "GB";
		}
		size = ((double) length) / (1 << 20);
		if(size >= 1) {
			return df.format(size) + "MB";
		}
		size = ((double) length) / (1 << 10);
		if(size >= 1) {
			return df.format(size) + "KB";
		}
		return length + "B";
	}

}