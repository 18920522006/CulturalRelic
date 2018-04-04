package com.netty.privates.frame;

import com.netty.privates.NettyConstant;
import com.netty.privates.client.FileUploadReqHandler;
import com.netty.privates.client.NettyClient;
import com.netty.privates.server.NettyServer;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangchen
 * @date 2018/4/3 9:10
 */
public class JProgressBarPanel extends JFrame {

    Thread thread = null;
    JProgressBarPanel frame = null;
    NioEventLoopGroup bossGroup = null;
    NioEventLoopGroup workGroup = null;

    Map<String, JProgressBar> progressBars = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(JProgressBarPanel.class);


    public NioEventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public NioEventLoopGroup getWorkGroup() {
        return workGroup;
    }

    public void  buildPanel() {
        /**
         * 面板
         */
        frame = new JProgressBarPanel();
        frame.setSize(750, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        /**
         * 面板关闭事件
         */
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (bossGroup != null) {
                    bossGroup.shutdownGracefully();
                }
                if (workGroup != null) {
                    workGroup.shutdownGracefully();
                }
                if (thread != null) {
                    thread.interrupt();
                }
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

                /**
                 * 调用Netty Server
                 */
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            bossGroup = new NioEventLoopGroup();
                            workGroup = new NioEventLoopGroup();
                            new NettyServer().run(NettyConstant.REMOTE_IP, NettyConstant.LOCAL_PORT, bossGroup, workGroup);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
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

                /**
                 * 上传按钮
                 */
                JButton developer = new JButton("上传文件");
                frame.add(developer);

                developer.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        /**
                         * 选择文件
                         */
                        JFileChooser chooser = new JFileChooser();
                        /**
                         * 多选
                         */
                        chooser.setMultiSelectionEnabled(true);

                        if (chooser.showOpenDialog(developer) == JFileChooser.APPROVE_OPTION) {
                            File[] files = chooser.getSelectedFiles();
                            if (files != null && files.length > 0) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        workGroup = new NioEventLoopGroup();
                                        try {
                                            new NettyClient().connect(
                                                    NettyConstant.REMOTE_IP,
                                                    NettyConstant.LOCAL_PORT,
                                                    files,
                                                    workGroup,
                                                    frame);
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        }
                    }
                });
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

    /**
     * 加入进度条
     * @param file
     * @param progress
     */
    public void progress(JFrame frame, File file, String progress) {
        JProgressBar progressBar = null;
        if (!progressBars.containsKey(file.getPath())) {
            progressBar = new JProgressBar();

            progressBar.setStringPainted(true);
            progressBars.put(file.getPath(), progressBar);

            frame.add(progressBar);
            frame.add(new JLabel(file.getName()));

        } else {
            progressBar = progressBars.get(file.getPath());
        }

        if(progress == null) {

        } else if (Integer.parseInt(progress) == 100 ) {
            progressBar.setString("已完成!");
        } else {
            log.info("进度 ： " + progress);
            progressBar.setValue(Integer.parseInt(progress));
        }
    }

    public static void main(String[] args){
        new JProgressBarPanel().buildPanel();
    }
}
