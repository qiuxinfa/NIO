package com.qxf.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;

/**
 * @ClassName NIOServer
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2021/1/6 18:36
 **/
public class NIOServer {
    private int port = 8888;
    private Selector selector;
    private ByteBuffer readBuf = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuf = ByteBuffer.allocate(1024);
    private Scanner scanner = new Scanner(System.in);

    public NIOServer(){
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 通道设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            ServerSocket socket = serverSocketChannel.socket();
            selector = Selector.open();
            socket.bind(new InetSocketAddress(port));
            // 准备接收请求
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务端启动成功");
        }catch (IOException e){
            System.out.println("服务端启动失败了...");
            e.printStackTrace();
        }
    }

    private void listen(){
        while (true){
            try {
                // 阻塞,直到有准备就绪的通道
                selector.select();
                // 获取准备就绪的通道集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys){
                    if (selectionKey.isAcceptable()){
                        // 接收就绪，有客户端请求连接
                        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector,SelectionKey.OP_READ);
                        System.out.println("连接成功： "+client.getLocalAddress());
                    }else if (selectionKey.isReadable()){
                        // 读就绪，客户端发消息给服务端
                        System.out.println("服务端，读就绪...");
                        SocketChannel client = (SocketChannel)selectionKey.channel();
                        StringBuffer msg = new StringBuffer("<<<< 收到客户端消息： ");
                        // 读之前先清空缓存
                        readBuf.clear();
                        // 将客户端的数据读到缓存中
                        while (client.read(readBuf) > 0){
                            // 切换为读模式
                            readBuf.flip();
                            msg.append(new String(readBuf.array(),0,readBuf.limit()));
                        }
                        System.out.println(msg.toString());
                        client.register(selector,SelectionKey.OP_WRITE);
                    }else if (selectionKey.isWritable()){
                        // 写就绪， 发送数据给客户端
                        System.out.println("服务端，写就绪...");
                        SocketChannel client = (SocketChannel)selectionKey.channel();
                        String msg = scanner.nextLine();
                        // 写之前，先清空缓存
                        writeBuf.clear();
                        writeBuf.put(msg.getBytes());
                        // 切换为读模式
                        writeBuf.flip();
                        client.write(writeBuf);
                        client.register(selector,SelectionKey.OP_READ);
                        System.out.println("服务端发送的数据是： "+msg);
                    }
                }
                // 清除处理过的事件
                selectionKeys.clear();

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
//                scanner.close();
            }
        }
    }

    public static void main(String[] args) {
        new NIOServer().listen();
    }

}
