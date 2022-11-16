package com.lkw.server.FileServer;

import com.lkw.server.Server.MyServer;
import com.lkw.server.Utils.Message;
import com.lkw.server.Utils.Utils;
import jdk.nashorn.internal.codegen.CompilerConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.lkw.server.Utils.FinalValue.*;

@Slf4j
public class MyFileServer extends MyServer implements Runnable {

    public static final int PORT = 9998;

    private Selector selector;

    Map<String, PrintWriter> map = new HashMap<>();


    public MyFileServer() {
        super();
        try {
            init(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(int port) throws IOException{
        //1.获取一个ServerSocket通道
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        System.out.println(serverChannel.isBlocking());
        serverChannel.configureBlocking(false);////设置为非阻塞
        System.out.println(serverChannel.isBlocking());
        //2.绑定监听，配置TCP参数，例如backlog大小
        serverChannel.socket().bind(new InetSocketAddress(port));
        //3.获取通道管理器
        selector= Selector.open();
        //将通道管理器与通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
        //只有当该事件到达时，Selector.select()会返回，否则一直阻塞。
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);//注册channel到selector,监测接受此通道套接字的连接

    }
    public void listen() throws IOException{
        System.out.println("服务器启动成功");
        boolean isRun = true;
        while(isRun){
            //当有注册的事件到达时，方法返回，否则阻塞。
            selector.select();
            //获取selector中的迭代器，选中项为注册的事件
            Iterator<SelectionKey> it=selector.selectedKeys().iterator();
            while(it.hasNext()){
                SelectionKey key = it.next();
                //删除已选的key，防止重复处理
                it.remove();
                if(key.isAcceptable()){
                    ServerSocketChannel server = (ServerSocketChannel)key.channel();
                    //获得客户端连接通道
                    SocketChannel channel = server.accept();
                    channel.configureBlocking(false);//可以在任意位置调用这个方法，新的阻塞模式只会影响下面的i/o操作
                    //在与客户端连接成功后，为客户端通道注册SelectionKey.OP_WRITE事件。
                    channel.register(selector,SelectionKey.OP_READ);
                    System.out.println("客户端请求连接事件");
                }else if(key.isReadable()){
                    dealReadEvent(key);
                 }

            }
        }
    }
    public void WriteFile(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        FileInputStream file = new FileInputStream("C:\\Users\\WH\\Desktop\\实验室各种账号密码.txt");
        FileChannel fileChannel = file.getChannel();
        //500M  堆外内存
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(524288000);
        while(fileChannel.position()<fileChannel.size()){
            fileChannel.read(byteBuffer);//从文件通道读取到byteBuffer
            byteBuffer.flip();
            while(byteBuffer.hasRemaining()){
                socketChannel.write(byteBuffer);//写入通道
            }
            byteBuffer.clear();//清理byteBuffer
            System.out.println(fileChannel.position()+" "+fileChannel.size());
        }
        System.out.println("结束写操作");
        //将文件写入到客户端后,就取消掉channel
        socketChannel.close();
    }

    public void dealReadEvent(SelectionKey key){
        boolean getFile=false;
        boolean sendFile=false;
        SocketChannel channel = null;
        try {
            channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int read = channel.read(buffer);

            // 如果是正常断开，read 的方法的返回值是 -1
            if (read == -1) {
                //cancel 会取消注册在 selector 上的 channel，并从 keys 集合中删除 key 后续不会再监听事件
                key.cancel();
            } else if(read>1000 && sendFile==true){
            //    可能是文件
            //    通过异步,接受文件
            //    然后将更改参数
                sendFile=false;
            } else{
                //处理客户端上传信息/文件
                //判断是消息还是文件
                //判断是上传还是下载文件
                //消息超过1024(一般不会)

                buffer.flip();
                Message msg =  Utils.decode(buffer.array());

                if(msg.type == MSG_GetFile)
                {
                //    需要向客户端发送文件

                //    关闭连接
                    key.cancel();
                }else if(msg.type==MSG_SentFile){
                // 客户端即将发送文件
                    sendFile=true;
                }
                log.debug(msg.toString());
            }

        } catch (IOException | ClassNotFoundException e) {

            System.out.println((key.attachment() == null ? "匿名用户" : key.attachment()) + " 离线了..");
            sendMsgToClient(new Message(MSG_SYSTEM, key.attachment() + " 离线了.."), channel);

            //取消注册
            key.cancel();

            //关闭通道
            try {
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    @Override
    public void run() {
        //
        // ServerSocket server;
        // Socket socket;
        // try {
        //     server = new ServerSocket(PORT);
        //     while(true) {
        //         socket = server.accept();
        //         //一个客户端接入就启动一个handler线程去处理
        //         System.out.println("启动myfileserver线程");
        //         new Thread(new FileHandler(map, socket)).start();
        //     }
        // } catch (IOException e) {
        //     // TODO 自动生成的 catch 块
        //     e.printStackTrace();
        // }
        try {
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
