package com.lkw.server.FileServer;

import java.io.*;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Map;

public class fileHandler implements Runnable{

    private Map<String, PrintWriter> map;
    private Socket socket;

    private DataInputStream dis;

    private FileOutputStream fos;


    private static DecimalFormat df = null;

    static {
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    }

    public fileHandler(Map<String, PrintWriter> map, Socket socket) {
        super();
        this.socket=socket;
        this.map=map;

    }

    @Override
    public void run() {
        try {
        System.out.println("启动fileHandler线程");
        //获取地址
        String remoteSocketAddress = socket.getRemoteSocketAddress().toString().substring(1);
        System.out.println("服务器文件处理连接的地址为"+remoteSocketAddress);


        dis = new DataInputStream(socket.getInputStream());
            while (true){

                    // 文件名和长度
                    String fileName = dis.readUTF();
                    long fileLength = dis.readLong();
                    File directory = new File(System.getProperty("user.dir")+"\\MyFile");//指定目录
                    if(!directory.exists()) {
                        directory.mkdir();
                    }
                System.out.println("文件名"+fileName);
                    File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
                    fos = new FileOutputStream(file);

                    // 开始接收文件
                    byte[] bytes = new byte[1024];//读取缓冲区
                    int count=0;//计总数
                    int length = 0;//读取长度
                System.out.println("文件字节数"+fileLength);
                    while((length = dis.read(bytes, 0, bytes.length)) != -1) {
                        fos.write(bytes, 0, length);
                        fos.flush();
                        count+=length;//总计数增加

                        if(count==fileLength)
                            break;
                    }
                    fos.close();

                    System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");



            }
    }catch (IOException e){
        System.out.println("文件传输失败");
    }
    }
    /**
     * 格式化文件大小
     * @param length
     * @return
     */
    private String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        if(size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if(size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if(size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }

}
