package test.dmall.tcpproxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import static io.netty.buffer.Unpooled.copiedBuffer;


@ChannelHandler.Sharable
public class TCPNetty4ServerHandler extends ChannelInboundHandlerAdapter {
    private Channel backendChannel;
    private Channel serverChannel;

    private Bootstrap bootstrap = new Bootstrap();


    private void openBackEnd(){
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new DownStreamClientHandler(serverChannel));
                    }
                });
    }

    private Channel connectBackEnd() {
        openBackEnd();
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect("www.baidu.com", 80).sync();
        } catch (Exception e) {

            return null;
        }
        Channel channel = channelFuture.channel();
        return channel;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead++++++++++++++++++++++" + msg);

        //将前端连接数据刷到后端
        backendChannel.writeAndFlush(msg);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelRegistered++++++++++++++++++++++");
        //把服务端连接传入后端连接
        serverChannel = ctx.channel();

        //客户端注册上来就创建后端连接
        backendChannel = connectBackEnd();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelUnregistered++++++++++++++++++++++");
        //客户端断开就断开后端连接
        backendChannel.close();
        serverChannel.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught++++++++++++++++++++++");
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 空闲超时直接关闭连接
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private void handshake(ChannelHandlerContext ctx) {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        System.out.println("handshake++++++++++++++++++++++");
                    }
                });
    }
}
