package test.dmall.httpproxy.servlet;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.dmall.netty.http.DemoGenericFutureListener;
import test.dmall.httpproxy.downstream.ProxyDownStreamClientHandler;
import test.dmall.httpproxy.porxy.*;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Enumeration;

public class DemoServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DemoServlet.class);
    public static final AttributeKey<DemoContext> CONTEXT_KEY = AttributeKey.valueOf("context_key");

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        RequestThroughputMetrics.incrementAndGetIncomingFlows();
        DemoContext context = new DemoContext((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);

        InetSocketAddress address = new InetSocketAddress("www.baidu.com", 80);

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
            System.out.println("GenericFutureListener==========");

            Channel channel = future.get();
            channel.pipeline().addLast(new HttpClientCodec())
                    .addLast(new HttpObjectAggregator(1024 * 1024))
                    .addLast(new ProxyDownStreamClientHandler(context));

            //获取用户请求Header和url
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            DefaultHttpHeaders httpHeaders = new DefaultHttpHeaders();
            Enumeration headerNames = httpServletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = (String) headerNames.nextElement();
                String value = httpServletRequest.getHeader(key);
                if(StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
                    httpHeaders.add(key, value);
                }
            }

            String url = HttpUtils.generateUrl(httpServletRequest);
            String queryString = HttpUtils.generateQueryString(httpServletRequest);
            if(StringUtils.isNotEmpty(queryString)) {
                url += Constants.SEPARATOR_FOR_QUESTION_MARK + queryString;
            }

            //发送请求header和url
            DefaultHttpRequest req = new DefaultHttpRequest(HttpVersion.HTTP_1_1,   HttpMethod.valueOf(httpServletRequest.getMethod()),  HttpUtils.formatUrlToUri(url), httpHeaders);
            channel.write(req).addListener(new DemoGenericFutureListener());

            if (servletRequest.getInputStream().isFinished()) {
                channel.writeAndFlush(new DefaultLastHttpContent())
                        .addListener(new DemoGenericFutureListener());
            } else {

                //发送请求content
                ServletInputStream is = servletRequest.getInputStream();
                byte[] buffer = new byte[1024];
                int length;
                while (channel.isWritable() && (is.isReady() && (length = is.read(buffer)) != -1)) {
                    channel.write(new DefaultHttpContent(Unpooled.copiedBuffer(buffer, 0, length)))
                            .addListener(new DemoGenericFutureListener());
                }
            }
            channel.flush();

            pool.release(channel);
        });

    }
}
