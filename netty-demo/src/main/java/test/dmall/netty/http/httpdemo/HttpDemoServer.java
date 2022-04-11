package test.dmall.netty.http.httpdemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class HttpDemoServer {

    private final int port;
    public static boolean isSSL;

    public HttpDemoServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new HttpDemoServerInitializer());

//            Channel ch = b.bind(port).sync().channel();
//            ch.closeFuture().sync(); //main线程会堵塞
//            System.out.println("---------------");

            ChannelFuture channelFuture = b.bind(port);
            channelFuture.syncUninterruptibly();
            Channel serverChannel = channelFuture.channel();//main线程不会堵塞

            System.out.println("---------------" +serverChannel.isActive() );
            Thread.sleep(300000);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8082;
        }
        if (args.length > 1) {
            isSSL = false;
        }
        new HttpDemoServer(port).run();
    }
}
