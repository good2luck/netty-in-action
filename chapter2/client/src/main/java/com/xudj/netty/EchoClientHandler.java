package com.xudj.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: netty-in-action
 * @description:
 * @author: xudj
 * @create: 2022-08-25 21:12
 **/
@ChannelHandler.Sharable
@Slf4j
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {


    /**
     * 当连接建立时，被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 当被通知channel活跃的时候，发送一条消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello netty", CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        // 记录已接收消息的转储
        log.info("client receive:{}", byteBuf.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常发生是打印，并关闭channel
        cause.printStackTrace();
        ctx.close();
    }
}
