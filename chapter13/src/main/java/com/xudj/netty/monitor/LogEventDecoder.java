package com.xudj.netty.monitor;

import com.xudj.netty.LogEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @program: netty-in-action
 * @description: 解码器 将传入的DatagramPacket 转为 LogEvent
 * @author: xudj
 * @create: 2022-09-03 21:25
 **/
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        // DatagramPacket中的数据
        ByteBuf byteBuf = msg.content();

        // SEPARATOR 的索引
        int indexOf = byteBuf.indexOf(0, byteBuf.readableBytes(), LogEvent.SEPARATOR);

        // 文件名
        String fileName = byteBuf.slice(0, indexOf)
                .toString(CharsetUtil.UTF_8);
        // 日志内容
        String logMsg = byteBuf.slice(indexOf + 1, byteBuf.readableBytes() - indexOf - 1)
                .toString(CharsetUtil.UTF_8);

        // 构建一个LogEvent，并添加到已经解码的消息列表中
        LogEvent logEvent = new LogEvent(msg.sender(), System.currentTimeMillis(),
                fileName, logMsg);

        out.add(logEvent);
    }
}
