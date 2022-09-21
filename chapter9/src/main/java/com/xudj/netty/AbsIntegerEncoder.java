package com.xudj.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @program: netty-in-action
 * @description: 将一种消息转换成另一种消息：将负数转成绝对值
 * @author: xudj
 * @create: 2022-08-29 21:54
 **/
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {

    private final int length;

    public AbsIntegerEncoder(int length) {
        if (length <= 0) {
            length = 4;
        }
        this.length = length;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有足够的字节进行编码
        while (in.readableBytes() >= length) {
            // 从输入的ByteBuf中读取下一个整数，并绝对值
            int i = in.readInt();
            int value = Math.abs(i);
            // 写入编码消息中
            out.add(value);
        }
    }

}
