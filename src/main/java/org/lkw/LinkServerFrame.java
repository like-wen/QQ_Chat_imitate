package org.lkw;

// !/usr/bin/env jdk1.8
// encoding:utf-8
//@software:IntelliJ IDEA
//@pack:chating
//@user:彭友聪
//@date:2019/07/27
//@time:下午 4:28
//@project:IDEA_JAVA
//@file:LinkServerFrame.java
//Author:御承扬
//email:2923616405@qq.com


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LinkServerFrame extends JFrame {
    private LinkServerFrame() {
        setLayout( null );
        setBounds(540,200, 400,250 );
        setVisible( true );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        JLabel lbIP = new JLabel( "服务器 IP 地址：" );
        lbIP.setBounds( 10,10,100,30 );
        lbIP.setVisible( true );
        JLabel userName = new JLabel( "用户名：" );
        userName.setBounds( 40,60,50,30 );
        userName.setVisible( true );
        JTextField jfIP = new JTextField(  ); // IP 地址输入框
        jfIP.setBounds( 110,10,250,30 );
        jfIP.setVisible( true );
        JTextField jfUser = new JTextField(  );
        jfUser.setBounds( 110,60,250,30 );
        jfUser.setVisible( true );
        JButton btn = new JButton( "连接服务器" );
        btn.setBounds( 120,100,100,20 );
        btn.setVisible( true );
        btn.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jfIP.getText().equals( "" ) && !jfUser.getText().equals( "" )) {
                    dispose(); // 销毁当前窗体
                    // 创建客户端窗体并传参
                    ClientFrame clientFrame = new ClientFrame( jfIP.getText().trim(), jfUser.getText().trim() );
                } else {
                    JOptionPane.showMessageDialog( null,"文本框里的内容不能为空！","警告",JOptionPane.WARNING_MESSAGE );
                }
            }
        } );
        Container center = getContentPane();
        center.add(lbIP);
        center.add( jfIP );
        center.add( userName );
        center.add( jfUser );
        center.add( btn );
        center.setBackground( Color.WHITE );
    }

    public static void main(String[] args) {
       new LinkServerFrame();
    }
}














