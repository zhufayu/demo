package test;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import test.genericMock.DemoGenericServiceImpl;
import test.helper.Constant;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class ZkProvider {

    public static void testZkReg(String servicePrefix, int from, int end, int port) {
        int export = port;
        System.out.println("zk from: " + from + " to: " + end + " port: " + export);

        ApplicationConfig application = new ApplicationConfig();
        application.setName("test-provider-SamplingTaskRequestDemo");
        application.setOwner("test_nacos_sync");

        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("dubbo");
        protocol.setPort(port);
        List<ServiceConfig<GenericService>> list = new ArrayList<>();

        for (int i = from; i < end; i++) {

            ServiceConfig<GenericService> service = new ServiceConfig<GenericService>();
            service.setRegistry(new RegistryConfig(Constant.zkAdd));
            service.setProtocol(protocol);
            service.setApplication(application);
            service.setGeneric("true");

            GenericService genericService = new DemoGenericServiceImpl();
            service.setRef(genericService);

            service.setInterface(servicePrefix + i);
            list.add(service);
        }
        CountDownLatch countDownLatch = new CountDownLatch(list.size());
        for (ServiceConfig<GenericService> service : list) {
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

    //java -jar -Dfrom=100 -Dend=200 -DportStart=0 -Dcount=100 ZkProvider.jar
    public static void main(String[] args) throws Exception {

        String from = System.getProperty("from");
        if (from == null || "".equals(from)) {
            from = "0";
        }

        String end = System.getProperty("end");
        if (end == null || "".equals(end)) {
            end = "1";
        }

        String portStart = System.getProperty("portStart");
        if (portStart == null || "".equals(portStart)) {
            portStart = "20880";
        }

        String count = System.getProperty("count");
        if (count == null || "".equals(count)) {
            count = "1";
        }

        //服务个数
        int fromC = Integer.parseInt(from);
        int endC = Integer.parseInt(end);

        //实例端口
        int iStart = Integer.parseInt(portStart);
        int iCount = Integer.parseInt(count);
        String prefix = Constant.interfaceLongName;

        for (int i = 0; i < iCount; i++) {
            int tmp = i + iStart;
            Constant.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    testZkReg(prefix, fromC, endC, tmp);
                }
            });
        }

        Thread.sleep(1000 * 30000);
    }
}
