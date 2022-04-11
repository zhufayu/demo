package test.dmall.httpproxy.bootstrap;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.dmall.httpproxy.servlet.DemoServlet;
import test.dmall.httpproxy.servlet.DemoServletContextListener;


import javax.servlet.ServletException;
import java.io.File;

public class TomcatStart {
    private static final Logger logger = LoggerFactory.getLogger(TomcatStart.class);
    public static int TOMCAT_PORT = 8088;

    public static String CODING_UTF = "utf-8";
    static int CPU_CORES = 4;


    public static void main(String[] args) throws ServletException, LifecycleException {
        TomcatStart.run();
    }

    public static void run() throws ServletException, LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(TomcatStart.TOMCAT_PORT);
//        tomcat.setBaseDir(".");

        StandardContext myCtx = (StandardContext) tomcat.addWebapp("", new File(".").getAbsolutePath());
        myCtx.setReloadable(false);

        // 上下文监听器
//        myCtx.addLifecycleListener(new AprLifecycleListener());
        myCtx.addApplicationLifecycleListener(new DemoServletContextListener());


        // 注册servlet
        tomcat.addServlet(myCtx, "demoServlet", new DemoServlet());
        // servlet mapping
        myCtx.addServletMapping("/*", "demoServlet");

        tomcat.getConnector().setURIEncoding(CODING_UTF);

        ProtocolHandler protocolHandler = tomcat.getConnector().getProtocolHandler();
        if (protocolHandler instanceof AbstractProtocol) {

            int threadCountBase = Math.max(8, CPU_CORES * 2);
            int minThreadCount = threadCountBase / 2;
            int maxThreadCount = threadCountBase;
            AbstractProtocol protocol = ((AbstractProtocol) protocolHandler);

            protocol.setMinSpareThreads(minThreadCount);
            protocol.setMaxThreads(maxThreadCount);
            logger.info("Set tomcat http exec thread count to {} - {} for {} cpu cores machine", minThreadCount, maxThreadCount, CPU_CORES);
            protocol.setAcceptorThreadCount(1);
            logger.info("Set tomcat http acceptor thread count to {} for {} cpu cores machine", 1, CPU_CORES);

            if (protocol instanceof Http11NioProtocol) {
                ((Http11NioProtocol) protocol).setPollerThreadCount(minThreadCount);
                ((Http11NioProtocol) protocol).setMaxKeepAliveRequests(100000);
            }
            logger.info("Set tomcat http client poller thread count to {} for {} cpu cores machine", minThreadCount, CPU_CORES);

            protocol.setMaxConnections(30000);
        }
        tomcat.start();
        tomcat.getServer().await();
    }
}
