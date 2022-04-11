package test.compare;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import test.helper.Constant;

import java.util.Date;
import java.util.Objects;
import java.util.Properties;

public class TestWatchNode {

    private static void watchZk(String service) throws Exception {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(Constant.zkhost)
                .retryPolicy(new RetryNTimes(1, 3000))
                .connectionTimeoutMs(5000);

        CuratorFramework client = builder.build();
        client.getConnectionStateListenable().addListener((clientInstance, state) -> {
            if (state == ConnectionState.LOST) {
                System.out.println("zk address: {} client state LOST");
            } else if (state == ConnectionState.CONNECTED) {
                System.out.println("zk address: {} client state CONNECTED");
            } else if (state == ConnectionState.RECONNECTED) {
                System.out.println("zk address: {} client state RECONNECTED");
            }
        });
        client.start();

        String path = "/dubbo/" + service + "/providers";
        TreeCache treeCache = new TreeCache(client, path);
        treeCache.start();

        Objects.requireNonNull(treeCache).getListenable().addListener((client1, event) -> {
            System.out.println("========zk" + " on change at: " + new Date() + " event: " + event);
        });

    }

    private static void watchNacos(String service) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", Constant.nacosPort);
        properties.setProperty("namespace", "public");
        NamingService naming = NamingFactory.createNamingService(properties);

        String path = "providers:" + service + "::";
        naming.subscribe(path, new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println("********nacos" + " on change at: " + new Date() + " event: " + ((NamingEvent) event).getServiceName() + ((NamingEvent) event).getInstances());
            }
        });
    }

    public static void main(String[] args) throws Exception {
        String service = "test.service.gen.DemoService0";
        watchZk(service);
        watchNacos(service);

        Thread.sleep(1000000);
    }
}
