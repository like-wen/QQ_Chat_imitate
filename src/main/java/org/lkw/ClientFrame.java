package org.lkw;

// !/usr/bin/env jdk1.8
// encoding:utf-8
//@software:IntelliJ IDEA
//@pack:chating
//@user:彭友聪
//@date:2019/07/27
//@time:下午 3:31
//@project:IDEA_JAVA
//@file:ClientFrame.java
//Author:御承扬
//email:2923616405@qq.com


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientFrame extends JFrame {
    private JTextField tfMessage; // 信息发送文本框
    private JTextArea textArea; // 信息接收文本域
    private ChatRoomClient client; // 客户端连接对象

    // 构造方法
    public ClientFrame(String ip, String userName) {
        super("多线程聊天室");
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        setLayout( null );
        setBounds( 100,50,800,680 );
        Container c = getContentPane();
        // 用户名称
        //窗口监听器
        this.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent atg0) {
                int op = JOptionPane.showConfirmDialog( ClientFrame.this,
                        "确定要退出聊天室吗？", "退出", JOptionPane.CLOSED_OPTION); // 弹出提示框
                if (op == JOptionPane.CLOSED_OPTION) {
                    client.sendMessage( "%EXIT%:"+userName ); // 发送信息
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    client.close();
                    System.exit( 0 );
                }
            }
        });
        // 发送按钮
        JButton btnSend = new JButton( "发送" );
        btnSend.setBounds( 680,550,100,30 );
        btnSend.setVisible( true );
        btnSend.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date date = new Date();
                SimpleDateFormat df = new SimpleDateFormat( "yyyy年MM月dd日 HH:mm:ss" ); // 设定日期格式
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                client.sendMessage( userName+" "+df.format( date )+": \n"+tfMessage.getText() ); // 向服务器发送消息
                tfMessage.setText(""); // 输入框为空
            }
        } );
        // 实例化客户端对象
        try {
            client = new ChatRoomClient( ip,8088 ); // 创建客户端对象
        } catch(UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ReadMessageThread messageThread = new ReadMessageThread(); // 创建读取客户端消息的线程类对象
        messageThread.start();

        client.sendMessage(userName);

        // 控件初始化
        JLabel text = new JLabel( "消息对话框:" );
        text.setBounds( 10,0,200,30 );
        text.setVisible( true );
        // 消息对话框
        textArea = new JTextArea(  );
        textArea.setBounds( 10,30,750,420 );
        textArea.setVisible( true );
        textArea.setBackground( Color.LIGHT_GRAY);
        textArea.setFont(new Font("标楷体", Font.BOLD, 16)); //设置当前字体。
        // 显示用户名
        JLabel lblUserName = new JLabel( userName );
        lblUserName.setBounds( 10,550,50,30 );
        lblUserName.setVisible( true );
        tfMessage = new JTextField(  );
        tfMessage.setBounds( 50,550,600,30 );
        tfMessage.setVisible( true );
        // 下方面板
        c.add(text);
        c.add(textArea);
        c.add(lblUserName);
        c.add(tfMessage);
        c.add(btnSend);
        c.setBackground( Color.WHITE );
        setVisible( true );
    }
    //数据接收
    private class ReadMessageThread extends Thread{
        private ReadMessageThread() {
        }

        public void run() {
            while (true) {
                String str = client.receiveMessage(); // 客户端收到服务器发来的文本内容
                textArea.append( str+"\n" ); // 向文本框添加内容
            }
        }
    }

}
