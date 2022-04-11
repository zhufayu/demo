package test;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.metadata.definition.model.ServiceDefinition;
import org.apache.dubbo.metadata.identifier.MetadataIdentifier;
import org.apache.dubbo.metadata.store.MetadataReport;
import org.apache.dubbo.metadata.store.MetadataReportFactory;
import org.apache.dubbo.metadata.store.zookeeper.ZookeeperMetadataReport;
import org.apache.dubbo.registry.zookeeper.ZookeeperRegistry;
import test.helper.Constant;
import test.mock.MyAbstractZookeeperTransporter;
import test.mock.MyZookeeperMetadataReport;


import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class AllProvider {
    private static ConcurrentHashMap<String, ZookeeperRegistry> concurrentHashMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ZookeeperMetadataReport> concurrentMetadataMap = new ConcurrentHashMap<>();

    private static Random rand = new Random();

    public static void mockZkReg(String servicePrefix, int from, int end, int port) {
        URL url = URL.valueOf(Constant.zkAdd);

        String mockIp = "10." + rand.nextInt(255) + "." + rand.nextInt(255) + ".10";
        String ip = mockIp + ":" + port;

        ZookeeperRegistry registry = new ZookeeperRegistry(url, new MyAbstractZookeeperTransporter());
        concurrentHashMap.put(ip, registry);

        for (int i = from; i < end; i++) {
            String mockInterface = servicePrefix + i;

            long timestamp = new java.util.Date().getTime();

            // dubbo://10.12.202.232:20880/
            // test.service.DemoService1
            // ?anyhost=true&application=test-provider-xxx&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&interface=test.service.DemoService1
            // &methods=getPermissions,sayHelloAsync,asyncInvoke,helloOnEvent&pid=22204&release=0.0.3-SNAPSHOT&revision=1.0.0&sdk_version=2.7.2&side=provider&threads=200&timestamp=1623052003923&version=1.0.0

            String dubboUrlStr = "dubbo://" + ip + "/" + mockInterface
                    + "?anyhost=true&application=test-demoService-application&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&interface=" + mockInterface
                    + "&methods=getPermissions,sayHelloAsync,asyncInvoke,helloOnEvent&pid=22204&release=0.0.3-SNAPSHOT&revision=1.0.0&sdk_version=2.7.2&side=provider&threads=200&timestamp=" + timestamp;

            URL dubboUrl = URL.valueOf(dubboUrlStr);
            registry.doRegister(dubboUrl);

            System.out.println("ZkReg: " + Constant.df.format(new Date()) + " id: " + mockInterface + " ip:" + ip);

        }
    }

    public static void mockZkRegConsumer(String servicePrefix, int from, int end, int port) {
        URL url = URL.valueOf(Constant.zkAdd);

        String mockIp = "20." + rand.nextInt(255) + "." + rand.nextInt(255) + "." + port;
        String ip = mockIp;

        ZookeeperRegistry registry = new ZookeeperRegistry(url, new MyAbstractZookeeperTransporter());
        concurrentHashMap.put(ip, registry);

        for (int i = from; i < end; i++) {
            String mockInterface = servicePrefix + i;
            long timestamp = new java.util.Date().getTime();

            String dubboUrlStr = "consumer://" + ip + "/" + mockInterface
                    + "?application=test-demoService-application&category=consumers&check=false&dubbo=2.0.2&interface=" + mockInterface
                    + "&lazy=false&methods=getPermissions,sayHelloAsync,asyncInvoke,helloOnEvent&pid=38384" +
                    "&release=0.0.3-SNAPSHOT&revision=1.0.0&sdk_version=2.7.2&side=consumer&sticky=false&version=1.0.0&timestamp=1623139728374" + timestamp;

            URL dubboUrl = URL.valueOf(dubboUrlStr);
            registry.doRegister(dubboUrl);

            System.out.println("ZkConsumer: " + Constant.df.format(new Date()) + " id: " + mockInterface + " ip:" + ip);
        }
    }

    private static void mockMetaData(String servicePrefix, int from, int end,String side) {
        URL url = URL.valueOf(Constant.zkAdd);
        MyZookeeperMetadataReport report = new MyZookeeperMetadataReport(url, new MyAbstractZookeeperTransporter());
        concurrentMetadataMap.put(servicePrefix + "meta" + side, report);
        for (int i = from; i < end; i++) {
            String mockInterface = servicePrefix + i;

            MetadataIdentifier metadataIdentifier = new MetadataIdentifier();
            metadataIdentifier.setServiceInterface(mockInterface);
            metadataIdentifier.setVersion("1.0.0");
            metadataIdentifier.setGroup("testGroup");
            metadataIdentifier.setSide(side);
            metadataIdentifier.setApplication("test-demoService-application");
            String metadata = "{\"parameters\":{\"owner\":\"test_nacos_sync\",\"side\":\"provider\",\"application\":\"test-provider-SamplingTaskRequestDemo\",\"release\":\"0.0.3-SNAPSHOT\",\"methods\":\"*\",\"deprecated\":\"false\",\"dubbo\":\"2.0.2\",\"dynamic\":\"true\",\"interface\":\"test.service.gen.SamplingTaskRequestDemoServiceSamplingTaskRequestDemoServiceSamplingTaskRequestDemoServiceSamplingTaskRequestDemoService0\",\"generic\":\"true\",\"anyhost\":\"true\"},\"canonicalName\":\"test.service.gen.SamplingTaskRequestDemoServiceSamplingTaskRequestDemoServiceSamplingTaskRequestDemoServiceSamplingTaskRequestDemoService0\",\"codeSource\":\"file:/C:/code/testnacos/target/classes/\",\"methods\":[{\"name\":\"getPermissionsGetPermissionsGetPermissions3\",\"parameterTypes\":[\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\"],\"returnType\":\"java.lang.String\"},{\"name\":\"getPermissionsGetPermissionsGetPermissions1\",\"parameterTypes\":[\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\"],\"returnType\":\"java.lang.String\"},{\"name\":\"getPermissionsGetPermissionsGetPermissions6\",\"parameterTypes\":[\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\"],\"returnType\":\"java.lang.String\"},{\"name\":\"getPermissionsGetPermissionsGetPermissions8\",\"parameterTypes\":[\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\"],\"returnType\":\"java.lang.String\"},{\"name\":\"getPermissionsGetPermissionsGetPermissions0\",\"parameterTypes\":[\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\"],\"returnType\":\"java.lang.String\"},{\"name\":\"getPermissionsGetPermissionsGetPermissions4\",\"parameterTypes\":[\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\"],\"returnType\":\"java.lang.String\"},{\"name\":\"getPermissionsGetPermissionsGetPermissions9\",\"parameterTypes\":[\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\"],\"returnType\":\"java.lang.String\"},{\"name\":\"getPermissionsGetPermissionsGetPermissions5\",\"parameterTypes\":[\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\"],\"returnType\":\"java.lang.String\"},{\"name\":\"getPermissionsGetPermissionsGetPermissions7\",\"parameterTypes\":[\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\"],\"returnType\":\"java.lang.String\"},{\"name\":\"getPermissionsGetPermissionsGetPermissions2\",\"parameterTypes\":[\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\"],\"returnType\":\"java.lang.String\"}],\"types\":[{\"type\":\"java.lang.String\",\"properties\":{\"value\":{\"type\":\"char[]\"},\"hash\":{\"type\":\"int\"}}},{\"type\":\"int\"},{\"type\":\"org.apache.dubbo.remoting.zookeeper.ZookeeperClient\"},{\"type\":\"char\"},{\"type\":\"org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter\",\"properties\":{\"zookeeperClientMap\":{\"type\":\"java.util.Map\\u003cjava.lang.String, org.apache.dubbo.remoting.zookeeper.ZookeeperClient\\u003e\"}}}]}";
            report.doStoreMetadata(metadataIdentifier, metadata);

            System.out.println("ZkMetaData: " + Constant.df.format(new Date()) + " id: " + mockInterface + " side: " + side );
        }
    }

    public static void createMetaData(String servicePrefix, int from, int end){
        mockMetaData(servicePrefix,from,end,"provider");
        mockMetaData(servicePrefix,from,end,"consumer");
    }

    public static void testNacosReg(String servicePrefix, int from, int end, int port) throws Exception {
        // int export = port;
        System.out.println("nacos from: " + from + " to: " + end + " port: " + port);

        Properties properties = new Properties();
        properties.setProperty("serverAddr", Constant.nacosPort);
        properties.setProperty("namespace", "public");
        final NamingService naming = NamingFactory.createNamingService(properties);

        //String ip = "10.1.1.23";
        String mockIp = "30." + rand.nextInt(255) + "." + rand.nextInt(255) + ".30";
        for (int i = from; i < end; i++) {
            String serviceName = servicePrefix + i;
            String type = "providers";
            String nacosName = type + ":" + serviceName + "::";

            Instance instance = new Instance();
            instance.setServiceName(nacosName);

            HashMap<String, String> map = new HashMap();
            map.put("side", "provider");
            map.put("methods", "getPermissions2,getPermissions0,getPermissions1");
            map.put("release", "0.0.3-SNAPSHOT");
            map.put("deprecated", "false");
            map.put("dubbo", "2.0.2");
            map.put("threads", "200");
            map.put("pid", "8257");
            map.put("interface", serviceName);
            map.put("generic", "false");
            map.put("revision", "1.0-SNAPSHOT");
            map.put("path", serviceName);
            map.put("protocol", "dubbo");
            map.put("application", "test-provider-xxx");
            map.put("sdk_version", "2.7.2");
            map.put("dynamic", "true");
            map.put("category", "providers");
            map.put("anyhost", "true");
            map.put("timestamp", System.currentTimeMillis() + "");

            instance.setMetadata(map);
            instance.setIp(mockIp);
            instance.setPort(port);
            try {
                naming.registerInstance(nacosName, "DEFAULT_GROUP", instance);
                System.out.println("NacosReg: " + Constant.df.format(new Date()) + " id: " + serviceName + " ip:" + mockIp + ":" + port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //java -jar -Dfrom=0 -Dend=600 -Dport=0 -Dcount=100 -Dsize=100 -Dnacoswrite=yes -DlimitTime=3000 -Xss256k AllProvider.jar
    public static void main(String[] args) throws Exception {

        String from = System.getProperty("from");
        if (from == null || "".equals(from)) {
            from = "0";
        }
        String end = System.getProperty("end");
        if (end == null || "".equals(end)) {
            end = "20";
        }

        //每个服务的实例数
        String count = System.getProperty("count");
        if (count == null || "".equals(count)) {
            count = "5";
        }

        //服务的分页数
        String size = System.getProperty("size");
        if (size == null || "".equals(size)) {
            size = "5";
        }

        String prefix = System.getProperty("prefix");
        if (prefix == null || "".equals(prefix)) {
            prefix = Constant.interfaceLongName;
        }

        //起始端口
        String port = System.getProperty("port");
        if (port == null || "".equals(port)) {
            port = "20880";
        }

        //nacos直接写
        String write = System.getProperty("write");
        if (write == null || "".equals(write)) {
            write = "nacos";
        }

        System.out.println(" from: " + from + " end: " + end + " port: " + port
                + " count: " + count + " write: " + write);

        //服务数
        int fromC = Integer.parseInt(from);
        int endC = Integer.parseInt(end);

        int pageSize = Integer.parseInt(size);

        //起始端口
        int iStart = Integer.parseInt(port);

        //端口个数
        int portCount = Integer.parseInt(count);

        if ("metadata".equals(write)) {
            createMetaData(prefix, fromC, endC);
            return;
        }

        for (int i = 0; i < (endC - fromC) / pageSize; i++) {
            int fromtmp = pageSize * i + fromC;
            int endtmp = fromtmp + pageSize;

            for (int j = 0; j < portCount; j++) {
                int portJ = iStart + j;
                String finalNacoswrite = write;
                String finalPrefix = prefix;

                Constant.executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if ("nacos".equals(finalNacoswrite)) {
                                testNacosReg(finalPrefix, fromtmp, endtmp, portJ);
                            } else if ("zk".equals(finalNacoswrite)) {
                                mockZkReg(finalPrefix, fromtmp, endtmp, portJ);
                            } else if ("consumer".equals(finalNacoswrite)) {
                                mockZkRegConsumer(finalPrefix, fromtmp, endtmp, portJ);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            iStart = iStart + portCount;
        }

        Thread.sleep(1000 * 30000);
    }
}
