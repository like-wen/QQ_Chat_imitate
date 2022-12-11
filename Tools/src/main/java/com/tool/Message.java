package com.tool;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;

import static com.tool.FinalValue.*;

/**
 * @author mazouri
 * @create 2021-05-05 21:00
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class Message implements Serializable {
	public int type;
	public String SendUser;
	public String GetUser;
	//内容对象为Object对象
	public Object content;
	// public String message;
	//todo: 图片的二进制传播

	public Message() {

	}

	public Message(String message) {
		this.SendUser="匿名";
		this.content=message;
	}

	public Message(int type, String message) {
		this.type = type;
		this.content = message;
	}
	// public Message(File file){
	// 	this.type = MSG_PICTURE;
	// 	try {
	// 		FileInputStream inputStream = new FileInputStream(file);
	// 		this.content = new ImageView(new Image(inputStream));
	// 	} catch (FileNotFoundException e) {
	// 		e.printStackTrace();
	// 	}
	// }

	public Message(int type, String sendUser, String getUser, File file) {
		this.type = type;
		SendUser = sendUser;
		GetUser = getUser;
		this.content =  file;
	}

	public Message(int type, String sendUser, String getUser, String content) {
		this.type = type;
		SendUser = sendUser;
		GetUser = getUser;
		this.content = content;
	}
}

