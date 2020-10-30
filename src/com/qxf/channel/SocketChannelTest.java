package com.qxf.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @ClassName SocketChannelTest
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2020/10/30 21:58
 **/
public class SocketChannelTest {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9999));
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        // 将数据放入缓冲区
        buffer.put("hello server,I'm client".getBytes());
        socketChannel.write(buffer);
        socketChannel.close();
    }
}
