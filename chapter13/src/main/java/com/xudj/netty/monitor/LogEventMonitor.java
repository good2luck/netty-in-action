package com.xudj.netty.monitor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

/**
 * @program: netty-in-action
 * @description: 将LogEventDecoder、LogEventHandler处理的消息，安装到pipeline中
 * @author: xudj
 * @create: 2022-09-03 21:39
 **/
public class LogEventMonitor {

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;


    public LogEventMonitor(InetSocketAddress address) {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        // 引导NioDatagramChannel
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LogEventDecoder())
                                .addLast(new LogEventHandler());
                    }
                })
                .localAddress(address);
    }

    /**
     * 绑定channel
     */
    public Channel bind () {
        return bootstrap.bind().syncUninterruptibly().channel();
    }

    public void stop () {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8890;

        LogEventMonitor monitor = new LogEventMonitor(new InetSocketAddress(port));
        try {
            // 绑定 channel
            Channel channel = monitor.bind();
            // 输出
            System.out.println("LogEventMonitor running.");

            channel.closeFuture().sync();
        } finally {
            monitor.stop();
        }

    }


}
