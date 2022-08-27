package com.xudj.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @program: netty-in-action
 * @description:
 * @author: xudj
 * @create: 2022-08-25 21:28
 **/
@Slf4j
public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
//        if (args.length != 2) {
//            log.info("usage:{}", EchoClient.class.getSimpleName());
//        }
//        String host = args[0];
//        int port = Integer.parseInt(args[1]);
        new EchoClient("127.0.0.1", 8888).start();
    }

    private void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 创建Bootstrap
            Bootstrap b = new Bootstrap();
            // 指定group以处理客户端事件，需要适用于Nio实现
            b.group(group)
                    // Nio传输的channel类型
                    .channel(NioSocketChannel.class)
                    // 设置服务器
                    .remoteAddress(new InetSocketAddress(host, port))
                    // 在创建channel时，向channelPipeline中添加一个EchoClientHandler实例
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new EchoClientHandler());
                        }
                    });

            // 连接到远程节点，阻塞直到连接完成
            ChannelFuture future = b.connect().sync();
            // 阻塞，直到channel关闭
            future.channel().closeFuture().sync();
        } finally {
            // 关闭线程池并释放资源
            group.shutdownGracefully().sync();
        }
    }
}
