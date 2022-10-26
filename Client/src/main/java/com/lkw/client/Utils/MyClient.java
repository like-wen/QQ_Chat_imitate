package com.lkw.client.Utils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import javax.xml.ws.Holder;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyClient implements Runnable{
    TextArea sendArea;

     TextArea acceptArea;

     Button sendBtn;

    String username;

    private Socket socket;

    private String host="192.168.199.100";

    private int port=8088;



    public MyClient(String username, TextArea sendArea, TextArea acceptArea, Button sendBtn) {
      super();
      this.username=username;
      this.sendBtn=sendBtn;
      this.sendArea=sendArea;
      this.acceptArea=acceptArea;

        System.out.println("init");
    }

    @Override
    public void run() {
        try {
            System.out.println("线程run");
            socket=new Socket(host,port);
            InputStream in = socket.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
            OutputStream out = socket.getOutputStream();
            PrintWriter pWriter = new PrintWriter(out);
            pWriter.write(username);
            pWriter.flush();
            sendBtn.setOnAction(e->{
                System.out.println("发送文字");
                 pWriter.write( username+": \n"+sendArea.getText()+"\n\n" ); // 向服务器发送消息
                pWriter.flush();
            });

            String message;

            while (true){

                //Thread.sleep(10);

                    try {
                        message=bReader.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                            acceptArea.appendText(message + "\n");


            }








        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
