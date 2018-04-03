package com.netty.privates.frame;

import com.netty.privates.NettyConstant;
import com.netty.privates.server.NettyServer;
import io.netty.channel.ChannelFuture;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalProgressBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

/**
 * @author wangchen
 * @date 2018/4/3 9:10
 */
public class JProgressBarPanel extends JFrame {

    public void  buildPanel() {
        /**
         * 面板
         */
        final JFrame frame = new JFrame("传输进度");
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        /**
         * 面板关闭事件
         */
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            }
        });
        /**
         * 按钮
         */
        JButton accept = new JButton("接收文件");
        JButton transfer = new JButton("发送文件");
        /**
         * 接收文件
         */
        accept.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.setTitle("正在接收");
                accept.setVisible(false);
                transfer.setVisible(false);

                JTextField jTextField = null;
                try {
                    jTextField = new JTextField("监听地址：" + InetAddress.getLocalHost().getHostAddress() + ":" + NettyConstant.LOCAL_PORT );
                    frame.add(jTextField);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                jTextField.setBorder(new EmptyBorder(0,0,0,0));
                jTextField.setBackground(new Color(238, 238, 238));
                jTextField.setFont(new Font("宋体",Font.BOLD,20));
            }
        });
        /**
         * 发送文件
         */
        transfer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setTitle("发送文件");
                accept.setVisible(false);
                transfer.setVisible(false);
            }
        });
        /**
         * 添加按钮
         */
        frame.add(accept);
        frame.add(transfer);
        /**
         * 显示面板
         */
        frame.setVisible(true);
    }

    public static void main(String[] args){
        new JProgressBarPanel().buildPanel();
    }
}
