package test.dmall.httpproxy.servlet;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DemoServletContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(DemoServletContextListener.class);
    private static BeanManager beanManager = BeanManager.getInstance();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("DemoServletContextListener get context event" + servletContextEvent.toString());
        beanManager.setInitialization(servletContextEvent.toString());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
