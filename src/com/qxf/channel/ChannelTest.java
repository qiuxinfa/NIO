package com.qxf.channel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @ClassName ChannelTest
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2020/10/27 23:50
 **/
public class ChannelTest {
    /**
     *通道：用于连接源节点和目标节点，负责缓冲区数据的传输
     * 分类：FileChannel,SocketChannel,ServerSocketChannel,DatagramChannel
     * 获取通道的方法：
     * 1.本地io：FileInputStream,FileOutputStream,RandomAccessFile
     * 2.网络io： Socket,ServerSocket,DatagramSocket
     * 3. Files工具类newByteChannel
     */
    public static void main(String[] args) throws IOException {
//        testCopyFileByChannel("D:\\Navicat注册码.txt","D:\\Navicat注册码111.txt");
//        testCopyFileByDirectBuffer();
        testChannel2Channel();
    }

    // 通道之间的数据传输
    public static void testChannel2Channel() throws IOException {
        // 获取通道
        FileChannel inChannel = FileChannel.open(Paths.get("D:\\Navicat注册码.txt"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("D:\\Navicat注册码444.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);

        // 通过调用 transferTo 或者 transferFrom方法实现复制
        inChannel.transferTo(0,inChannel.size(),outChannel);

        inChannel.close();
        outChannel.close();
    }

    // 文件复制，使用直接缓冲区
    public static void testCopyFileByDirectBuffer() throws IOException {
        // 获取通道
        FileChannel inChannel = FileChannel.open(Paths.get("D:\\Navicat注册码.txt"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("D:\\Navicat注册码222.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        // 内存映射文件
        MappedByteBuffer inMap = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMap = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
        // 缓冲区
        byte[] dest = new byte[inMap.limit()];
        // 直接对缓冲区进行读写
        inMap.get(dest);
        outMap.put(dest);
        // 关闭通道
        inChannel.close();
        outChannel.close();
    }

    // 文件复制，使用非直接缓冲区
    public static void testCopyFileByChannel(String srcFileName,String destFileName){
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
             fileInputStream = new FileInputStream(srcFileName);
             fileOutputStream = new FileOutputStream(destFileName);
             // 1. 获取通道
            inChannel = fileInputStream.getChannel();
            outChannel = fileOutputStream.getChannel();

            // 2. 分配指定缓冲区大小
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // 3.向缓冲区写数据
            while (inChannel.read(buffer) != -1){
                // 4.切换为读模式
                buffer.flip();
                // 5.从缓冲区读数据
                outChannel.write(buffer);
                // 6.清空缓冲区
                buffer.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outChannel != null){
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
