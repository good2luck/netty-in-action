package com.xudj.netty.jdk;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @program: netty-in-action
 * @description: 未使用netty的Nio程序
 * @author: xudj
 * @create: 2022-08-27 11:07
 **/
@Slf4j
public class PlainNioServer {

    public void serve (int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        ServerSocket serverSocket = serverChannel.socket();
        InetSocketAddress socketAddress = new InetSocketAddress(port);
        // 服务器绑定到指定端口
        serverSocket.bind(socketAddress);

        // 打开selector来处理channel
        Selector selector = Selector.open();
        // 将ServerSocket注册到Selector以接受连接
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 消息
        final ByteBuffer msg = ByteBuffer.wrap("hi".getBytes(StandardCharsets.UTF_8));
        for (;;) {
            try {
                // 等待需要处理的新事件，阻塞将持续到下一个传入事件
              selector.select();
            }  catch (IOException e) {
                e.printStackTrace();
                break;
            }
            // 获取所有接收事件的SelectionKey
            Set<SelectionKey> readKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel client = channel.accept();
                        client.configureBlocking(false);
                        // 接收客户端，并注册到选择器
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                        log.info("accept connection:{}", client);
                    }

                    // 套接字是否准备好写数据
                    if (key.isWritable()) {
                         SocketChannel client = (SocketChannel) key.channel();
                         ByteBuffer buffer = (ByteBuffer) key.attachment();
                         while (buffer.hasRemaining()) {
                            if (client.write(buffer) == 0) {
                                break;
                            }
                         }
                         // 关闭连接
                         client.close();
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException e1) {e1.printStackTrace();}
                }
            }
        }


    }

}
