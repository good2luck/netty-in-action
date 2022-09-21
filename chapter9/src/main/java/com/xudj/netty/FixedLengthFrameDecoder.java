package com.xudj.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @program: netty-in-action
 * @description: 入站字节转换成消息
 * @author: xudj
 * @create: 2022-08-29 21:05
 **/
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

    /**
     * 要生成帧的长度
     */
    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException("frameLength must be a positive int:" + frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 检查是否有足够的帧大小，生成下一帧
        while (byteBuf.readableBytes() >= frameLength) {
            // 从byteBuf读取一个新帧
            ByteBuf buf = byteBuf.readBytes(frameLength);
            // 将该帧添加到已被解码的消息列表中
            list.add(buf);
        }
    }


}
