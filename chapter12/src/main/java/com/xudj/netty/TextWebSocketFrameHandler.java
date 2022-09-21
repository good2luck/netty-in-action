package com.xudj.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * @program: netty-in-action
 * @description: 处理文本帧
 * @author: xudj
 * @create: 2022-09-02 07:45
 **/
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup channels;

    public TextWebSocketFrameHandler(ChannelGroup channels) {
        this.channels = channels;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // 握手成功事件，则移除HttpRequestHandler，因为其不会接收到http消息了
            ctx.pipeline().remove(HttpRequestHandler.class);
            // 通知其它已连接上的客户端，新的webSocket客户端已经连接
            channels.writeAndFlush(new TextWebSocketFrame("client " + ctx.channel() + " joined."));
            // 新的webSocket客户端加入的组中，以接收其它所有消息
            channels.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 添加消息的引用计数
        TextWebSocketFrame retain = msg.retain();
        // 并将消息写到所有已经连接的客户端
        channels.writeAndFlush(retain);
    }
}
