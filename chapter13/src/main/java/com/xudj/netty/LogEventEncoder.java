package com.xudj.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @program: netty-in-action
 * @description: 编码LogEvent成DatagramPacket，以便传输
 * @author: xudj
 * @create: 2022-09-03 19:07
 **/
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {

    private final InetSocketAddress remoteAddress;

    // LogEventEncoder 创建了即将被发送到remoteAddress 的DatagramPacket消息
    public LogEventEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent msg, List<Object> out) throws Exception {
        byte[] files = msg.getLogfile().getBytes(CharsetUtil.UTF_8);

        byte[] msgByte = msg.getMsg().getBytes(CharsetUtil.UTF_8);

        ByteBuf buffer = ctx.alloc().buffer(files.length + msgByte.length + 1);
        // 将文件名写入 ByteBuf
        buffer.writeBytes(files);
        buffer.writeByte(LogEvent.SEPARATOR);
        // 将日志消息添加到 ByteBuf
        buffer.writeBytes(msgByte);

        // 拥有数据和消息地址的 DatagramPacket，添加到出站消息列表中
        out.add(new DatagramPacket(buffer, remoteAddress));
    }
}
