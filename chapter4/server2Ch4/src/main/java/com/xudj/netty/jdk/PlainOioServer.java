package com.xudj.netty.jdk;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @program: netty-in-action
 * @description: 未使用netty的阻塞网络程序
 * @author: xudj
 * @create: 2022-08-27 10:59
 **/
@Slf4j
public class PlainOioServer {

    public void serve(int port) throws IOException {
        // 绑定到指定端口
        final ServerSocket serverSocket = new ServerSocket(port);

        try {
            for (;;) {
                // 接收连接
                final Socket clientSocket = serverSocket.accept();
                log.info("accept connection from:{}", clientSocket);
                // 启动新线程处理连接
                new Thread(() -> {
                    OutputStream out;
                    try {
                        out = clientSocket.getOutputStream();
                        // 消息写给连接的客户端
                        out.write("hi".getBytes(StandardCharsets.UTF_8));
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            // 关闭连接
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        } finally {}
    }

}
