package test.nacosreg;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import test.helper.Constant;

import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class NacosReg {
    public static void reg() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", Constant.nacosPort);
        properties.setProperty("namespace", "public");
        final NamingService naming = NamingFactory.createNamingService(properties);

        int j = 2;

        String serviceName = "test.service.gen.DemoService1493";
        //String serviceName = Constant.interFullName + finalI;
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
        map.put("version", "1.0.0");
        map.put("module.version", "3.0.0");

        instance.setMetadata(map);

        instance.setIp("10.2.2." + j);
        instance.setPort(j);

        try {
            naming.registerInstance(nacosName, "DEFAULT_GROUP", instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //java -jar -Dfrom=0 -Dend=1000 -Dport=11211 -Dcount=1 -DlimitTime=3000 -Xss256k NacosReg.jar
    public static void test() throws Exception {

        Properties properties = new Properties();
        properties.setProperty("serverAddr", Constant.nacosPort);
        properties.setProperty("namespace", "public");
        final NamingService naming = NamingFactory.createNamingService(properties);


        String from = System.getProperty("from");
        if (from == null || "".equals(from)) {
            from = "0";
        }

        String end = System.getProperty("end");
        if (end == null || "".equals(end)) {
            end = "10";
        }

        String count = System.getProperty("count");
        if (count == null || "".equals(count)) {
            count = "5";
        }

        String port = System.getProperty("port");
        if (port == null || "".equals(port)) {
            port = "20980";
        }

        //服务个数
        int fromC = Integer.parseInt(from);
        int endC = Integer.parseInt(end);

        int portC = Integer.parseInt(port);
        int countC = Integer.parseInt(count);

        for (int i = fromC; i < endC; i++) {
            int finalI = i;

            Constant.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("thread start....");
                    Date start = new Date();

                    for (int j = 0; j < countC; j++) {
                        String serviceName = "org.apache.dubbo.metadata.identifier.TestMetadataIdentifier" + finalI;
                        //String serviceName = Constant.interFullName + finalI;
                        String type = "providers";
                        String nacosName = type + ":" + serviceName + ":1.0.0:group1";

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
                        map.put("version", "1.0.0");
                        map.put("module.version", "3.0.0");

                        instance.setMetadata(map);

                        instance.setIp("10.1.1." + j);
                        instance.setPort(portC + j);

                        try {
                            naming.registerInstance(nacosName, "DEFAULT_GROUP", instance);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Date end = new Date();
                    long cost = end.getTime() - start.getTime();
                    System.out.println("thread-1 this proicess cost:" + cost / 1000 / 60);
                    System.out.println("thread-1 end....");
                }
            });
        }

        Thread.sleep(1000 * 30000);
    }


    public static void main(String[] ages) throws Exception {
        test();
    }
}
