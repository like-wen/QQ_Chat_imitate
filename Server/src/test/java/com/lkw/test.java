package com.lkw;

import java.util.Base64;

public class test {
    private static final String SALT = "MySaLt";

    private static final int REPEAT = 3;
    public static void main(String[] args) {
        String encode = encode("123");
        System.out.println(encode);
        System.out.println(decode(encode));
    }

    public static String encode(String str) {
        // 加盐处理
        String temp = str + "{" + SALT + "}";
        byte data[] = temp.getBytes();
        for (int i = 0; i < REPEAT; i++) {
            // 重复加密
            data = Base64.getEncoder().encode(data);
        }
        return new String(data);
    }
    public static String decode(String str) {
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
