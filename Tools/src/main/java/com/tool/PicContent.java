package com.tool;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.*;

@Data
@Accessors(chain = true)
public class PicContent implements Serializable{
	String PicName;
	byte[] data;
}
