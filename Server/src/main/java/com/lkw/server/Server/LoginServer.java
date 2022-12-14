package com.lkw.server.Server;

import com.lkw.server.Utils.MybatisPlusController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LoginServer implements Runnable {

    public static final int PORT = 9999;

    MybatisPlusController mybatisPlusController=MybatisPlusController.getController();


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
                    String response;
                    strs[1]=strs[1].replaceAll("\\s+","");
                    strs[2]=strs[2].replaceAll("\\s+","");

                    if (strs[1]!=""&&strs[2]!="") {
                        response= mybatisPlusController.loginOrSignUp(strs[0], strs[1], strs[2]);
                    } else {
                        response="false";
                    }


                        System.out.println(strs[0] + strs[1] + strs[2]);


                        System.out.println(response);
                        //向socket发送消息,以便于登录判定
                        pWriter.write(response.toString() + "\n");
                        pWriter.flush();
                }








            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}














