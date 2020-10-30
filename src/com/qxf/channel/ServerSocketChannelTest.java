package com.qxf.channel;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @ClassName ServerSocketChannelTest
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2020/10/30 22:03
 **/
public class ServerSocketChannelTest {
    public static void main(String[] args) throws Exception{
        ServerSocketChannel serverChannel = ServerSocketChannel.open()
                .bind(new InetSocketAddress("127.0.0.1", 9999));
        // 接收客户端请求
        SocketChannel socketChannel = serverChannel.accept();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.read(buffer);
        buffer.flip();
        System.out.println("服务端收到客户端的信息："+new String(buffer.array(),0,buffer.limit()));
        socketChannel.close();
        serverChannel.close();
    }
}
