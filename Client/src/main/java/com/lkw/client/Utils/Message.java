package com.lkw.client.Utils;

import java.io.Serializable;

/**
 * @author mazouri
 * @create 2021-05-05 21:00
 */
public class Message implements Serializable {
	public int type;
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

