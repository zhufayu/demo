package test.mock;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.zookeeper.ZookeeperRegistry;

import java.util.ArrayList;
import java.util.HashMap;

public class MockProvider2Main {

    public static void main(String[] args) throws Exception {
        URL url = URL.valueOf("zookeeper://10.248.224.74:2181?backup=10.248.224.66:2181,10.248.224.72:2181");
        HashMap<String, ZookeeperRegistry> hashMap = new HashMap<>();
        int numService = 2;
        int instanceNum = 5;
        String prefix = "test.service.gen.SamplingTaskRequestDemoService";
        int port = 20880;
        for (int i = 0; i < numService; i++) {
            String mockInterface = prefix + i;
            ZookeeperRegistry registry = new ZookeeperRegistry(url, new MyAbstractZookeeperTransporter());

            hashMap.put(mockInterface, registry);

            for (int j = 0; j < instanceNum; j++) {
                String ip = "10.16.245.21:" + (port + j);

                long timestamp = new java.util.Date().getTime();

                String dubboUrlStr = "dubbo://" + ip + "/" + mockInterface
                        + "?anyhost=true&application=luckymock&default=true&deprecated=false&dubbo=2.0.2&dynamic=true&generic=true" + mockInterface
                        + "&methods=*&pid=9132&release=0.0.3-SNAPSHOT&sdk_version=2.7.2&side=provider&timestamp=" + timestamp;

                URL dubboUrl = URL.valueOf(dubboUrlStr);
                registry.doRegister(dubboUrl);
                System.out.println(mockInterface + ":" + ip);
            }
        }
        Thread.sleep(100 * 1000);
    }
}
