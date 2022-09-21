package com.xudj.netty.secure;

import com.xudj.netty.ChatServerInitializer;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * @program: netty-in-action
 * @description: 为pipeline添加加密
 * @author: xudj
 * @create: 2022-09-02 21:47
 **/
public class SecureChatServerInitializer extends ChatServerInitializer {

    private final SslContext sslContext;

    public SecureChatServerInitializer(ChannelGroup channels, SslContext sslContext) {
        super(channels);
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        // 调用父类的initChannel方法
        super.initChannel(ch);

        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        sslEngine.setUseClientMode(false);
        // 将SslHandler添加pipeline中
        ch.pipeline().addLast(new SslHandler(sslEngine));
    }
}
