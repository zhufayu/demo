package test.mock;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.metadata.identifier.MetadataIdentifier;
import org.apache.dubbo.metadata.store.zookeeper.ZookeeperMetadataReport;
import org.apache.dubbo.remoting.zookeeper.ZookeeperTransporter;

public class MyZookeeperMetadataReport extends ZookeeperMetadataReport {
    public MyZookeeperMetadataReport(URL url, ZookeeperTransporter zookeeperTransporter) {
        super(url, zookeeperTransporter);
    }

    public void doStoreMetadata(MetadataIdentifier providerMetadataIdentifier, String serviceDefinitions) {
        doStoreProviderMetadata(providerMetadataIdentifier, serviceDefinitions);
    }
}
