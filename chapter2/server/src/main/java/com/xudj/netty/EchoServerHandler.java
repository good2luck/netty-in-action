package com.xudj.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.logging.Logger;

/**
 * @program: netty-in-action
 * @description:
 * @author: xudj
 * @create: 2022-08-25 07:49
 **/
@ChannelHandler.Sharable
@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 每个传入的消息都会调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        log.info("channelRead:{}", in.toString(CharsetUtil.UTF_8));
        // 将接收到消息写给发送者，而不冲刷出站消息
        ctx.write(in);
    }

    /**
     * 当前批次读取完成
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }


    /**
     * 读取操作期间异常会被调用
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
