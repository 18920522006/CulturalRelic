package com.netty.nio;

import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wangchen
 * @date 2018/2/26 15:06
 */
public class MultiplexerTimeServer implements Runnable {

    /**
     * 多路复用器
     */
    private Selector selector;

    /**
     * 父管道
     */
    private ServerSocketChannel serveChannel;

    private volatile boolean stop;

    /**
     * 初始化多路复用器，绑定监听接口
     * @param port
     */
    public MultiplexerTimeServer(int port) {
        try {
            /**
             * 打开多路复用器
             */
            selector = Selector.open();
            /**
             * 打开父通道
             */
            serveChannel = ServerSocketChannel.open();
            /**
             * 设置为非阻塞模式
             */
            serveChannel.configureBlocking(false);
            /**
             *  监听端口
             */
            serveChannel.socket().bind(new InetSocketAddress(port), 1024);
            /**
             * 将父通道绑定到多路复用器上，监听访问事件
             */
            serveChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("当前 Time Server 的启动监听端口为 ：" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void setStop(boolean stop) {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                /**
                 *  阻塞到至少有一个通道在你注册的事件上就绪了
                 */
                selector.select(1000);
                /**
                 * 选择已经就绪的通道
                 */
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                for (Iterator<SelectionKey> it = selectionKeys.iterator(); it.hasNext() ;) {
                    SelectionKey key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (IOException e) {
                        e.getStackTrace();

                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        /**
         * SelectionKey 准备就绪
         */
        if (key.isValid()) {
            /**
             * 处理访问监听
             */
            if (key.isAcceptable()){
                /**
                 * 通过Key 找到 ServerSocketChannel
                 */
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();

                SocketChannel socketChannel = serverSocketChannel.accept();
                /**
                 * 通道为非阻塞
                 */
                socketChannel.configureBlocking(false);
                /**
                 * 注册到多路复用器, 监听读操作
                 */
                socketChannel.register(selector, SelectionKey.OP_READ);
            }
            /**
             *  处理读的监听
             */
            if (key.isReadable()) {
                /**
                 * 通过Key 找到 SocketChannel
                 */
                SocketChannel socketChannel = (SocketChannel)key.channel();
                /**
                 * 非阻塞通道
                 */
                socketChannel.configureBlocking(false);
                /**
                 * 数据缓冲区
                 */
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                /**
                 *  从通道中读到 缓冲区为异步 不阻塞
                 */
                int readBytes = socketChannel.read(byteBuffer);

                if (readBytes > 0) {
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);

                    String body = new String(bytes, "UTF-8");

                    System.out.println("socketChannel 接收到的信息 ：" + body);

                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";

                    /**
                     * 返回客户端信息
                     */
                    doWrite(socketChannel, currentTime);
                } else if (readBytes < 0) {
                    //对端链路关闭
                    key.cancel();
                    socketChannel.close();
                } else {

                }
            }
        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (!StringUtils.isEmpty(response)) {
            byte[] bytes = response.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            channel.write(byteBuffer);
        }
    }
}
