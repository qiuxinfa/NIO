package com.qxf.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName WebServer
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2020/10/29 23:15
 **/
public class WebServer {
    public static void main(String[] args) {
        try {
            // 获取通道
            ServerSocketChannel ssc = ServerSocketChannel.open();
            // 监听本机 8888 端口
            ssc.socket().bind(new InetSocketAddress(8888));
            // 设置为非阻塞状态
            ssc.configureBlocking(false);
            // 获取Selector
            Selector selector = Selector.open();
            // 将channel注册到selector，并且指定感兴趣的事件是 Accept
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            String content = "";
            ByteBuffer buffer = ByteBuffer.allocate(1024);
//            ByteBuffer writeBuff = ByteBuffer.allocate(128);
//            writeBuff.put("received".getBytes());
//            writeBuff.flip();

            while (true) {
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();

                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isAcceptable()) {
                        // 创建新的连接，并且把连接注册到selector上，而且，
                        // 声明这个channel只对读操作感兴趣。
                        SocketChannel socketChannel = ssc.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    else if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        buffer.clear();
                        // 从channel中读数据到buffer，也就是向buffer写数据
                        socketChannel.read(buffer);
                        // 将buffer从写模式切换为读模式
                        buffer.flip();
                        content = new String(new byte[buffer.limit()]);
                        System.out.println("received from client : " + content);
                        // 监听感兴趣的事件 写
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                    else if (key.isWritable()) {
                        // 清空buffer，从头开始写
                        buffer.clear();
                        buffer.put(("我是服务端，我收到了你发来的信息了，你发的信息是："+content).getBytes());
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        socketChannel.write(buffer);
                        // 监听感兴趣的事件 读
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
