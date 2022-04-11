package test.apiconf;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import test.helper.Constant;
import test.service.DemoService1;
import test.service.DemoService1Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static test.helper.Constant.classLongName;

public class ProviderAPI {
    public static void testOneService() {
        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("test-provider-xxx");

        // 连接注册中心配置
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(Constant.zkAdd);
        registry.isZookeeperProtocol();

        // 服务提供者协议配置
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("dubbo");
        protocol.setPort(-1);
        protocol.setThreads(200);

        // 服务提供者暴露服务配置
        ServiceConfig<DemoService1> service = new ServiceConfig<DemoService1>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        service.setApplication(application);
        service.setRegistry(registry); // 多个注册中心可以用setRegistries()
        service.setProtocol(protocol); // 多个协议可以用setProtocols()
        service.setInterface(DemoService1.class);
        service.setRef(new DemoService1Impl());
        service.setVersion("1.0.0");

        // 暴露及注册服务
        service.export();
        System.out.println("export: " + Constant.df.format(new Date()) + " id: " + service.getId());
    }

    public static void testMutilService(int from, int to, String prefix ,int port) throws Exception {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("test-provider-xxx");

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(Constant.zkAdd);
        registry.isZookeeperProtocol();

        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("dubbo");
        protocol.setPort(port);
        protocol.setThreads(200);

        List<ServiceConfig<Object>> list = new ArrayList<>();

        for (int i = from; i < to; i++) {
            ServiceConfig<Object> service = new ServiceConfig<>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏

            String interName = prefix + i;
            Class cInter = Class.forName(interName);

            String className = prefix + "Impl" + i;
            Class cClass = Class.forName(className);

            service.setApplication(application);
            service.setRegistry(registry); // 多个注册中心可以用setRegistries()
            service.setProtocol(protocol); // 多个协议可以用setProtocols()
            //service.setVersion("1.0.0");
            service.setInterface(cInter);

            service.setRef(cClass.newInstance());
            list.add(service);
        }
        System.out.println("begin start: " + Constant.df.format(new Date()));
        CountDownLatch countDownLatch = new CountDownLatch(list.size());
        for (ServiceConfig<Object> service : list) {
            Constant.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    String id = service.getId();
                    service.export();
                    System.out.println("export: " + Constant.df.format(new Date()) + " id: " + id);
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void tesExportAndUnexport(String address, int from, int count, int port, String prefix) throws Exception {
        System.out.println("test: " + address);
        int export = 20880 + port;

        ApplicationConfig application = new ApplicationConfig();
        application.setName("test-provider-xxx");

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(address);
        registry.isZookeeperProtocol();

        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("dubbo");
        protocol.setPort(export);
        protocol.setThreads(200);

        List<ServiceConfig<Object>> list = new ArrayList<>();

        for (int i = from; i < count; i++) {
            ServiceConfig<Object> service = new ServiceConfig<>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏

            String interName = prefix + i;
            Class cInter = Class.forName(interName);

            String className = prefix + "Impl" + i;
            Class cClass = Class.forName(className);

            service.setApplication(application);
            service.setRegistry(registry); // 多个注册中心可以用setRegistries()
            service.setProtocol(protocol); // 多个协议可以用setProtocols()
            //service.setVersion("1.0.0");
            service.setInterface(cInter);

            service.setRef(cClass.newInstance());
            list.add(service);
        }
        System.out.println("begin start: " + Constant.df.format(new Date()) + " port: " + export);

        for (ServiceConfig<Object> service : list) {
            Constant.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    String id = service.getId();
                    service.export();
                    System.out.println("export: " + Constant.df.format(new Date()) + " id: " + id);
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    service.unexport();
                    System.out.println("unexport: " + Constant.df.format(new Date()) + " id: " + id);

                }
            });
        }
    }

    public static void main(String[] args) throws Exception {
//        int port=20880;
//        //生效端口可能不是传入的端口
//        for(int i=0; i<5;i++){
//            testMutilService(0, 5, Constant.interfaceLongName,port +i);
//            Thread.sleep(1000);
//        }

        testOneService();
    }
}
