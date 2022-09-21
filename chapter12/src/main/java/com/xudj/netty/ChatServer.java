package com.xudj.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * @program: netty-in-action
 * @description: 引导服务器
 * @author: xudj
 * @create: 2022-09-02 08:02
 **/
public class ChatServer {

    // 创建DefaultChannelGroup，其将保存所有连接的channel
    private final ChannelGroup channels = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;


    /**
     * 创建ChatServerInitializer
     * @param channels
     * @return
     */
    public ChannelInitializer<Channel> createInitializer (ChannelGroup channels) {
        return new ChatServerInitializer(channels);
    }


    public ChannelFuture start (InetSocketAddress address) {
        // 引导服务器
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer(channels));

        ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly();
        channel = future.channel();

        return future;
    }


    /**
     * 处理关闭服务器，并释放所有资源
     */
    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        channels.close();
        group.shutdownGracefully();
    }


    public static void main(String[] args) {
        int port = 8889;
        final ChatServer chatServer = new ChatServer();
        // 启动
        ChannelFuture future = chatServer.start(new InetSocketAddress(port));

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                chatServer.destroy();
            }
        });

        future.channel().closeFuture().syncUninterruptibly();
    }

}
