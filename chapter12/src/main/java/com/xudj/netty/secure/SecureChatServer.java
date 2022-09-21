package com.xudj.netty.secure;

import com.xudj.netty.ChatServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.net.InetSocketAddress;

/**
 * @program: netty-in-action
 * @description: 加密服务
 * 注意：测试发现需要https才行，https://localhost:8889
 *
 * @author: xudj
 * @create: 2022-09-02 21:54
 **/
public class SecureChatServer extends ChatServer {

    private final SslContext sslContext;

    public SecureChatServer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    public ChannelInitializer<Channel> createInitializer(ChannelGroup channels) {
        return new SecureChatServerInitializer(channels, sslContext);
    }

    public static void main(String[] args) {
        int port = 8889;

        try {
            SelfSignedCertificate certificate = new SelfSignedCertificate();
            SslContext sslContext = SslContext.newServerContext(certificate.certificate(), certificate.privateKey());

            SecureChatServer secureChatServer = new SecureChatServer(sslContext);
            ChannelFuture future = secureChatServer.start(new InetSocketAddress(port));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                secureChatServer.destroy();
            }));
            future.channel().closeFuture().syncUninterruptibly();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
