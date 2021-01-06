package com.qxf.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;

/**
 * @ClassName NIOClient
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2021/1/6 18:36
 **/
public class NIOClient {
    private String ip = "localhost";
    private int port = 8888;
    private Selector selector;
    private ByteBuffer readBuf = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuf = ByteBuffer.allocate(1024);
    private Scanner scanner = new Scanner(System.in);

    public NIOClient(){
        try {
            SocketChannel socketChannel = SocketChannel.open();
            // 通道设置为非阻塞
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(ip,port));
            start();
        }catch (IOException e){
            System.out.println("客户端启动失败了...");
            e.printStackTrace();
        }
    }

    private void start(){
        while (true){
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys){
                    if (selectionKey.isConnectable()){
                        // 连接就绪
                        SocketChannel client = (SocketChannel) selectionKey.channel();
                        if (client.isConnectionPending()){
                            // 完成连接
                            client.finishConnect();
                            System.out.println("连接成功...");
                        }
                        // 客户端，先写，后读
                        client.register(selector,SelectionKey.OP_WRITE);
                    }else if (selectionKey.isReadable()){
                        // 读就绪，收到服务端发来的消息
                        System.out.println("客户端，读就绪...");
                        SocketChannel client = (SocketChannel)selectionKey.channel();
                        StringBuffer msg = new StringBuffer(">>> 收到服务端消息： ");
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
                        System.out.println("客户端，写就绪...");
                        // 写就绪， 发送数据给服务端
                        SocketChannel client = (SocketChannel)selectionKey.channel();
                        String msg = scanner.nextLine();
                        // 写之前，先清空缓存
                        writeBuf.clear();
                        writeBuf.put(msg.getBytes());
                        // 切换为读模式
                        writeBuf.flip();
                        client.write(writeBuf);
                        client.register(selector,SelectionKey.OP_READ);
                        System.out.println("客户端发送的数据是： "+msg);
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
        new NIOClient();
    }

}
