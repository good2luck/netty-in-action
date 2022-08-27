package com.xudj.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.net.InetSocketAddress;

/**
 * @program: netty-in-action
 * @description:
 * @author: xudj
 * @create: 2022-08-25 20:38
 **/
@Slf4j
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
//        if (args.length != 1) {
//            log.info("usage:{}", EchoServer.class.getSimpleName());
//        }
//        int port = Integer.parseInt(args[0]);
        // 设置端口值
        new EchoServer(8888).start();
    }

    private void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        // 创建EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建ServerBootStrap
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    // 指定所使用的 NIO传输channel
                    .channel(NioServerSocketChannel.class)
                    // 使用指定的端口设置套接字地址
                    .localAddress(new InetSocketAddress(port))
                    // 添加一个 EchoServerHandler 到子Channel的 ChannelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // 因为EchoServerHandler注解了@shareable，所有的客户端共享一个handler实例
                            channel.pipeline().addLast(serverHandler);
                        }
                    });

            // 异步绑定服务器，调用sync阻塞，直到绑定成功
            ChannelFuture channelFuture = bootstrap.bind().sync();
            // 获取Channel 的 closeFuture，并阻塞当前线程直到完成
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 关闭EventLoopGroup，释放所有资源
            group.shutdownGracefully().sync();
        }

    }

}
