package test.dmall.tcpproxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;

public class ProxyTest {

    public void run(int port) throws Exception {

        ServerBootstrap b = new ServerBootstrap();
        b.group(new NioEventLoopGroup(1), new NioEventLoopGroup(1)).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();

//                        SslContext sslContext = SSLContextFactory.getSslContext("dmall.com");
//                        SSLEngine engine = sslContext.newEngine(socketChannel.alloc());

                        //pipeline.addFirst("ssl", new SslHandler(engine));

                        pipeline.addLast(new TCPNetty4ServerHandler());

                    }
                });

        ChannelFuture channelFuture = b.bind( port);
//        ChannelFuture channelFuture = b.bind(new InetSocketAddress("0.0.0.0", port));
        channelFuture.syncUninterruptibly();
        Channel serverChannel = channelFuture.channel();//main线程不会堵塞
        System.out.println("---------------" + serverChannel.isActive());
    }


    public static void main(String[] args) throws Exception {
        int port = 8082;
        new ProxyTest().run(port);


        Thread.sleep(300000);
    }

}
