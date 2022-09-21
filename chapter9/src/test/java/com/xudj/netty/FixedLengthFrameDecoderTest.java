package com.xudj.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @program: netty-in-action
 * @description: 测试入站消息
 * @author: xudj
 * @create: 2022-08-29 21:22
 **/
public class FixedLengthFrameDecoderTest {

    @Test
    public void testFrameDecoder() {
        // 创建byteBuf, 并写入9个字节
        ByteBuf byteBuf = Unpooled.buffer();
        for (int i = 0 ; i < 9; i++) {
            byteBuf.writeByte(i);
        }

        // 创建EmbeddedChannel
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));

        ByteBuf duplicate = byteBuf.duplicate();
        // 写字节, 将数据写入embeddedChannel
        assertTrue(embeddedChannel.writeInbound(duplicate.retain()));

        // 标记channel已完成
        assertTrue(embeddedChannel.finish());

        // read msg
        // 读取生成的消息，验证是否有3帧，其中每帧有3个字节
        ByteBuf read = embeddedChannel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        read = embeddedChannel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        read = embeddedChannel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        assertNull(embeddedChannel.readInbound());
        byteBuf.release();
    }


    @Test
    public void testFrameDecoder2 () {
        ByteBuf byteBuf = Unpooled.buffer();
        for (int i = 0 ; i < 9; i++) {
            byteBuf.writeByte(i);
        }

        // 创建EmbeddedChannel
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));

        ByteBuf input = byteBuf.duplicate();
        // 写字节, 将数据写入embeddedChannel
        // 此处为false，没有完整的帧可供写
        assertFalse(embeddedChannel.writeInbound(input.readBytes(2)));
        assertTrue(embeddedChannel.writeInbound(input.readBytes(7)));

        // 标记channel已完成
        assertTrue(embeddedChannel.finish());

        // read msg
        // 读取生成的消息，验证是否有3帧，其中每帧有3个字节
        ByteBuf read = embeddedChannel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        read = embeddedChannel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        read = embeddedChannel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        assertNull(embeddedChannel.readInbound());
        byteBuf.release();

    }

}
