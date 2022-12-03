package com.lkw.server.FileServer;

import com.lkw.server.Utils.MybatisPlusController;

import java.io.*;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Map;

public class FileHandler implements Runnable{

    private Map<String, PrintWriter> map;
    private Socket socket;

    private DataInputStream dis;

    private FileOutputStream fos;

    private DataOutputStream writerFile;
    private FileInputStream fileInputStream;

    private MybatisPlusController mybatisPlusController=new MybatisPlusController();




    private static DecimalFormat df = null;

    static {
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    }

    public FileHandler(Map<String, PrintWriter> map, Socket socket) {
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
            System.out.println("服务器文件处理连接的地址为" + remoteSocketAddress);

            writerFile = new DataOutputStream(socket.getOutputStream());

            dis = new DataInputStream(socket.getInputStream());
            while (true) {
                // 文件名和长度
                String fileName = dis.readUTF();
                if (matchStringByIndexOf(fileName, "<get>")) {
                    //响应获取文件
                    System.out.println("响应");
                    //截取文件名传入响应
                    responseFile(fileName.substring(5));
                    continue;
                }
                //数据库更新
                mybatisPlusController.fileAdd(fileName,"SYSTEM");
                long fileLength = dis.readLong();
                File directory = new File(System.getProperty("user.dir") + "\\MyFile");//指定目录
                if (!directory.exists()) {
                    directory.mkdir();
                }
                System.out.println("文件名" + fileName);
                File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
                fos = new FileOutputStream(file);

                // 开始接收文件
                byte[] bytes = new byte[1024];//读取缓冲区
                int count = 0;//计总数
                int length = 0;//读取长度
                System.out.println("文件字节数" + fileLength);
                while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
                    fos.write(bytes, 0, length);
                    fos.flush();
                    count += length;//总计数增加
                    if (count == fileLength)
                        break;
                }
                fos.close();
                System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");
            }
        }catch (IOException e) {
            System.out.println("文件传输失败");
        }
    }

    /**
     * 响应回文件
     */
    private void responseFile(String substring) {

        File file = new File(System.getProperty("user.dir") + "\\MyFile\\"+substring);
        System.out.println(System.getProperty("user.dir") + "\\MyFile\\"+substring);
        try {
            fileInputStream = new FileInputStream(file);
            //传输文件名和长度
            writerFile.writeUTF(file.getName());
            writerFile.flush();
            writerFile.writeLong(file.length());
            writerFile.flush();
            //更新数据库
            mybatisPlusController.fileUpdate(file.getName());
            //传输文件
            byte[] bytes = new byte[1024];
            int length = 0;
            long progress = 0;
            while ((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
                writerFile.write(bytes, 0, length);
                writerFile.flush();
                progress += length;
                System.out.print("| " + (100 * progress / file.length()) + "% |");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
    //方法1、通过String的indexOf(String str, int fromIndex)方法
    private boolean matchStringByIndexOf( String parent,String child )
    {
        int index = 0;
        while( ( index = parent.indexOf(child, index) ) != -1 )
        {
            index = index+child.length();

            return true;
        }						  //结果输出
        return false;
    }

}

