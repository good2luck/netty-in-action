package com.xudj.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @program: netty-in-action
 * @description: 初始化Pipeline
 * @author: xudj
 * @create: 2022-09-02 07:55
 **/
public class ChatServerInitializer extends ChannelInitializer {

    private final ChannelGroup channels;

    public ChatServerInitializer(ChannelGroup channels) {
        this.channels = channels;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        // 将所有的handler添加到pipeline中
        ChannelPipeline pipeline = ch.pipeline();
        // HttpRequest、HttpContent、LastHttpContent 与字节的相互编解码
        pipeline.addLast(new HttpServerCodec())
                // 文件处理
                .addLast(new ChunkedWriteHandler())
                // 将多个HttpMessage 聚合 FullHttpRequest、FullHttpResponse
                .addLast(new HttpObjectAggregator(64 * 1024)) // 64K
                // 处理FullHttpRequest
                .addLast(new HttpRequestHandler("/ws"))
                // 处理升级握手
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                // 处理 TextWebSocketFrame 和握手完成事件
                .addLast(new TextWebSocketFrameHandler(channels));
    }
}
