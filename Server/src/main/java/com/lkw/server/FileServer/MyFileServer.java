package com.lkw.server.FileServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MyFileServer implements Callable {

    public static final int PORT = 9998;

    Map<String, PrintWriter> map = new HashMap<>();


    public MyFileServer() {
        super();

    }

    @Override
    public String call() {
        ServerSocket server;
        Socket socket;
        try {
            server = new ServerSocket(PORT);
            while(true) {
                socket = server.accept();
                //一个客户端接入就启动一个handler线程去处理
                System.out.println("启动myfileserver线程");
                new Thread(new FileHandler(map, socket)).start();
            }
        } catch (IOException e)
        {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return "MyFileServer线程正常执行..." + Thread.currentThread().getName();
    }
}
