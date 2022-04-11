package test.dmall.netty.server_client.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static test.dmall.netty.server_client.client.Constant.nowNum;

public class NettyClientNew {
    //服务端的IP
    private static String SERVER_HOST = "10.12.214.27";
    private static int PORT = 9000;


    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String dHost = System.getProperty("server.host");
        if (null != dHost && dHost.length() > 0) {
            SERVER_HOST = dHost;
        }
        String dPort = System.getProperty("server.port");
        if (null != dPort && dPort.length() > 0) {
            PORT = Integer.valueOf(dPort);
        }

        String num = System.getProperty("thread.num");
        int count = 3;
        if (null != num && num.length() > 0) {
            count = Integer.valueOf(num);
        }

        test(PORT,count);

        Thread.sleep(120000);
        System.exit(0);
    }

    public static void test(int port,int count) throws ExecutionException, InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addFirst(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        Constant.channelConcurrentHashMap.put(ctx.channel().toString(), ctx.channel());
                    }

                    @Override
                    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                        Constant.channelConcurrentHashMap.remove(ctx.channel().toString());
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        System.out.println("error: " + cause.getMessage());
                    }
                });
            }
        });

        System.out.println("client starting....");
        for (int i = 0; i < 5; i++) {
            ChannelFuture channelFuture = bootstrap.connect(SERVER_HOST, port);
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    System.out.println("connect failed");
                }
            });
            channelFuture.get();
        }

        ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(4,
                new BasicThreadFactory.Builder().namingPattern("send-msg-schedule-pool-%d").daemon(true).build());
        Constant.sendMessage(executorService);
    }
}
