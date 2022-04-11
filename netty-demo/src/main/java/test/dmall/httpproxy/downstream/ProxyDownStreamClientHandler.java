package test.dmall.httpproxy.downstream;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import test.dmall.httpproxy.porxy.DemoContext;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ProxyDownStreamClientHandler extends ChannelInboundHandlerAdapter {
    private DemoContext context;

    public ProxyDownStreamClientHandler(DemoContext demoContext) {
        this.context = demoContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Client channelRead++++++++++++++++++++++" + msg);

        //从下游获取到内容发送给response
        HttpServletResponse httpServletResponse = context.getServletResponse();
        if (msg instanceof HttpResponse) {
            HttpResponse resp = (HttpResponse) msg;
            Iterator<Map.Entry<String, String>> it = resp.headers().iteratorAsString();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                httpServletResponse.addHeader(entry.getKey(), entry.getValue());
            }
            httpServletResponse.setStatus(resp.status().code());
        } else if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            int length = content.content().readableBytes();
            if (length > 0) {
                byte[] chunk = new byte[length];
                content.content().readBytes(chunk);
                Queue<byte[]> buffer = new LinkedList<>();
                buffer.offer(chunk);

                ServletOutputStream os = httpServletResponse.getOutputStream();
                while (os.isReady()) {
                    if (!buffer.isEmpty()) {
                        os.write(buffer.poll());
                    }
                }
            } else {
                httpServletResponse.flushBuffer();
            }
        }
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
