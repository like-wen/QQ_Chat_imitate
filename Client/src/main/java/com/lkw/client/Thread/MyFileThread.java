package com.lkw.client.Thread;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;

/**
 * 客户端
 *
 *
 */
public class MyFileThread implements Runnable {

	private Text toolTips;

	private Button selectFileBtn;
	private Socket socketFile;

	private Button sendFileBtn;
	private TextArea filePathArea;

	private ListView<String> fileList;
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



	public MyFileThread(Button sendFileBtn, Button acceptAreaFileBtn, TextArea filePathArea, ListView<String> fileList, Button selectFileBtn, Text toolTips) {
		super();
		filePathArea.setPromptText("未选择文件");
		this.filePathArea = filePathArea;

		this.sendFileBtn = sendFileBtn;
		this.acceptAreaFileBtn=acceptAreaFileBtn;
		this.fileList=fileList;
		this.selectFileBtn=selectFileBtn;
		this.toolTips = toolTips;
	}

	@Override
	public void run() {
		try {
			socketFile = new Socket("localhost", 9998);//192.168.199.100
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
		selectFileBtn.setOnAction(e->{
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("选择上传云端的文件");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			File file = fileChooser.showOpenDialog(new Stage());
			if (file==null)
				return;
			filePathArea.setText(file.getAbsolutePath());
		});


		sendFileBtn.setOnAction(e -> {
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
					System.out.println("文件传输进度: " + (100 * progress / file.length()) + "% ");
				}
				toolTips.setText("传输完成");
				filePathArea.setText("");
			} catch (Exception ex) {
				toolTips.setText("文件找不到!");
				filePathArea.setText("");
				throw new RuntimeException(ex);
			}
		});

		acceptAreaFileBtn.setOnAction(e->{
			try {
				String selectedItem = fileList.getSelectionModel().getSelectedItem();
				System.out.println("获取的文件名是"+selectedItem);
				if(selectedItem==null){
					toolTips.setText("没有选中文件");
				}else {
					writerFile.writeUTF("<get>" + selectedItem);
					writerFile.flush();
				}
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
				toolTips.setText("文件接收成功");
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
