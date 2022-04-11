package test.dmall.netty.server_client.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    //服务端的IP
    private static String SERVER_HOST = "10.12.214.27";
    private static int PORT = 9000;
    private static int CONNECT_NUM = 1;

    private static boolean SEND_MSG = true;

    public static void main(String[] args) {
        String dHost = System.getProperty("server.host");
        if (null != dHost && dHost.length() > 0) {
            SERVER_HOST = dHost;
        }
        String dPort = System.getProperty("server.port");
        if (null != dPort && dPort.length() > 0) {
            PORT = Integer.valueOf(dPort);
        }

        String cNum = System.getProperty("connect.num");
        if (null != cNum && cNum.length() > 0) {
            CONNECT_NUM = Integer.valueOf(cNum);
        }

        String sendMsg = System.getProperty("send.msg");
        if (null != sendMsg && sendMsg.length() > 0 && "true".equals(sendMsg)) {
            SEND_MSG = true;
        }

        new NettyClient().start(PORT);
    }

    public void start(int port) {
        System.out.println("client starting....");
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup);

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addFirst(new TCPNetty4ClientHandler());
            }
        });
        for (int i = 0; i < 1; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.interrupted() && Constant.nowNum.get() < CONNECT_NUM) {
                        try {
                            ChannelFuture channelFuture = bootstrap.connect(SERVER_HOST, port);
                            channelFuture.addListener((ChannelFutureListener) future -> {
                                if (!future.isSuccess()) {
                                    System.out.println("connect failed");
                                }
                            });
                            channelFuture.get();
                        } catch (Exception e) {
                        }
                        Constant.nowNum.getAndIncrement();
                    }
                }
            }).start();
        }

        if (SEND_MSG) {
            ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(4,
                    new BasicThreadFactory.Builder().namingPattern("send-msg-schedule-pool-%d").daemon(true).build());

            Constant.sendMessage(executorService);
        }
    }

}
