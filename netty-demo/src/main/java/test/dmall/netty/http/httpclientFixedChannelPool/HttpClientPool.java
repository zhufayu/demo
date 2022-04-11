package test.dmall.netty.http.httpclientFixedChannelPool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.net.InetSocketAddress;
import java.net.URI;


public class HttpClientPool {

    ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;

    public static void main(String[] args) throws Exception {
        InetSocketAddress address = new InetSocketAddress("www.baidu.com", 80);

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap strap = new Bootstrap();
        strap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
        SimpleChannelPool pool = new FixedChannelPool(strap.remoteAddress(address), new HttpChannelPoolHandler(), 2);

        for (int i = 0; i < 10; i++) {
            new Runnable() {
                @Override
                public void run() {
                    Future<Channel> f = pool.acquire();
                    f.addListener((FutureListener<Channel>) future -> {
                        System.out.println("============in listener");
                        Channel channel = future.get();

                        URI uri = new URI("/s?wd=netty");
                        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
                        channel.writeAndFlush(request);

                        pool.release(channel);
                    });
                }
            }.run();
            System.out.println("--------------in pool acquire");

        }

        Thread.sleep(200000);
    }
}
