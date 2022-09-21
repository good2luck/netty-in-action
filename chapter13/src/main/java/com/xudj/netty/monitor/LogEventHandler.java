package com.xudj.netty.monitor;

import com.xudj.netty.LogEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @program: netty-in-action
 * @description: 对 LogEventDecoder 解码出来的 logEvent 执行一系列出来
 * @author: xudj
 * @create: 2022-09-03 21:32
 **/
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent logEvent) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(logEvent.getReceivedTimestamp())
                .append(" [")
                .append(logEvent.getSource().toString())
                .append("] [")
                .append(logEvent.getLogfile())
                .append("] : ")
                .append(logEvent.getMsg());
        // 打印
        System.out.println(sb.toString());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
