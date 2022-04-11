package test.dmall.tcpproxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DownStreamClientHandler extends ChannelInboundHandlerAdapter {
    private Channel serverChannel;

    public DownStreamClientHandler(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Client channelRead++++++++++++++++++++++" + msg);
        serverChannel.writeAndFlush(msg);
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client channelUnregistered++++++++++++++++++++++");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Client exceptionCaught++++++++++++++++++++++");
        ctx.close();
    }
}
