package com.xudj.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

/**
 * @program: netty-in-action
 * @description: 广播者 引导服务器
 * 启动后，
 * 命令行 执行
 * cd /Users/xudejian
 * echo cc >> messageText.xml
 *
 * @author: xudj
 * @create: 2022-09-03 19:18
 **/
public class LogEventBroadcaster {

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;


    public LogEventBroadcaster(InetSocketAddress address, File file) {
        group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(group)
                // 引导NioDatagramChannel，无状态的
                .channel(NioDatagramChannel.class)
                // SO_BROADCAST 套接字选项
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public void run () throws Exception {
        // 绑定channel
        Channel channel = bootstrap.bind(0).sync().channel();
        // 文件指针
        long pointer = 0;
        // 循环处理
        for (;;) {
            long len = file.length();
            if (len < pointer) {
                // 文件指针设置成文件的最后一个字节
                pointer = len;
            } else if (len > pointer){
                RandomAccessFile raf = new RandomAccessFile(this.file, "r");
                // 设置当前的文件指针
                raf.seek(pointer);

                String line;
                while ((line = raf.readLine()) != null) {
                    // 对于每一个日志条目，写入一个LogEvent到channel中
                    channel.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
                }
                // 读取当前的文件指针
                pointer = raf.getFilePointer();
                raf.close();
            }
            try {
                // 睡眠1s，如果被中断，则退出循环，否则循环处理
                Thread.sleep(1000);
            } catch (Exception e) {
                Thread.interrupted();
                break;
            }
        }

    }

    public void stop () {
        // 关闭资源
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(
                new InetSocketAddress("255.255.255.255", 8890),
                new File("/Users/xudejian/messageText.xml"));

        try {
            broadcaster.run();
        } finally {
            broadcaster.stop();
        }

    }

}
