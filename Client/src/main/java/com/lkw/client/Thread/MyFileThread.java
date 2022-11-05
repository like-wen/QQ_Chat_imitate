package com.lkw.client.Thread;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;

/**
 * 客户端
 * @author 花大侠
 *
 */
public class MyFileThread implements Runnable {

	private Text toolTips;
	private Socket 	socketFile;

	private Button sendFileBtn;
	private TextArea filePathArea;
	private DataOutputStream writerFile;
	private FileInputStream fileInputStream;



	public MyFileThread(Button sendFileBtn, TextArea filePathArea, Text toolTips) {
		super();
		this.filePathArea=filePathArea;
		this.sendFileBtn=sendFileBtn;

		this.toolTips=toolTips;
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
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		sendFileBtn.setOnAction(e -> {
			//读取地址输入框内容并转换成文件
			File file=new File(filePathArea.getText());
				try {
					fileInputStream = new FileInputStream(file);
					//传输文件名和长度

					writerFile.writeUTF(file.getName());
					writerFile.flush();
					writerFile.writeLong(file.length());
					writerFile.flush();
					//传输文件
					byte[] bytes=new byte[1024];
					int length = 0;
					long progress = 0;
					while((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
						writerFile.write(bytes, 0, length);
						writerFile.flush();
						progress += length;
						toolTips.setText("");
						System.out.print("| " + (100*progress/file.length()) + "% |");
					}
				} catch (Exception ex) {
					toolTips.setText("文件找不到!");
					throw new RuntimeException(ex);
				}
		});


			//收消息
			while (true) {


			}
		}


}