package com.lkw.server.Server;

import com.lkw.server.FileServer.FileHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class LoginServer implements Runnable {

    public static final int PORT = 9999;




    @Override
    public void run() {
        ServerSocket server;
        try {
            server = new ServerSocket(PORT);
            while(true) {
                Socket socket = server.accept();
                //一个客户端接入就启动一个handler线程去处理
                System.out.println("启动登录线程");
                new Thread(new LoginHandler(socket)).start();
            }
        } catch (IOException e)
        {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }



    public class LoginHandler implements Runnable{
        Socket socket;
        public LoginHandler(Socket socket) {
            this.socket=socket;

        }

        @Override
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
                OutputStream out = socket.getOutputStream();
                PrintWriter pWriter = new PrintWriter(out);
                String str;

                while (true) {
                    str = bReader.readLine();
                    System.out.println(str);
                    String[] strs = str.split(":");


                    System.out.println(strs[0]+strs[1]);
                    Boolean b = checkLogin(strs[0], strs[1]);
                    System.out.println(b);
                    pWriter.write(b.toString()+"\n");
                    pWriter.flush();
                }







            }catch (Exception e){}
        }

        private Boolean checkLogin(String username,String password) {
            //TODO 数据库查询
            return true;

        }

    }
}














