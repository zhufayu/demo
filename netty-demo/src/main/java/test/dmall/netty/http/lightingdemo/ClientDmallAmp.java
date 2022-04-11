package test.dmall.netty.http.lightingdemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import test.dmall.netty.http.DemoGenericFutureListener;

import java.net.InetSocketAddress;
import java.net.URI;

public class ClientDmallAmp {
    ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;

    public static void main(String[] args) throws Exception {
        InetSocketAddress address = new InetSocketAddress("10.49.16.205", 8080);
        //InetSocketAddress address = new InetSocketAddress("www.baidu.com", 80);

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap strap = new Bootstrap();
        strap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);


        SimpleChannelPool pool = new SimpleChannelPool(strap.remoteAddress(address), new ChannelPoolHandler() {
            @Override
            public void channelReleased(Channel ch) throws Exception {
                System.out.println("channelReleased channel id: " + ch.id());
            }

            @Override
            public void channelAcquired(Channel ch) throws Exception {
                System.out.println("channelAcquired channel id: " + ch.id());
            }

            @Override
            public void channelCreated(Channel channel) throws Exception {
                System.out.println("channelCreated channel id: " + channel.id());

            }
        });

        Future<Channel> f = pool.acquire();
        f.addListener((GenericFutureListener<Future<Channel>>) future -> {
            System.out.println("GenericFutureListener");

            Channel channel = future.get();
            channel.pipeline().addLast(new HttpClientCodec())
                    .addLast(new HttpObjectAggregator(1024 * 1024))
                    .addLast(new HttpClientHandler());

            URI uri = new URI("/s?wd=netty");

            DefaultHttpHeaders httpHeaders = new DefaultHttpHeaders();
            httpHeaders.add("user-agent", "mock request");
            httpHeaders.add("host", "devamp.dmall.com");
            httpHeaders.add("USER_HEADER", "USER_VALUE");
            DefaultHttpRequest req = new DefaultHttpRequest(HttpVersion.HTTP_1_1,   HttpMethod.valueOf("GET"), "", httpHeaders);


            channel.write(req).addListener(new DemoGenericFutureListener());
            channel.flush();

            pool.release(channel);
        });

        Thread.sleep(200000);
    }
}
