package com.qxf.buffer;

import java.nio.ByteBuffer;

/**
 * @ClassName BufferTest
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2020/10/27 22:34
 **/
public class BufferTest {
    /*
     * 除了boolean类型外，基本数据类型都有对应的Buffer类型，即：
     * ByteBuffer,CharBuffer,ShortBuffer,IntBuffer,LongBuffer,FloatBuffer,DoubleBuffer
     * 最常用的是ByteBuffer
     *
     **/
    public static void main(String[] args) {
        String data = "abc123";
        // 直接缓冲区和非直接缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        ByteBuffer.allocateDirect(1024);
//        boolean direct = buffer.isDirect();
        printMsg("before put data",buffer);

        // 把数据放入缓存中
        buffer.put(data.getBytes());
        printMsg("after put data",buffer);

        // 从写模式，切换为读模式
        buffer.flip();
        printMsg("after flip()",buffer);

        byte[] dest = new byte[buffer.limit()];
        // 从缓存中取数据
        buffer.get(dest, 0, 3);
        printMsg("第一次取出的数据："+new String(dest,0,3),buffer);

        // 标记position的位置
        buffer.mark();

        buffer.get(dest, 0, 2);
        printMsg("第二次取出的数据："+new String(dest,0,2),buffer);

        buffer.reset();
        printMsg("after reset data",buffer);
    }

    public static void printMsg(String title,ByteBuffer buffer){
        System.out.println(title);
        System.out.println("position: "+buffer.position());
        System.out.println("limit: "+buffer.limit());
        System.out.println("========================");
    }
}
