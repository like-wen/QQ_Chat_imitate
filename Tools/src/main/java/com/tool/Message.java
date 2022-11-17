package com.tool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

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

	public String message;


	public Message() {

	}

	public Message(String message) {
		this.message = message;
	}

	public Message(int type, String message) {
		this.type = type;
		this.message = message;
	}

	@Override
	public String toString() {
		return "Message{" +
				"type=" + type +
				", message='" + message + '\'' +
				'}';
	}
}

