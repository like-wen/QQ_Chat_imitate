package com.lkw.client.ClientController;

import com.lkw.client.Utils.MyClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainController {
    @FXML
    private TextArea sendArea;

    @FXML
    private TextArea acceptArea;

    @FXML
    private Button sendBtn;

    private String username;

    private String host="192.168.199.100";

    private int port=8088;
    private Socket socket;

    public BufferedReader bufferedReader; // 字节流读取套接字输入流

    private static PrintWriter pWriter; // 字节流写入套接字输出流

    public MainController() {


    }

    public void init(String username) throws IOException {



        new Thread(new MyClient(username,sendArea,acceptArea, sendBtn)).start();
    }

    public void sendTextBtn(){
        System.out.println("发送文字");
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat( "yyyy年MM月dd日 HH:mm:ss" ); // 设定日期格式
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Platform.runLater(()->{

            sendMessage( username+" "+df.format( date )+": \n"+sendArea.getText() ); // 向服务器发送消息
        });
        sendArea.setText("");//清空发送台


    }

    public void sendFileBtn(){


    }
    public final void sendMessage(String str) {
        pWriter.println( str );
        pWriter.flush();
    }

    public final String receiveMessage() {
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
