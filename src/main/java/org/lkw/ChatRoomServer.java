package org.lkw;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ChatRoomServer {
    private ServerSocket serverSocket; // 服务器套接字
    private HashSet<Socket> allSockets; // 客户端套接字集合

    // 聊天室构造方法
    public ChatRoomServer(){
        try {
            serverSocket = new ServerSocket( 8088 );
        } catch (IOException e) {
            e.printStackTrace();
        }
        allSockets = new HashSet<>(  ); //  实例化客户端套接字集合
    }

    // 启动聊天室的方法
    private void startService() throws IOException {
        int i=0;
        while(true) {
            i++;
            Socket s = serverSocket.accept(); // 获得一个客户端连接
            System.out.printf( "用户%d已经进入聊天室\n",i );
            allSockets.add(s);
            new ServerThread(s).start();
        }
    }

    // 服务器线程内部类
    private class ServerThread extends Thread {
        Socket socket; // 客户端套接字

        ServerThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            BufferedReader br = null;
            try {
                // 将客户端套接字输入流转为字节流读取
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    String str = br.readLine(); // 读取到一行之后，则赋值给字符串
                    if (str.contains("%EXIT%")) { // 如果文本内容包含"%EXIT%"
                        allSockets.remove(socket); // 集合删除此客户端连接
                        // 服务器向所有客户端接口发出退出通知
                        sendMessageToAllClient(str.split(":")[1] + "用户已退出聊天室");
                        socket.close();
                        return;
                    }
                    sendMessageToAllClient(str); // 向所有客户端发送此客户端发送来的文本消息
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* *
         * 发送消息给客户端的方法
         *
         * @param message
         */
        final void sendMessageToAllClient(String message) throws IOException {
            for (Socket s : allSockets) { // 循环集合中所有的客户端连接
                PrintWriter pw = new PrintWriter(s.getOutputStream()); // 创建输出流
                pw.println(message); // 输入写入文本
                pw.flush();
            }
        }

    }


    public static void main(String[] args) {
        try {
            new ChatRoomServer().startService();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
