package com.xudj.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @program: netty-in-action
 * @description: 出站消息测试
 * @author: xudj
 * @create: 2022-08-29 21:59
 **/
public class AbsIntegerEncoderTest {


    @Test
    public void testEncoder () {
        // 创建一个ByteBuf，并写入9个负整数
        ByteBuf byteBuf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            byteBuf.writeInt(i * -1);
        }

        // 创建EmbeddedChannel，并安装要测试的AbsIntegerEncoder
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder(4));
        // 写入数据
        assertTrue(channel.writeOutbound(byteBuf));
        // 标记channel完成
        assertTrue(channel.finish());

        // read byte
        for (int i = 1; i < 10; i++) {
            // 读取出站端消息，并断言包括了对应绝对值
            int r = channel.readOutbound();
            assertEquals(i, r);
        }

        assertNull(channel.readOutbound());

    }

}
