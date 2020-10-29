package com.qxf.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @ClassName WebClient
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2020/10/29 23:16
 **/
public class WebClient {
    public static void main(String[] args) throws IOException {
        try {
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(8888));
            String content = "hello server,this is message from client";
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Selector selector = Selector.open();
            channel.configureBlocking(false);
            // 监听 读写 操作
            channel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);

            while (true) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if (key.isReadable()){
                        // 可读
                        buffer.flip();
                        channel.read(buffer);
                        System.out.println("收到服务端的信息："+new String(new byte[buffer.limit()]));

                    }
                    else if (key.isWritable()){
                        // 可写
                        buffer.clear();
                        buffer.put(("我是客户端，这是我发的信息"+Math.random()).getBytes());
                        channel.write(buffer);
                    }
                }
            }
        } catch (IOException e) {
        }
    }
}
