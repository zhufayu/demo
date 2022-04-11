package test.mock;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.zookeeper.ZookeeperClient;
import org.apache.dubbo.remoting.zookeeper.curator.CuratorZookeeperClient;
import org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter;

public class MyAbstractZookeeperTransporter extends AbstractZookeeperTransporter {
    @Override
    protected ZookeeperClient createZookeeperClient(URL url) {
        return new CuratorZookeeperClient(url);
    }
}
