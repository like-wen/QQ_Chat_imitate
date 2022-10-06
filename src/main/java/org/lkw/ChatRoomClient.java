package org.lkw;

// !/usr/bin/env jdk1.8
// encoding:utf-8
//@software:IntelliJ IDEA
//@pack:chating
//@user:彭友聪
//@date:2019/07/27
//@time:上午 10:14
//@project:IDEA_JAVA
//@file:ChatRoomClient.java
//Author:御承扬
//email:2923616405@qq.com

/*
 *   聊天室客户端
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatRoomClient {
    private Socket socket; // 客户端套接字
    private BufferedReader bufferedReader; // 字节流读取套接字输入流
    private PrintWriter pWriter; // 字节流写入套接字输出流

    /*
     * 聊天室客户端构造方法
     * @param host
     *          服务器 IP 地址
     * @param port
     *          服务器与客户端互联的端口
     */
    public ChatRoomClient(String host, int port) throws UnknownHostException, IOException {
        socket = new Socket( host, port ); // 连接服务器
        bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) ); // 字节流读取套接字输入流
        pWriter = new PrintWriter( socket.getOutputStream() ); // 字节流写入套接字输出流
    }

    /*
     * 聊天室客户端发送消息的方法
     * @param str 客户端发送的消息
     */
    public final void sendMessage(String str) {
        pWriter.println( str );
        pWriter.flush();
    }

    /*
     * 聊天客户端获取消息的方法
     * @return 读取某个客户端发送的消息
     */
    final String receiveMessage() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
     * 关闭套接字
     */
    public final void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
