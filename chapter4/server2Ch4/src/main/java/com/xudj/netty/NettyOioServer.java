package com.xudj.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @program: netty-in-action
 * @description: 使用netty的阻塞网络处理
 * @author: xudj
 * @create: 2022-08-27 11:26
 **/
@Slf4j
public class NettyOioServer {

    public void serve (int port) throws Exception {
        final ByteBuf byteBuf = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("hi", Charset.forName("utf-8")));

        EventLoopGroup eventLoopGroup = new OioEventLoopGroup();

        try {
            // 创建serverBootStrap
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(eventLoopGroup)
                    // 使用OioServerSocketChannel以允许阻塞模式
                    .channel(OioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    // 指定ChannelInitializer，每个已接受的连接都会调用
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    // 将消息写回客户端，并添加ChannelFutureListener，以便消息写完便关闭连接
                                    ctx.writeAndFlush(byteBuf.duplicate())
                                            .addListener(ChannelFutureListener.CLOSE);

                                }
                            });
                        }
                    });
            // 阻塞连接
            ChannelFuture f = bootstrap.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            // 资源释放
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

}
