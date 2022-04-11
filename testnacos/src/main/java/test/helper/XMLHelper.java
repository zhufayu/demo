package test.helper;

import java.io.File;

public class XMLHelper {
    public static String createFlag = "zk";

    private static String xmlLoc = "C:\\code\\dsftest\\src\\main\\resources\\gen";

    private static String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "\n" +
            "<beans xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "       xmlns:dubbo=\"http://dubbo.apache.org/schema/dubbo\"\n" +
            "       xmlns=\"http://www.springframework.org/schema/beans\" xmlns:beans=\"http://www.springframework.org/schema/beans\"\n" +
            "       xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd\n" +
            "       http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd\">\n";

    private static String xmlEnd = "</beans>";

    private static String providerApp = "    <dubbo:application name=\"test-provider-xxx\"/>\n";
    private static String consumerApp = "    <dubbo:application name=\"test-consumer-xxx\"/>\n";

    private static String zkProtocol =
            "    <dubbo:registry address=\"zookeeper://10.248.224.74:2181?backup=10.248.224.66:2181,10.248.224.72:2181\" timeout=\"1000000000\"/>\n" +
            "    <dubbo:protocol name=\"dubbo\" port=\"-1\"/>\n";

    private static String nacosProtocol =
            "    <dubbo:registry address=\"nacos://10.248.224.74:8848?backup=10.248.224.66:8848,10.248.224.72:8848\" timeout=\"1000000000\"/>\n" +
            "    <dubbo:protocol name=\"dubbo\" port=\"-1\"/>\n";


    public static void provider() {
        for (int i = 0; i < ClassHelper.interfaceCount; i++) {

            String providerLoc = xmlLoc + "\\provider" + i + ".xml";

            FileHelper.write(providerLoc, xmlHeader, false);
            if (createFlag.contains("zk")) {
                FileHelper.write(providerLoc, zkProtocol, true);
            } else {
                FileHelper.write(providerLoc, nacosProtocol, true);
            }
            FileHelper.write(providerLoc, providerApp, true);

            String tmp =
                    "    <dubbo:service acl=\"true\" interface=\"test.service.gen.DemoService" + i + "\" ref=\"proxyService_" + i + "\" timeout=\"100\"/>\n" +
                            "    <bean id=\"proxyService_" + i + "\" class=\"test.service.gen.DemoServiceImpl" + i + "\" />\n";
            FileHelper.write(providerLoc, tmp, true);

            FileHelper.write(providerLoc, xmlEnd, true);
        }
    }

    public static void consumer() {
        for (int i = 0; i < ClassHelper.interfaceCount; i++) {
            String consumerLoc = xmlLoc + "\\consumer" + i + ".xml";

            FileHelper.write(consumerLoc, xmlHeader, false);

            if (createFlag.contains("zk")) {
                FileHelper.write(consumerLoc, zkProtocol, true);
            } else {
                FileHelper.write(consumerLoc, nacosProtocol, true);
            }

            FileHelper.write(consumerLoc, consumerApp, true);

            String tmp = "    <dubbo:reference id=\"proxyService_" + i + "\" interface=\"test.service.gen.DemoService" + i + "\" timeout=\"3000000\" />\n";
            FileHelper.write(consumerLoc, tmp, true);

            FileHelper.write(consumerLoc, xmlEnd, true);
        }
    }

    public static void createXml() {
        File file = new File(xmlLoc);
        FileHelper.dealDir(file);
        provider();
        consumer();
    }
}
